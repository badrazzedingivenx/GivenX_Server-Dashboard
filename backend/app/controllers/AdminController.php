<?php

/*
Rôle du fichier :
Ce contrôleur gère les fonctionnalités réservées à l’administrateur.

Ce qu’il contient :
- le dashboard administrateur avec statistiques globales
- la liste des utilisateurs
- le blocage d’un utilisateur
- la suppression d’un utilisateur

Pourquoi il existe :
Le cahier des charges demande un espace administrateur distinct permettant
de superviser l’ensemble de la plateforme SaaS.

Comment il s’intègre dans le projet :
Il expose des endpoints API accessibles uniquement aux utilisateurs ayant
le rôle admin.
*/

class AdminController
{
    public function dashboard(array $params = []): void
    {
        try {
            $pdo = Database::getConnection();

            $users = $pdo->query("SELECT COUNT(*) FROM users")->fetchColumn();
            $servers = $pdo->query("SELECT COUNT(*) FROM servers")->fetchColumn();
            $projects = $pdo->query("SELECT COUNT(*) FROM projects")->fetchColumn();
            $alerts = $pdo->query("SELECT COUNT(*) FROM alerts")->fetchColumn();

            Response::json([
                "success" => true,
                "message" => "Statistiques administrateur récupérées",
                "data" => [
                    "users_count" => (int) $users,
                    "servers_count" => (int) $servers,
                    "projects_count" => (int) $projects,
                    "alerts_count" => (int) $alerts
                ]
            ]);
        } catch (Exception $e) {
            Logger::system('error', 'admin.dashboard', 'Erreur dashboard admin', $e->getMessage());

            Response::json([
                "success" => false,
                "message" => "Erreur récupération dashboard admin",
                "error" => $e->getMessage()
            ], 500);
        }
    }

    public function users(array $params = []): void
    {
        try {
            $pdo = Database::getConnection();

            $stmt = $pdo->query("
                SELECT id, name, email, role, status, created_at
                FROM users
                ORDER BY id DESC
            ");

            $users = $stmt->fetchAll(PDO::FETCH_ASSOC);

            Response::json([
                "success" => true,
                "message" => "Liste des utilisateurs récupérée",
                "data" => [
                    "users" => $users
                ]
            ]);
        } catch (Exception $e) {
            Logger::system('error', 'admin.users', 'Erreur récupération utilisateurs', $e->getMessage());

            Response::json([
                "success" => false,
                "message" => "Erreur récupération utilisateurs",
                "error" => $e->getMessage()
            ], 500);
        }
    }

    public function blockUser(array $params): void
    {
        try {
            $admin = Auth::requireAuth();
            $pdo = Database::getConnection();
            $id = isset($params['id']) ? (int) $params['id'] : 0;

            if ($id <= 0) {
                Response::json([
                    "success" => false,
                    "message" => "Identifiant utilisateur invalide"
                ], 422);
            }

            $stmt = $pdo->prepare("
                UPDATE users
                SET status = 'blocked'
                WHERE id = :id
            ");

            $stmt->execute([
                'id' => $id
            ]);

            Logger::activity(
                (int) $admin['id'],
                'block_user',
                'user',
                $id,
                "Blocage de l'utilisateur {$id}"
            );

            Response::json([
                "success" => true,
                "message" => "Utilisateur bloqué"
            ]);
        } catch (Exception $e) {
            Logger::system('error', 'admin.blockUser', 'Erreur blocage utilisateur', $e->getMessage());

            Response::json([
                "success" => false,
                "message" => "Erreur blocage utilisateur",
                "error" => $e->getMessage()
            ], 500);
        }
    }

    public function deleteUser(array $params): void
    {
        try {
            $admin = Auth::requireAuth();
            $pdo = Database::getConnection();
            $id = isset($params['id']) ? (int) $params['id'] : 0;

            if ($id <= 0) {
                Response::json([
                    "success" => false,
                    "message" => "Identifiant utilisateur invalide"
                ], 422);
            }

            $stmt = $pdo->prepare("
                DELETE FROM users
                WHERE id = :id
            ");

            $stmt->execute([
                'id' => $id
            ]);

            Logger::activity(
                (int) $admin['id'],
                'delete_user',
                'user',
                $id,
                "Suppression de l'utilisateur {$id}"
            );

            Response::json([
                "success" => true,
                "message" => "Utilisateur supprimé"
            ]);
        } catch (Exception $e) {
            Logger::system('error', 'admin.deleteUser', 'Erreur suppression utilisateur', $e->getMessage());

            Response::json([
                "success" => false,
                "message" => "Erreur suppression utilisateur",
                "error" => $e->getMessage()
            ], 500);
        }
    }
}
