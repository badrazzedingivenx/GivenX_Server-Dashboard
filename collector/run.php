<?php
/*
Rôle du fichier :
Ce fichier est le point d’entrée principal du collecteur.

Ce qu’il contient :
- le chargement des classes du collecteur
- un mode affichage local
- un mode envoi des métriques serveur
- un mode envoi des métriques projet

Pourquoi il existe :
Il permet de tester rapidement le collecteur depuis le terminal.

Comment il s’intègre dans le projet :
Il sert de point d’exécution pour les futurs lancements automatiques.
*/

require_once __DIR__ . '/src/SystemReader.php';
require_once __DIR__ . '/src/ProjectMonitor.php';
require_once __DIR__ . '/src/ApiSender.php';
require_once __DIR__ . '/src/Collector.php';

$config = require __DIR__ . '/config/collector.php';

$collector = new Collector($config);

$mode = $argv[1] ?? 'print';

if ($mode === 'send') {
    echo json_encode($collector->sendOnce(), JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE) . PHP_EOL;
    exit;
}

if ($mode === 'send-projects') {
    echo json_encode($collector->sendProjectMetricsOnce(), JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE) . PHP_EOL;
    exit;
}

$collector->outputOnce();
