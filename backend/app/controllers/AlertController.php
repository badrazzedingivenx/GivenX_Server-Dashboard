<?php
/*
Rôle du fichier :
Ce fichier contient le contrôleur des alertes.

Ce qu’il contient :
- la liste des alertes de l’utilisateur connecté
- une génération minimale d’alertes à partir des dernières métriques

Pourquoi il existe :
Le cahier des charges exige des alertes automatiques.

Comment il s’intègre dans le projet :
Le backend peut générer des alertes simples et le frontend peut les afficher.
*/


class AlertController
{
    private PDO $db;

    public function __construct()
    {
        $this->db = Database::getConnection();
    }

    public function index(array $params = []): void
    {
        $user = Auth::requireAuth();

        if (($user['role'] ?? 'user') === 'admin') {
            $stmt = $this->db->query("
                SELECT *
                FROM alerts
                ORDER BY created_at DESC
                LIMIT 100
            ");
            $alerts = $stmt->fetchAll(PDO::FETCH_ASSOC);
        } else {
            $stmt = $this->db->prepare("
                SELECT *
                FROM alerts
                WHERE user_id = ?
                ORDER BY created_at DESC
                LIMIT 100
            ");
            $stmt->execute([$user['id']]);
            $alerts = $stmt->fetchAll(PDO::FETCH_ASSOC);
        }

        Response::json([
            "success" => true,
            "message" => "Liste des alertes récupérée",
            "data" => [
                "alerts" => $alerts
            ]
        ]);
    }

    public function generate(array $params = []): void
    {
        $user = Auth::requireAuth();
        $input = Request::input();

        $type = trim($input['type'] ?? '');
        $message = trim($input['message'] ?? '');
        $severity = trim($input['severity'] ?? 'warning');
        $serverId = !empty($input['server_id']) ? (int) $input['server_id'] : null;
        $projectId = !empty($input['project_id']) ? (int) $input['project_id'] : null;

        if ($type === '' || $message === '') {
            Response::json([
                "success" => false,
                "message" => "Le type et le message sont obligatoires"
            ], 422);
        }

        $stmt = $this->db->prepare("
            INSERT INTO alerts (
                user_id,
                server_id,
                project_id,
                type,
                severity,
                message,
                status,
                created_at
            )
            VALUES (?, ?, ?, ?, ?, ?, 'open', NOW())
        ");

        $stmt->execute([
            (int) $user['id'],
            $serverId,
            $projectId,
            $type,
            $severity,
            $message
        ]);

        $alertId = (int) $this->db->lastInsertId();

        $alert = [
            'id' => $alertId,
            'user_id' => (int) $user['id'],
            'type' => $type,
            'message' => $message,
            'severity' => $severity,
            'server_id' => $serverId,
            'project_id' => $projectId,
            'status' => 'open',
        ];

        UserActionLogger::log((int) $user['id'], 'alert_generated', 'Alerte générée : ' . $type);
        SystemLogService::log('warning', 'Nouvelle alerte générée : ' . $message);

        if (strtolower($severity) === 'critical') {
            $this->sendAlertEmails($alert);
        }

        Response::json([
            "success" => true,
            "message" => "Alerte générée avec succès",
            "data" => [
                "alert_id" => $alertId
            ]
        ], 201);
    }

    public function resolve(array $params): void
    {
        $user = Auth::requireAuth();
        $id = (int) ($params['id'] ?? 0);

        $checkStmt = $this->db->prepare("
            SELECT *
            FROM alerts
            WHERE id = ?
            LIMIT 1
        ");
        $checkStmt->execute([$id]);
        $alert = $checkStmt->fetch(PDO::FETCH_ASSOC);

        if (!$alert) {
            Response::json([
                "success" => false,
                "message" => "Alerte introuvable"
            ], 404);
        }

        if (($user['role'] ?? 'user') !== 'admin' && (int) $alert['user_id'] !== (int) $user['id']) {
            Response::json([
                "success" => false,
                "message" => "Accès non autorisé"
            ], 403);
        }

        $stmt = $this->db->prepare("
            UPDATE alerts
            SET status = 'resolved',
                resolved_at = NOW()
            WHERE id = ?
        ");

        $stmt->execute([$id]);

        UserActionLogger::log((int) $user['id'], 'alert_resolved', 'Alerte résolue ID : ' . $id);
        SystemLogService::log('info', 'Alerte résolue ID : ' . $id);

        Response::json([
            "success" => true,
            "message" => "Alerte résolue avec succès"
        ]);
    }

    private function sendAlertEmails(array $alert): void
    {
        try {
            $stmt = $this->db->query("
                SELECT email
                FROM users
                WHERE status = 'active'
            ");

            $users = $stmt->fetchAll(PDO::FETCH_ASSOC);

            foreach ($users as $user) {
                $email = trim($user['email'] ?? '');

                if ($email === '') {
                    continue;
                }

                $sent = EmailService::sendAlertEmail($email, $alert);

                if ($sent) {
                    SystemLogService::log('info', 'Email d’alerte envoyé à ' . $email);
                } else {
                    SystemLogService::log('error', 'Échec envoi email d’alerte à ' . $email);
                }
            }
        } catch (Throwable $e) {
            error_log('[EMAIL ALERT ERROR] ' . $e->getMessage());
            SystemLogService::log('error', 'Erreur envoi emails alertes');
        }
    }
}
