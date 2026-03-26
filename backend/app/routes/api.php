<?php
/*
Rôle du fichier :
Ce fichier enregistre les routes principales de l’API.

Ce qu’il contient :
- les routes HTTP accessibles
- l’association entre URL et méthodes des contrôleurs

Pourquoi il existe :
Il sépare la définition des routes de la logique métier.

Comment il s’intègre dans le projet :
Le fichier public/index.php charge ce fichier pour enregistrer les routes dans le routeur.
*/




$router->get('/', [HealthController::class, 'home']);
$router->get('/api/health', [HealthController::class, 'health']);

$router->post('/api/auth/register', [AuthController::class, 'register']);
$router->post('/api/auth/login', [AuthController::class, 'login']);
$router->get('/api/auth/me', [AuthController::class, 'me']);
$router->post('/api/auth/logout', [AuthController::class, 'logout']);

$router->get('/api/servers', [ServerController::class, 'index']);
$router->post('/api/servers', [ServerController::class, 'store']);
$router->get('/api/servers/{id}', [ServerController::class, 'show']);
$router->put('/api/servers/{id}', [ServerController::class, 'update']);
$router->delete('/api/servers/{id}', [ServerController::class, 'delete']);

$router->get('/api/projects', [ProjectController::class, 'index']);
$router->post('/api/projects', [ProjectController::class, 'store']);
$router->get('/api/projects/{id}', [ProjectController::class, 'show']);
$router->put('/api/projects/{id}', [ProjectController::class, 'update']);
$router->delete('/api/projects/{id}', [ProjectController::class, 'destroy']);

$router->get('/api/servers/{id}/metrics/history', [MetricController::class, 'serverHistory']);
$router->get('/api/servers/{id}/metrics/latest', [MetricController::class, 'serverLatest']);
$router->get('/api/projects/{id}/metrics/history', [MetricController::class, 'projectHistory']);
$router->get('/api/projects/{id}/metrics/latest', [MetricController::class, 'projectLatest']);

$router->get('/api/alerts', [AlertController::class, 'index']);
$router->post('/api/alerts/generate', [AlertController::class, 'generate']);
$router->post('/api/alerts/{id}/resolve', [AlertController::class, 'resolve']);

$router->post('/api/collector/server-metrics', [CollectorController::class, 'storeServerMetrics']);
$router->post('/api/collector/project-metrics', [CollectorProjectController::class, 'storeProjectMetrics']);

$router->get('/api/admin/dashboard', function () {
    $user = Auth::requireAuth();

    if (($user['role'] ?? 'user') !== 'admin') {
        Response::json([
            'success' => false,
            'message' => 'Accès administrateur requis'
        ], 403);
    }

    (new AdminController())->dashboard();
});

$router->get('/api/admin/users', function () {
    $user = Auth::requireAuth();

    if (($user['role'] ?? 'user') !== 'admin') {
        Response::json([
            'success' => false,
            'message' => 'Accès administrateur requis'
        ], 403);
    }

    (new AdminController())->users();
});

$router->put('/api/admin/users/{id}/block', function ($params) {
    $user = Auth::requireAuth();

    if (($user['role'] ?? 'user') !== 'admin') {
        Response::json([
            'success' => false,
            'message' => 'Accès administrateur requis'
        ], 403);
    }

    (new AdminController())->blockUser($params);
});

$router->delete('/api/admin/users/{id}', function ($params) {
    $user = Auth::requireAuth();

    if (($user['role'] ?? 'user') !== 'admin') {
        Response::json([
            'success' => false,
            'message' => 'Accès administrateur requis'
        ], 403);
    }

    (new AdminController())->deleteUser($params);
});

$router->get('/api/logs/activity', [LogController::class, 'myLogs']);
$router->get('/api/admin/logs/system', [LogController::class, 'systemLogs']);
$router->get('/api/admin/logs/users', [LogController::class, 'userLogs']);

$router->get('/api/alert-rules', [AlertRuleController::class, 'index']);
$router->post('/api/alert-rules', [AlertRuleController::class, 'store']);
$router->put('/api/alert-rules/{id}', [AlertRuleController::class, 'update']);
$router->delete('/api/alert-rules/{id}', [AlertRuleController::class, 'delete']);
