<?php
/*
Rôle du fichier :
Ce fichier est le point d’entrée principal du backend PHP.

Toutes les requêtes HTTP vers l’API passent par ce fichier.

Ce qu’il fait :
- configure les headers CORS pour React
- charge les classes core
- charge les services
- charge les contrôleurs
- charge les routes
- exécute le routeur

Pourquoi il existe :
Il centralise l’exécution de l’API REST.

Comment il s’intègre dans le projet :
Le frontend React, le collecteur Linux et les appels curl passent par ce fichier.
*/


require_once __DIR__ . '/../vendor/autoload.php';

function loadEnvFile(string $path): void
{
    if (!file_exists($path)) {
        return;
    }

    $lines = file($path, FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES);

    foreach ($lines as $line) {
        $line = trim($line);

        if ($line === '' || str_starts_with($line, '#')) {
            continue;
        }

        [$name, $value] = array_pad(explode('=', $line, 2), 2, '');

        $name = trim($name);
        $value = trim($value);

        if ($name !== '') {
            $_ENV[$name] = $value;
            putenv($name . '=' . $value);
        }
    }
}

loadEnvFile(__DIR__ . '/../.env');

ini_set('display_errors', 0);
ini_set('log_errors', 1);
ini_set('error_log', __DIR__ . '/../storage/logs/php_errors.log');
error_reporting(E_ALL);

header("Content-Type: application/json; charset=UTF-8");

$allowedOrigins = [
    'http://localhost:3000',
    'http://127.0.0.1:3000',
];

$origin = $_SERVER['HTTP_ORIGIN'] ?? '';

if (in_array($origin, $allowedOrigins, true)) {
    header("Access-Control-Allow-Origin: {$origin}");
}

header("Vary: Origin");
header("Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS");
header("Access-Control-Allow-Headers: Content-Type, Authorization");
header("Access-Control-Allow-Credentials: true");

if (($_SERVER['REQUEST_METHOD'] ?? 'GET') === 'OPTIONS') {
    http_response_code(200);
    exit;
}

require_once __DIR__ . '/../app/core/Database.php';
require_once __DIR__ . '/../app/core/Request.php';
require_once __DIR__ . '/../app/core/Response.php';
require_once __DIR__ . '/../app/core/Router.php';
require_once __DIR__ . '/../app/core/Auth.php';

require_once __DIR__ . '/../app/services/SystemLogService.php';
require_once __DIR__ . '/../app/services/UserActionLogger.php';
require_once __DIR__ . '/../app/services/AlertEngine.php';
require_once __DIR__ . '/../app/services/EmailService.php';

require_once __DIR__ . '/../app/controllers/HealthController.php';
require_once __DIR__ . '/../app/controllers/AuthController.php';
require_once __DIR__ . '/../app/controllers/ServerController.php';
require_once __DIR__ . '/../app/controllers/ProjectController.php';
require_once __DIR__ . '/../app/controllers/MetricController.php';
require_once __DIR__ . '/../app/controllers/AlertController.php';
require_once __DIR__ . '/../app/controllers/AlertRuleController.php';
require_once __DIR__ . '/../app/controllers/CollectorController.php';
require_once __DIR__ . '/../app/controllers/CollectorProjectController.php';
require_once __DIR__ . '/../app/controllers/AdminController.php';
require_once __DIR__ . '/../app/controllers/LogController.php';

$router = new Router();

require_once __DIR__ . '/../app/routes/api.php';

$method = Request::method();
$uri = Request::uri();

$router->dispatch($method, $uri);
