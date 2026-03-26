<?php
/*
Rôle du fichier :
Ce fichier contient le contrôleur de consultation des métriques historiques.

Ce qu’il contient :
- l’historique des métriques serveur
- l’historique des métriques projet
- les dernières métriques serveur
- les dernières métriques projet

Pourquoi il existe :
Le dashboard doit pouvoir afficher des courbes et des valeurs récentes.

Comment il s’intègre dans le projet :
Le frontend appellera ces endpoints pour afficher les graphiques et les cartes de monitoring.
*/

class MetricController
{
    public function serverHistory(array $params): void
    {
        $user = Auth::requireAuth();
        $serverId = isset($params['id']) ? (int) $params['id'] : 0;
        $limit = (int) Request::query('limit', 20);

        if ($serverId <= 0) {
            Response::json([
                'success' => false,
                'message' => 'Identifiant serveur invalide'
            ], 422);
        }

        if ($limit <= 0 || $limit > 500) {
            $limit = 20;
        }

        $pdo = Database::getConnection();

        $serverStmt = $pdo->prepare("
            SELECT id, name
            FROM servers
            WHERE id = :id AND user_id = :user_id
            LIMIT 1
        ");

        $serverStmt->execute([
            'id' => $serverId,
            'user_id' => $user['id']
        ]);

        $server = $serverStmt->fetch();

        if (!$server) {
            Response::json([
                'success' => false,
                'message' => 'Serveur introuvable ou non autorisé'
            ], 404);
        }

        $stmt = $pdo->prepare("
            SELECT
                id,
                server_id,
                cpu_usage,
                ram_usage,
                disk_usage,
                network_in,
                network_out,
                temperature,
                process_count,
                collected_at
            FROM server_metrics
            WHERE server_id = :server_id
            ORDER BY collected_at DESC
            LIMIT {$limit}
        ");

        $stmt->execute([
            'server_id' => $serverId
        ]);

        $metrics = $stmt->fetchAll();

        Response::json([
            'success' => true,
            'message' => 'Historique serveur récupéré avec succès',
            'data' => [
                'server' => $server,
                'metrics' => array_reverse($metrics)
            ]
        ]);
    }

    public function serverLatest(array $params): void
    {
        $user = Auth::requireAuth();
        $serverId = isset($params['id']) ? (int) $params['id'] : 0;

        if ($serverId <= 0) {
            Response::json([
                'success' => false,
                'message' => 'Identifiant serveur invalide'
            ], 422);
        }

        $pdo = Database::getConnection();

        $serverStmt = $pdo->prepare("
            SELECT id, name
            FROM servers
            WHERE id = :id AND user_id = :user_id
            LIMIT 1
        ");

        $serverStmt->execute([
            'id' => $serverId,
            'user_id' => $user['id']
        ]);

        $server = $serverStmt->fetch();

        if (!$server) {
            Response::json([
                'success' => false,
                'message' => 'Serveur introuvable ou non autorisé'
            ], 404);
        }

        $stmt = $pdo->prepare("
            SELECT
                id,
                server_id,
                cpu_usage,
                ram_usage,
                disk_usage,
                network_in,
                network_out,
                temperature,
                process_count,
                collected_at
            FROM server_metrics
            WHERE server_id = :server_id
            ORDER BY collected_at DESC
            LIMIT 1
        ");

        $stmt->execute([
            'server_id' => $serverId
        ]);

        $metric = $stmt->fetch();

        Response::json([
            'success' => true,
            'message' => 'Dernière métrique serveur récupérée avec succès',
            'data' => [
                'server' => $server,
                'metric' => $metric
            ]
        ]);
    }

    public function projectHistory(array $params): void
    {
        $user = Auth::requireAuth();
        $projectId = isset($params['id']) ? (int) $params['id'] : 0;
        $limit = (int) Request::query('limit', 20);

        if ($projectId <= 0) {
            Response::json([
                'success' => false,
                'message' => 'Identifiant projet invalide'
            ], 422);
        }

        if ($limit <= 0 || $limit > 500) {
            $limit = 20;
        }

        $pdo = Database::getConnection();

        $projectStmt = $pdo->prepare("
            SELECT id, name, server_id
            FROM projects
            WHERE id = :id AND user_id = :user_id
            LIMIT 1
        ");

        $projectStmt->execute([
            'id' => $projectId,
            'user_id' => $user['id']
        ]);

        $project = $projectStmt->fetch();

        if (!$project) {
            Response::json([
                'success' => false,
                'message' => 'Projet introuvable ou non autorisé'
            ], 404);
        }

        $stmt = $pdo->prepare("
            SELECT
                id,
                project_id,
                cpu_usage,
                ram_usage,
                status,
                pid,
                port_detected,
                collected_at
            FROM project_metrics
            WHERE project_id = :project_id
            ORDER BY collected_at DESC
            LIMIT {$limit}
        ");

        $stmt->execute([
            'project_id' => $projectId
        ]);

        $metrics = $stmt->fetchAll();

        Response::json([
            'success' => true,
            'message' => 'Historique projet récupéré avec succès',
            'data' => [
                'project' => $project,
                'metrics' => array_reverse($metrics)
            ]
        ]);
    }

    public function projectLatest(array $params): void
    {
        $user = Auth::requireAuth();
        $projectId = isset($params['id']) ? (int) $params['id'] : 0;

        if ($projectId <= 0) {
            Response::json([
                'success' => false,
                'message' => 'Identifiant projet invalide'
            ], 422);
        }

        $pdo = Database::getConnection();

        $projectStmt = $pdo->prepare("
            SELECT id, name, server_id
            FROM projects
            WHERE id = :id AND user_id = :user_id
            LIMIT 1
        ");

        $projectStmt->execute([
            'id' => $projectId,
            'user_id' => $user['id']
        ]);

        $project = $projectStmt->fetch();

        if (!$project) {
            Response::json([
                'success' => false,
                'message' => 'Projet introuvable ou non autorisé'
            ], 404);
        }

        $stmt = $pdo->prepare("
            SELECT
                id,
                project_id,
                cpu_usage,
                ram_usage,
                status,
                pid,
                port_detected,
                collected_at
            FROM project_metrics
            WHERE project_id = :project_id
            ORDER BY collected_at DESC
            LIMIT 1
        ");

        $stmt->execute([
            'project_id' => $projectId
        ]);

        $metric = $stmt->fetch();

        Response::json([
            'success' => true,
            'message' => 'Dernière métrique projet récupérée avec succès',
            'data' => [
                'project' => $project,
                'metric' => $metric
            ]
        ]);
    }
}
