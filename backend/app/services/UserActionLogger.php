<?php

/*
Rôle du fichier :
Ce service enregistre les actions des utilisateurs dans la base de données.

Ce qu’il contient :
- une méthode statique pour enregistrer une action métier

Pourquoi il existe :
Le cahier des charges demande des logs d’actions utilisateur.

Comment il s’intègre dans le projet :
Les contrôleurs appellent ce service après une action importante
comme login, création serveur, création projet, suppression, etc.
*/

class UserActionLogger
{
    public static function log(int $userId, string $action, ?string $details = null): void
    {
        try {
            $pdo = Database::getConnection();

            $stmt = $pdo->prepare("
                INSERT INTO activity_logs (user_id, action, details, created_at)
                VALUES (?, ?, ?, NOW())
            ");

            $stmt->execute([
                $userId,
                $action,
                $details
            ]);
        } catch (Exception $e) {
            // On ne casse pas l’application si le log échoue
        }
    }
}
