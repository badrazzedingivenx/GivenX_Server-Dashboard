<?php
/*
Rôle du fichier :
Ce fichier contient un contrôleur de test pour vérifier le bon fonctionnement du backend.

Ce qu’il contient :
- une classe HealthController
- des méthodes qui renvoient l’état de l’API et de la base de données

Pourquoi il existe :
Il permet de tester rapidement que l’API fonctionne correctement avant d’ajouter les vraies fonctionnalités métier.

Comment il s’intègre dans le projet :
Le routeur appellera ce contrôleur pour les routes de vérification système.
*/

class HealthController
{
    public function home(): void
    {
        Response::json([
            'success' => true,
            'message' => 'Bienvenue sur l’API Monitoring SaaS',
            'data' => [
                'service' => 'backend',
                'version' => '1.0.0',
                'status' => 'running'
            ]
        ]);
    }

    public function health(): void
    {
        $databaseStatus = 'ok';

        try {
            $pdo = Database::getConnection();
            $stmt = $pdo->query('SELECT 1');
            $stmt->fetch();
        } catch (Exception $e) {
            $databaseStatus = 'error';
        }

        Response::json([
            'success' => true,
            'message' => 'Vérification de santé du backend',
            'data' => [
                'api' => 'ok',
                'database' => $databaseStatus,
                'timestamp' => date('Y-m-d H:i:s')
            ]
        ]);
    }
}
