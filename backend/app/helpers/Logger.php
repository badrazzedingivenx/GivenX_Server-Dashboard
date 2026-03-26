<?php

/*
Rôle du fichier :
Ce fichier contient la classe utilitaire de logging du projet.

Ce qu’il contient :
- l’enregistrement des logs d’actions utilisateur
- l’enregistrement des logs système
- des méthodes statiques réutilisables dans tous les contrôleurs

Pourquoi il existe :
Le cahier des charges demande des logs réellement exploitables.

Comment il s’intègre dans le projet :
Les contrôleurs backend appellent cette classe pour stocker automatiquement
les événements importants dans activity_logs et system_logs.
*/

class Logger
{
    public static function activity(
        int $userId,
        string $action,
        ?string $targetType = null,
        ?int $targetId = null,
        ?string $description = null,
        ?string $ipAddress = null
    ): void {
        try {
            $pdo = Database::getConnection();

            $stmt = $pdo->prepare("
                INSERT INTO activity_logs (
                    user_id,
                    action,
                    target_type,
                    target_id,
                    description,
                    ip_address
                ) VALUES (
                    :user_id,
                    :action,
                    :target_type,
                    :target_id,
                    :description,
                    :ip_address
                )
            ");

            $stmt->execute([
                'user_id' => $userId,
                'action' => $action,
                'target_type' => $targetType,
                'target_id' => $targetId,
                'description' => $description,
                'ip_address' => $ipAddress ?? self::ip()
            ]);
        } catch (Exception $e) {
            self::system(
                'error',
                'logger.activity',
                'Erreur enregistrement activity log',
                $e->getMessage()
            );
        }
    }

    public static function system(
        string $level,
        string $source,
        string $message,
        ?string $context = null
    ): void {
        try {
            $pdo = Database::getConnection();

            $stmt = $pdo->prepare("
                INSERT INTO system_logs (
                    level,
                    source,
                    message,
                    context
                ) VALUES (
                    :level,
                    :source,
                    :message,
                    :context
                )
            ");

            $stmt->execute([
                'level' => $level,
                'source' => $source,
                'message' => $message,
                'context' => $context
            ]);
        } catch (Exception $e) {
            // On évite une boucle infinie de log si même le système de logs casse.
        }
    }

    public static function ip(): ?string
    {
        return $_SERVER['REMOTE_ADDR'] ?? null;
    }
}
