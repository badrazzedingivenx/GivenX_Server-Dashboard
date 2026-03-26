<?php

/*
Rôle du fichier :
Ce service enregistre les logs système dans la base de données.

Ce qu’il contient :
- une méthode statique pour écrire un log système

Pourquoi il existe :
Le cahier des charges demande des logs système exploitables.

Comment il s’intègre dans le projet :
Les contrôleurs et scripts peuvent appeler ce service pour tracer
les événements techniques importants du système.
*/

class SystemLogService
{
    public static function log(string $level, string $message): void
    {
        try {
            $pdo = Database::getConnection();

            $stmt = $pdo->prepare("
                INSERT INTO system_logs (level, message, created_at)
                VALUES (?, ?, NOW())
            ");

            $stmt->execute([
                $level,
                $message
            ]);
        } catch (Exception $e) {
            // On ne casse pas l’application si le log échoue
        }
    }
}
