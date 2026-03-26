<?php
/*
Rôle du fichier :
Ce contrôleur permet de consulter les logs système et les logs utilisateurs.

Ce qu’il contient :
- récupération des logs système
- récupération des logs d’actions utilisateur

Pourquoi il existe :
Le cahier des charges demande une page de consultation des logs.

Comment il s’intègre dans le projet :
Le frontend React appelle ces endpoints pour afficher l’historique des actions
et des événements techniques.
*/

class LogController
{
    public function systemLogs(array $params = []): void
    {
        $user = Auth::requireAuth();

        if (($user['role'] ?? 'user') !== 'admin') {
            Response::json([
                'success' => false,
                'message' => 'Accès administrateur requis'
            ], 403);
            return;
        }

        $pdo = Database::getConnection();

        $stmt = $pdo->query("
            SELECT *
            FROM system_logs
            ORDER BY created_at DESC
            LIMIT 100
        ");

        $logs = $stmt->fetchAll(PDO::FETCH_ASSOC);

        Response::json([
            'success' => true,
            'message' => 'Logs système récupérés',
            'data' => [
                'logs' => $logs
            ]
        ]);
    }

    public function userLogs(array $params = []): void
    {
        $user = Auth::requireAuth();

        if (($user['role'] ?? 'user') !== 'admin') {
            Response::json([
                'success' => false,
                'message' => 'Accès administrateur requis'
            ], 403);
            return;
        }

        $pdo = Database::getConnection();

        $stmt = $pdo->query("
            SELECT activity_logs.*, users.name
            FROM activity_logs
            JOIN users ON users.id = activity_logs.user_id
            ORDER BY activity_logs.created_at DESC
            LIMIT 100
        ");

        $logs = $stmt->fetchAll(PDO::FETCH_ASSOC);

        Response::json([
            'success' => true,
            'message' => 'Logs utilisateur récupérés',
            'data' => [
                'logs' => $logs
            ]
        ]);
    }

    public function myLogs(array $params = []): void
    {
        $user = Auth::requireAuth();
        $pdo = Database::getConnection();

        $stmt = $pdo->prepare("
            SELECT activity_logs.*, users.name
            FROM activity_logs
            JOIN users ON users.id = activity_logs.user_id
            WHERE activity_logs.user_id = ?
            ORDER BY activity_logs.created_at DESC
            LIMIT 100
        ");

        $stmt->execute([$user['id']]);
        $logs = $stmt->fetchAll(PDO::FETCH_ASSOC);

        Response::json([
            'success' => true,
            'message' => 'Mes logs récupérés',
            'data' => [
                'logs' => $logs
            ]
        ]);
    }
}
