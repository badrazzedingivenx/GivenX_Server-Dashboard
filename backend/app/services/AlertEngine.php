<?php

/*
Rôle du fichier :
Ce service centralise toute la logique des alertes automatiques.

Ce qu’il contient :
- la lecture des règles d’alertes personnalisables
- la création d’alertes CPU élevé
- la création d’alertes RAM élevée
- la création d’alertes projet arrêté
- la détection serveur indisponible
- la résolution automatique de certaines alertes

Pourquoi il existe :
Le projet doit déclencher des alertes de manière automatique et cohérente
sans dupliquer la logique dans plusieurs contrôleurs.

Comment il s’intègre dans le projet :
Les contrôleurs de collecte appellent ce service après chaque insertion
de métrique, et un script périodique peut l’utiliser pour vérifier
les serveurs indisponibles.
*/

class AlertEngine
{
    public static function evaluateServerMetric(int $serverId, array $metric): void
    {
        $pdo = Database::getConnection();

        $serverStmt = $pdo->prepare("
            SELECT s.id, s.name, s.user_id
            FROM servers s
            WHERE s.id = :id
            LIMIT 1
        ");
        $serverStmt->execute(['id' => $serverId]);
        $server = $serverStmt->fetch(PDO::FETCH_ASSOC);

        if (!$server) {
            return;
        }

        $cpuRule = self::getRule($server['user_id'], 'cpu_high', $serverId, null, 85, 'warning', 10);
        $ramRule = self::getRule($server['user_id'], 'ram_high', $serverId, null, 85, 'warning', 10);

        $cpuUsage = isset($metric['cpu_usage']) ? (float)$metric['cpu_usage'] : 0.0;
        $ramUsage = isset($metric['ram_usage']) ? (float)$metric['ram_usage'] : 0.0;

        if ($cpuUsage >= (float)$cpuRule['threshold_value']) {
            self::createAlertIfNeeded(
                $server['user_id'],
                $serverId,
                null,
                'cpu_high',
                $cpuRule['severity'],
                "CPU élevé sur le serveur {$server['name']} : {$cpuUsage}%",
                (int)$cpuRule['cooldown_minutes']
            );
        } else {
            self::resolveOpenAlerts($server['user_id'], $serverId, null, 'cpu_high');
        }

        if ($ramUsage >= (float)$ramRule['threshold_value']) {
            self::createAlertIfNeeded(
                $server['user_id'],
                $serverId,
                null,
                'ram_high',
                $ramRule['severity'],
                "RAM élevée sur le serveur {$server['name']} : {$ramUsage}%",
                (int)$ramRule['cooldown_minutes']
            );
        } else {
            self::resolveOpenAlerts($server['user_id'], $serverId, null, 'ram_high');
        }

        self::resolveOpenAlerts($server['user_id'], $serverId, null, 'server_down');
    }

    public static function evaluateProjectMetric(int $projectId, array $metric): void
    {
        $pdo = Database::getConnection();

        $projectStmt = $pdo->prepare("
            SELECT p.id, p.name, p.user_id, p.server_id
            FROM projects p
            WHERE p.id = :id
            LIMIT 1
        ");
        $projectStmt->execute(['id' => $projectId]);
        $project = $projectStmt->fetch(PDO::FETCH_ASSOC);

        if (!$project) {
            return;
        }

        $rule = self::getRule($project['user_id'], 'process_stopped', null, $projectId, null, 'critical', 10);

        $status = $metric['status'] ?? 'unknown';

        if ($status === 'stopped') {
            self::createAlertIfNeeded(
                $project['user_id'],
                null,
                $projectId,
                'process_stopped',
                $rule['severity'],
                "Le projet {$project['name']} est arrêté",
                (int)$rule['cooldown_minutes']
            );
        } else {
            self::resolveOpenAlerts($project['user_id'], null, $projectId, 'process_stopped');
        }
    }

    public static function checkServerDownAlerts(): void
    {
        $pdo = Database::getConnection();

        $servers = $pdo->query("
            SELECT id, user_id, name, last_seen_at
            FROM servers
            WHERE is_active = 1
        ")->fetchAll(PDO::FETCH_ASSOC);

        foreach ($servers as $server) {
            $rule = self::getRule((int)$server['user_id'], 'server_down', (int)$server['id'], null, 30, 'critical', 10);

            $timeoutSeconds = (int)$rule['threshold_value'];
            if ($timeoutSeconds <= 0) {
                $timeoutSeconds = 30;
            }

            $lastSeenAt = $server['last_seen_at'];

            if (!$lastSeenAt || (time() - strtotime($lastSeenAt)) > $timeoutSeconds) {
                self::createAlertIfNeeded(
                    (int)$server['user_id'],
                    (int)$server['id'],
                    null,
                    'server_down',
                    $rule['severity'],
                    "Le serveur {$server['name']} est indisponible",
                    (int)$rule['cooldown_minutes']
                );
            } else {
                self::resolveOpenAlerts((int)$server['user_id'], (int)$server['id'], null, 'server_down');
            }
        }
    }

    private static function getRule(
        int $userId,
        string $ruleType,
        ?int $serverId,
        ?int $projectId,
        $defaultThreshold,
        string $defaultSeverity,
        int $defaultCooldown
    ): array {
        $pdo = Database::getConnection();

        $stmt = $pdo->prepare("
            SELECT *
            FROM alert_rules
            WHERE user_id = :user_id
              AND rule_type = :rule_type
              AND is_active = 1
              AND (
                    (server_id = :server_id)
                 OR (project_id = :project_id)
                 OR (server_id IS NULL AND project_id IS NULL)
              )
            ORDER BY
                CASE
                    WHEN server_id = :server_id THEN 1
                    WHEN project_id = :project_id THEN 2
                    ELSE 3
                END
            LIMIT 1
        ");

        $stmt->execute([
            'user_id' => $userId,
            'rule_type' => $ruleType,
            'server_id' => $serverId,
            'project_id' => $projectId
        ]);

        $rule = $stmt->fetch(PDO::FETCH_ASSOC);

        if ($rule) {
            return $rule;
        }

        return [
            'threshold_value' => $defaultThreshold,
            'severity' => $defaultSeverity,
            'cooldown_minutes' => $defaultCooldown
        ];
    }

    private static function createAlertIfNeeded(
        int $userId,
        ?int $serverId,
        ?int $projectId,
        string $type,
        string $severity,
        string $message,
        int $cooldownMinutes
    ): void {
        $pdo = Database::getConnection();

        $stmt = $pdo->prepare("
            SELECT id, created_at
            FROM alerts
            WHERE user_id = :user_id
              AND type = :type
              AND status = 'open'
              AND (
                    (server_id <=> :server_id)
                AND (project_id <=> :project_id)
              )
            ORDER BY created_at DESC
            LIMIT 1
        ");

        $stmt->execute([
            'user_id' => $userId,
            'type' => $type,
            'server_id' => $serverId,
            'project_id' => $projectId
        ]);

        $existing = $stmt->fetch(PDO::FETCH_ASSOC);

        if ($existing) {
            return;
        }

        $recentStmt = $pdo->prepare("
            SELECT id
            FROM alerts
            WHERE user_id = :user_id
              AND type = :type
              AND (
                    (server_id <=> :server_id)
                AND (project_id <=> :project_id)
              )
              AND created_at >= DATE_SUB(NOW(), INTERVAL :cooldown MINUTE)
            LIMIT 1
        ");

        $recentStmt->bindValue(':user_id', $userId, PDO::PARAM_INT);
        $recentStmt->bindValue(':type', $type, PDO::PARAM_STR);
        $recentStmt->bindValue(':server_id', $serverId, $serverId === null ? PDO::PARAM_NULL : PDO::PARAM_INT);
        $recentStmt->bindValue(':project_id', $projectId, $projectId === null ? PDO::PARAM_NULL : PDO::PARAM_INT);
        $recentStmt->bindValue(':cooldown', $cooldownMinutes, PDO::PARAM_INT);
        $recentStmt->execute();

        if ($recentStmt->fetch(PDO::FETCH_ASSOC)) {
            return;
        }

        $insert = $pdo->prepare("
            INSERT INTO alerts (
                user_id,
                server_id,
                project_id,
                type,
                severity,
                message,
                status,
                created_at
            ) VALUES (
                :user_id,
                :server_id,
                :project_id,
                :type,
                :severity,
                :message,
                'open',
                NOW()
            )
        ");

        $insert->bindValue(':user_id', $userId, PDO::PARAM_INT);
        $insert->bindValue(':server_id', $serverId, $serverId === null ? PDO::PARAM_NULL : PDO::PARAM_INT);
        $insert->bindValue(':project_id', $projectId, $projectId === null ? PDO::PARAM_NULL : PDO::PARAM_INT);
        $insert->bindValue(':type', $type, PDO::PARAM_STR);
        $insert->bindValue(':severity', $severity, PDO::PARAM_STR);
        $insert->bindValue(':message', $message, PDO::PARAM_STR);
        $insert->execute();
    }

    private static function resolveOpenAlerts(int $userId, ?int $serverId, ?int $projectId, string $type): void
    {
        $pdo = Database::getConnection();

        $stmt = $pdo->prepare("
            UPDATE alerts
            SET status = 'resolved',
                resolved_at = NOW()
            WHERE user_id = :user_id
              AND type = :type
              AND status = 'open'
              AND (
                    (server_id <=> :server_id)
                AND (project_id <=> :project_id)
              )
        ");

        $stmt->bindValue(':user_id', $userId, PDO::PARAM_INT);
        $stmt->bindValue(':type', $type, PDO::PARAM_STR);
        $stmt->bindValue(':server_id', $serverId, $serverId === null ? PDO::PARAM_NULL : PDO::PARAM_INT);
        $stmt->bindValue(':project_id', $projectId, $projectId === null ? PDO::PARAM_NULL : PDO::PARAM_INT);
        $stmt->execute();
    }
}
