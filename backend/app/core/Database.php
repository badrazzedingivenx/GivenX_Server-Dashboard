<?php
/*
Rôle du fichier :
Ce fichier gère la connexion à la base de données MySQL avec PDO.

Ce qu’il contient :
- une classe Database
- une méthode statique pour obtenir une connexion PDO unique

Pourquoi il existe :
Il évite de répéter la logique de connexion dans plusieurs fichiers du projet.

Comment il s’intègre dans le projet :
Les contrôleurs et services utiliseront cette classe pour dialoguer avec MySQL.
*/


class Database
{
    private static ?PDO $connection = null;

    public static function getConnection(): PDO
    {
        if (self::$connection === null) {
            $config = require __DIR__ . '/../config/database.php';

            $dsn = sprintf(
                'mysql:host=%s;port=%s;dbname=%s;charset=%s',
                $config['host'],
                $config['port'],
                $config['database'],
                $config['charset']
            );

            try {
                self::$connection = new PDO(
                    $dsn,
                    $config['username'],
                    $config['password'],
                    [
                        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
                        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
                    ]
                );
            } catch (PDOException $e) {
                error_log('[DB ERROR] ' . $e->getMessage());

                Response::json([
                    'success' => false,
                    'message' => 'Erreur de connexion à la base de données'
                ], 500);
            }
        }

        return self::$connection;
    }
}
