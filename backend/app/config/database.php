<?php
/*
Rôle du fichier :
Ce fichier contient la configuration de connexion à la base de données MySQL.

Ce qu’il contient :
- l’hôte MySQL
- le nom de la base
- l’utilisateur
- le mot de passe
- le jeu de caractères

Pourquoi il existe :
Il permet de centraliser la configuration de la base de données dans un seul endroit.

Comment il s’intègre dans le projet :
La classe Database utilise ce fichier pour construire la connexion PDO vers MySQL.
*/

return [
    'host' => $_ENV['DB_HOST'] ?? 'localhost',
    'port' => $_ENV['DB_PORT'] ?? '3306',
    'database' => $_ENV['DB_DATABASE'] ?? 'monitoring_saas',
    'username' => $_ENV['DB_USERNAME'] ?? 'root',
    'password' => $_ENV['DB_PASSWORD'] ?? '',
    'charset' => 'utf8mb4',
];
