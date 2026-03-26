<?php
/*
Rôle du fichier :
Ce fichier contient la configuration générale de l’application backend.

Ce qu’il contient :
- le nom du projet
- l’environnement
- l’URL de base de l’API

Pourquoi il existe :
Il centralise les paramètres généraux du backend.

Comment il s’intègre dans le projet :
Les autres fichiers pourront lire cette configuration pour éviter de répéter les valeurs dans plusieurs endroits.
*/

return [
    'app_name' => 'Monitoring SaaS',
    'env' => 'development',
    'base_url' => 'http://localhost:8000',
];

