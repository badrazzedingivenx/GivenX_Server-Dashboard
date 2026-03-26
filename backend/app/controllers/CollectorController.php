<?php
/*
Rôle du fichier :
Ce fichier reçoit les métriques serveur envoyées par le collecteur Linux.

Ce qu’il contient :
- la vérification du token du collecteur
- l’enregistrement des métriques serveur
- la mise à jour du last_seen_at du serveur
- le déclenchement automatique des alertes CPU/RAM

Pourquoi il existe :
Le backend a besoin d’un point d’entrée sécurisé pour les agents de collecte.

Comment il s’intègre dans le projet :
Le collecteur Linux appelle ce contrôleur via HTTP POST.
*/

class CollectorController
{
    private function getCollectorToken(): ?string
    {
        $header = $_SERVER['HTTP_AUTHORIZATION'] ?? $_SERVER['REDIRECT_HTTP_AUTHORIZATION'] ?? null;

        if (!$header && function_exists('getallheaders')) {
            $headers = getallheaders();
            $header = $headers['Authorization'] ?? $headers['authorization'] ?? null;
        }

        if (!$header) {
            return null;
        }

        if (preg_match('/Bearer\s+(.+)/i', $header, $matches)) {
            return trim($matches[1]);
        }

        return null;
    }

    private function authenticateCollector(int $serverId): bool
    {
        $token = $this->getCollectorToken();

        if (!$token) {
            return false;
        }

        $tokenHash = hash('sha256', $token);
        $pdo = Database::getConnection();

        $stmt = $pdo->prepare("
            SELECT id
            FROM collector_tokens
            WHERE server_id = :server_id
              AND token_hash = :token_hash
              AND revoked_at IS NULL
              AND (expires_at IS NULL OR expires_at > NOW())
            LIMIT 1
        ");

        $stmt->execute([
            'server_id' => $serverId,
            'token_hash' => $tokenHash
        ]);

        return (bool) $stmt->fetch();
    }

    public function storeServerMetrics(array $params = []): void
    {
        $input = Request::input();

        $serverId = isset($input['server_id']) ? (int) $input['server_id'] : 0;
        $metrics = $input['metrics'] ?? [];

        if ($serverId <= 0) {
            Response::json([
                'success' => false,
                'message' => 'Le champ server_id est obligatoire'
            ], 422);
        }

        if (!$this->authenticateCollector($serverId)) {
            Response::json([
                'success' => false,
                'message' => 'Token collecteur invalide ou absent'
            ], 401);
        }

        $cpuUsage = isset($metrics['cpu']) ? (float) $metrics['cpu'] : 0.0;
        $ramUsage = isset($metrics['ram']['usage_percent']) ? (float) $metrics['ram']['usage_percent'] : 0.0;
        $diskUsage = isset($metrics['disk']['usage_percent']) ? (float) $metrics['disk']['usage_percent'] : 0.0;
        $networkIn = isset($metrics['network']['bytes_in']) ? (float) $metrics['network']['bytes_in'] : 0.0;
        $networkOut = isset($metrics['network']['bytes_out']) ? (float) $metrics['network']['bytes_out'] : 0.0;
        $temperature = array_key_exists('temperature', $metrics) && $metrics['temperature'] !== null
            ? (float) $metrics['temperature']
            : null;
        $processCount = isset($metrics['process_count']) ? (int) $metrics['process_count'] : 0;
        $collectedAt = !empty($metrics['collected_at']) ? $metrics['collected_at'] : date('Y-m-d H:i:s');

        $pdo = Database::getConnection();

        $serverStmt = $pdo->prepare("
            SELECT id
            FROM servers
            WHERE id = :id
            LIMIT 1
        ");

        $serverStmt->execute([
            'id' => $serverId
        ]);

        if (!$serverStmt->fetch()) {
            Response::json([
                'success' => false,
                'message' => 'Serveur introuvable'
            ], 404);
        }

        $stmt = $pdo->prepare("
            INSERT INTO server_metrics (
                server_id,
                cpu_usage,
                ram_usage,
                disk_usage,
                network_in,
                network_out,
                temperature,
                process_count,
                collected_at
            ) VALUES (
                :server_id,
                :cpu_usage,
                :ram_usage,
                :disk_usage,
                :network_in,
                :network_out,
                :temperature,
                :process_count,
                :collected_at
            )
        ");

        $stmt->execute([
            'server_id' => $serverId,
            'cpu_usage' => $cpuUsage,
            'ram_usage' => $ramUsage,
            'disk_usage' => $diskUsage,
            'network_in' => $networkIn,
            'network_out' => $networkOut,
            'temperature' => $temperature,
            'process_count' => $processCount,
            'collected_at' => $collectedAt
        ]);

        $updateServer = $pdo->prepare("
            UPDATE servers
            SET last_seen_at = NOW()
            WHERE id = :id
        ");
        $updateServer->execute(['id' => $serverId]);

        AlertEngine::evaluateServerMetric($serverId, [
            'cpu_usage' => $cpuUsage,
            'ram_usage' => $ramUsage
        ]);

        Response::json([
            'success' => true,
            'message' => 'Métriques serveur enregistrées avec succès',
            'data' => [
                'server_id' => $serverId,
                'metric_id' => (int) $pdo->lastInsertId()
            ]
        ], 201);
    }
}
