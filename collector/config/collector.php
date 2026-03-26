<?php
/*
Rôle du fichier :
Ce fichier contient la configuration principale du collecteur Linux.

Ce qu’il contient :
- l’URL du backend
- l’intervalle de collecte
- le chemin du fichier de logs
- l’identifiant serveur
- le token collecteur
- la liste des projets à surveiller

Pourquoi il existe :
Le collecteur a besoin d’un point central pour définir son comportement.

Comment il s’intègre dans le projet :
Toutes les classes du collecteur lisent cette configuration.
*/

return [
    'api_base_url' => 'http://localhost:8000',
    'collection_interval_seconds' => 5,
    'log_file' => __DIR__ . '/../logs/collector.log',
    'server_name' => 'Serveur Ubuntu Principal',
    'disk_path' => '/',
    'server_id' => 2,
    'collector_token' => 'collector_server_token_2026',
    'projects' => [
        [
            'project_id' => 2,
            'name' => 'Frontend Monitoring',
            'process_name' => 'node',
            'port' => 3000,
            'project_path' => '/var/www/monitoring/frontend'
        ]
    ]
];
