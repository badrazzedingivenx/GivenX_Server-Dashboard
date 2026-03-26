<?php
/*
Rôle du fichier :
Ce fichier reçoit les métriques des projets envoyées par le collecteur Linux.

Ce qu’il contient :
- la vérification du token du collecteur
- l’enregistrement des métriques projet
- le déclenchement automatique des alertes projet arrêté

Pourquoi il existe :
Le projet doit surveiller les applications en plus des métriques globales serveur.

Comment il s’intègre dans le projet :
Le collecteur Linux appelle cet endpoint via HTTP POST.
*/

class CollectorProjectController
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

    public function storeProjectMetrics(array $params = []): void
    {
        $input = Request::input();

        $projectId = isset($input['project_id']) ? (int) $input['project_id'] : 0;
        $serverId = isset($input['server_id']) ? (int) $input['server_id'] : 0;
        $metrics = $input['metrics'] ?? [];

        if ($projectId <= 0 || $serverId <= 0) {
            Response::json([
                'success' => false,
                'message' => 'Les champs project_id et server_id sont obligatoires'
            ], 422);
        }

        if (!$this->authenticateCollector($serverId)) {
            Response::json([
                'success' => false,
                'message' => 'Token collecteur invalide ou absent'
            ], 401);
        }

        $pdo = Database::getConnection();

        $projectStmt = $pdo->prepare("
            SELECT id, server_id
            FROM projects
            WHERE id = :project_id AND server_id = :server_id
            LIMIT 1
        ");

        $projectStmt->execute([
            'project_id' => $projectId,
            'server_id' => $serverId
        ]);

        if (!$projectStmt->fetch()) {
            Response::json([
                'success' => false,
                'message' => 'Projet introuvable pour ce serveur'
            ], 404);
        }

        $cpuUsage = isset($metrics['cpu_usage']) ? (float) $metrics['cpu_usage'] : 0.0;
        $ramUsage = isset($metrics['ram_usage_mb']) ? (float) $metrics['ram_usage_mb'] : 0.0;
        $status = !empty($metrics['status']) ? $metrics['status'] : 'unknown';
        $pid = isset($metrics['pid']) ? (int) $metrics['pid'] : null;
        $portDetected = isset($metrics['port_detected']) ? (int) $metrics['port_detected'] : null;
        $collectedAt = !empty($metrics['collected_at']) ? $metrics['collected_at'] : date('Y-m-d H:i:s');

        $allowedStatuses = ['running', 'stopped', 'warning', 'unknown'];
        if (!in_array($status, $allowedStatuses, true)) {
            $status = 'unknown';
        }

        $stmt = $pdo->prepare("
            INSERT INTO project_metrics (
                project_id,
                cpu_usage,
                ram_usage,
                status,
                pid,
                port_detected,
                collected_at
            ) VALUES (
                :project_id,
                :cpu_usage,
                :ram_usage,
                :status,
                :pid,
                :port_detected,
                :collected_at
            )
        ");

        $stmt->bindValue(':project_id', $projectId, PDO::PARAM_INT);
        $stmt->bindValue(':cpu_usage', $cpuUsage);
        $stmt->bindValue(':ram_usage', $ramUsage);
        $stmt->bindValue(':status', $status, PDO::PARAM_STR);
        $stmt->bindValue(':pid', $pid, $pid === null ? PDO::PARAM_NULL : PDO::PARAM_INT);
        $stmt->bindValue(':port_detected', $portDetected, $portDetected === null ? PDO::PARAM_NULL : PDO::PARAM_INT);
        $stmt->bindValue(':collected_at', $collectedAt, PDO::PARAM_STR);
        $stmt->execute();

        AlertEngine::evaluateProjectMetric($projectId, [
            'status' => $status
        ]);

        Response::json([
            'success' => true,
            'message' => 'Métriques projet enregistrées avec succès',
            'data' => [
                'project_id' => $projectId,
                'metric_id' => (int) $pdo->lastInsertId()
            ]
        ], 201);
    }
}
