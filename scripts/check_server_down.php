<?php
/*
Rôle du fichier :
Ce script vérifie périodiquement si un serveur est indisponible.

Ce qu’il contient :
- le chargement minimal du backend
- l’appel au moteur d’alertes pour détecter les serveurs down

Pourquoi il existe :
Une alerte "server_down" ne peut pas être déclenchée par le collecteur
si le collecteur ne réussit plus à envoyer de métriques.

Comment il s’intègre dans le projet :
Ce script est prévu pour être lancé par cron toutes les minutes.
*/

require_once __DIR__ . '/../backend/app/core/Database.php';
require_once __DIR__ . '/../backend/app/services/AlertEngine.php';

AlertEngine::checkServerDownAlerts();

echo json_encode([
    'success' => true,
    'message' => 'Vérification server_down terminée'
], JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE) . PHP_EOL;
