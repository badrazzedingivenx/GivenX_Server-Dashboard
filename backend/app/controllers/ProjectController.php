<?php
/*
Rôle du fichier :
Ce fichier contient le contrôleur de gestion des projets surveillés.

Ce qu’il contient :
- la liste des projets de l’utilisateur connecté
- la création d’un nouveau projet
- l’affichage d’un projet précis
- la mise à jour d’un projet
- la suppression d’un projet

Pourquoi il existe :
Il regroupe les opérations liées aux projets dans un contrôleur dédié.

Comment il s’intègre dans le projet :
Le routeur appellera ce contrôleur pour les routes /api/projects.
*/

class ProjectController
{
    public function index(array $params = []): void
    {
        $user = Auth::requireAuth();
        $pdo = Database::getConnection();

        $stmt = $pdo->prepare("
            SELECT
                projects.id,
                projects.user_id,
                projects.server_id,
                projects.name,
                projects.description,
                projects.project_path,
                projects.port,
                projects.process_name,
                projects.status,
                projects.is_active,
                projects.created_at,
                projects.updated_at,
                servers.name AS server_name
            FROM projects
            INNER JOIN servers ON servers.id = projects.server_id
            WHERE projects.user_id = :user_id
            ORDER BY projects.id DESC
        ");

        $stmt->execute([
            'user_id' => $user['id']
        ]);

        $projects = $stmt->fetchAll();

        Response::json([
            'success' => true,
            'message' => 'Liste des projets récupérée avec succès',
            'data' => [
                'projects' => $projects
            ]
        ]);
    }

    public function store(array $params = []): void
    {
        $user = Auth::requireAuth();
        $input = Request::input();

        $serverId = isset($input['server_id']) ? (int) $input['server_id'] : 0;
        $name = trim($input['name'] ?? '');
        $description = trim($input['description'] ?? '');
        $projectPath = trim($input['project_path'] ?? '');
        $port = isset($input['port']) && $input['port'] !== '' ? (int) $input['port'] : null;
        $processName = trim($input['process_name'] ?? '');
        $status = trim($input['status'] ?? 'unknown');
        $isActive = isset($input['is_active']) ? (int) !!$input['is_active'] : 1;

        if ($serverId <= 0) {
            Response::json([
                'success' => false,
                'message' => 'Le champ server_id est obligatoire'
            ], 422);
        }

        if ($name === '') {
            Response::json([
                'success' => false,
                'message' => 'Le champ name est obligatoire'
            ], 422);
        }

        $allowedStatuses = ['running', 'stopped', 'warning', 'unknown'];
        if (!in_array($status, $allowedStatuses, true)) {
            Response::json([
                'success' => false,
                'message' => 'Le champ status est invalide'
            ], 422);
        }

        $pdo = Database::getConnection();

        $serverStmt = $pdo->prepare("
            SELECT id, user_id, name
            FROM servers
            WHERE id = :id AND user_id = :user_id
            LIMIT 1
        ");

        $serverStmt->execute([
            'id' => $serverId,
            'user_id' => $user['id']
        ]);

        $server = $serverStmt->fetch();

        if (!$server) {
            Response::json([
                'success' => false,
                'message' => 'Serveur introuvable ou non autorisé'
            ], 404);
        }

        $stmt = $pdo->prepare("
            INSERT INTO projects (
                user_id,
                server_id,
                name,
                description,
                project_path,
                port,
                process_name,
                status,
                is_active
            ) VALUES (
                :user_id,
                :server_id,
                :name,
                :description,
                :project_path,
                :port,
                :process_name,
                :status,
                :is_active
            )
        ");

        $stmt->execute([
            'user_id' => $user['id'],
            'server_id' => $serverId,
            'name' => $name,
            'description' => $description !== '' ? $description : null,
            'project_path' => $projectPath !== '' ? $projectPath : null,
            'port' => $port,
            'process_name' => $processName !== '' ? $processName : null,
            'status' => $status,
            'is_active' => $isActive
        ]);

        $projectId = (int) $pdo->lastInsertId();

        $projectStmt = $pdo->prepare("
            SELECT
                projects.id,
                projects.user_id,
                projects.server_id,
                projects.name,
                projects.description,
                projects.project_path,
                projects.port,
                projects.process_name,
                projects.status,
                projects.is_active,
                projects.created_at,
                projects.updated_at,
                servers.name AS server_name
            FROM projects
            INNER JOIN servers ON servers.id = projects.server_id
            WHERE projects.id = :id AND projects.user_id = :user_id
            LIMIT 1
        ");

        $projectStmt->execute([
            'id' => $projectId,
            'user_id' => $user['id']
        ]);

        $project = $projectStmt->fetch();

        Response::json([
            'success' => true,
            'message' => 'Projet créé avec succès',
            'data' => [
                'project' => $project
            ]
        ], 201);
    }

    public function show(array $params): void
    {
        $user = Auth::requireAuth();
        $projectId = isset($params['id']) ? (int) $params['id'] : 0;

        if ($projectId <= 0) {
            Response::json([
                'success' => false,
                'message' => 'Identifiant projet invalide'
            ], 422);
        }

        $pdo = Database::getConnection();

        $stmt = $pdo->prepare("
            SELECT
                projects.id,
                projects.user_id,
                projects.server_id,
                projects.name,
                projects.description,
                projects.project_path,
                projects.port,
                projects.process_name,
                projects.status,
                projects.is_active,
                projects.created_at,
                projects.updated_at,
                servers.name AS server_name
            FROM projects
            INNER JOIN servers ON servers.id = projects.server_id
            WHERE projects.id = :id AND projects.user_id = :user_id
            LIMIT 1
        ");

        $stmt->execute([
            'id' => $projectId,
            'user_id' => $user['id']
        ]);

        $project = $stmt->fetch();

        if (!$project) {
            Response::json([
                'success' => false,
                'message' => 'Projet introuvable ou non autorisé'
            ], 404);
        }

        Response::json([
            'success' => true,
            'message' => 'Projet récupéré avec succès',
            'data' => [
                'project' => $project
            ]
        ]);
    }

    public function update(array $params): void
    {
        $user = Auth::requireAuth();
        $projectId = isset($params['id']) ? (int) $params['id'] : 0;
        $input = Request::input();

        if ($projectId <= 0) {
            Response::json([
                'success' => false,
                'message' => 'Identifiant projet invalide'
            ], 422);
        }

        $pdo = Database::getConnection();

        $existingStmt = $pdo->prepare("
            SELECT *
            FROM projects
            WHERE id = :id AND user_id = :user_id
            LIMIT 1
        ");

        $existingStmt->execute([
            'id' => $projectId,
            'user_id' => $user['id']
        ]);

        $existingProject = $existingStmt->fetch();

        if (!$existingProject) {
            Response::json([
                'success' => false,
                'message' => 'Projet introuvable ou non autorisé'
            ], 404);
        }

        $serverId = array_key_exists('server_id', $input) ? (int) $input['server_id'] : (int) $existingProject['server_id'];
        $name = array_key_exists('name', $input) ? trim((string) $input['name']) : $existingProject['name'];
        $description = array_key_exists('description', $input) ? trim((string) $input['description']) : $existingProject['description'];
        $projectPath = array_key_exists('project_path', $input) ? trim((string) $input['project_path']) : $existingProject['project_path'];
        $port = array_key_exists('port', $input) ? (($input['port'] === '' || $input['port'] === null) ? null : (int) $input['port']) : $existingProject['port'];
        $processName = array_key_exists('process_name', $input) ? trim((string) $input['process_name']) : $existingProject['process_name'];
        $status = array_key_exists('status', $input) ? trim((string) $input['status']) : $existingProject['status'];
        $isActive = array_key_exists('is_active', $input) ? (int) !!$input['is_active'] : (int) $existingProject['is_active'];

        if ($serverId <= 0) {
            Response::json([
                'success' => false,
                'message' => 'Le champ server_id est obligatoire'
            ], 422);
        }

        if ($name === '') {
            Response::json([
                'success' => false,
                'message' => 'Le champ name est obligatoire'
            ], 422);
        }

        $allowedStatuses = ['running', 'stopped', 'warning', 'unknown'];
        if (!in_array($status, $allowedStatuses, true)) {
            Response::json([
                'success' => false,
                'message' => 'Le champ status est invalide'
            ], 422);
        }

        $serverStmt = $pdo->prepare("
            SELECT id, user_id
            FROM servers
            WHERE id = :id AND user_id = :user_id
            LIMIT 1
        ");

        $serverStmt->execute([
            'id' => $serverId,
            'user_id' => $user['id']
        ]);

        $server = $serverStmt->fetch();

        if (!$server) {
            Response::json([
                'success' => false,
                'message' => 'Serveur introuvable ou non autorisé'
            ], 404);
        }

        $stmt = $pdo->prepare("
            UPDATE projects
            SET
                server_id = :server_id,
                name = :name,
                description = :description,
                project_path = :project_path,
                port = :port,
                process_name = :process_name,
                status = :status,
                is_active = :is_active
            WHERE id = :id AND user_id = :user_id
        ");

        $stmt->execute([
            'server_id' => $serverId,
            'name' => $name,
            'description' => $description !== '' ? $description : null,
            'project_path' => $projectPath !== '' ? $projectPath : null,
            'port' => $port,
            'process_name' => $processName !== '' ? $processName : null,
            'status' => $status,
            'is_active' => $isActive,
            'id' => $projectId,
            'user_id' => $user['id']
        ]);

        $updatedStmt = $pdo->prepare("
            SELECT
                projects.id,
                projects.user_id,
                projects.server_id,
                projects.name,
                projects.description,
                projects.project_path,
                projects.port,
                projects.process_name,
                projects.status,
                projects.is_active,
                projects.created_at,
                projects.updated_at,
                servers.name AS server_name
            FROM projects
            INNER JOIN servers ON servers.id = projects.server_id
            WHERE projects.id = :id AND projects.user_id = :user_id
            LIMIT 1
        ");

        $updatedStmt->execute([
            'id' => $projectId,
            'user_id' => $user['id']
        ]);

        $project = $updatedStmt->fetch();

        Response::json([
            'success' => true,
            'message' => 'Projet mis à jour avec succès',
            'data' => [
                'project' => $project
            ]
        ]);
    }

    public function destroy(array $params): void
    {
        $user = Auth::requireAuth();
        $projectId = isset($params['id']) ? (int) $params['id'] : 0;

        if ($projectId <= 0) {
            Response::json([
                'success' => false,
                'message' => 'Identifiant projet invalide'
            ], 422);
        }

        $pdo = Database::getConnection();

        $stmt = $pdo->prepare("
            SELECT id, user_id, name
            FROM projects
            WHERE id = :id AND user_id = :user_id
            LIMIT 1
        ");

        $stmt->execute([
            'id' => $projectId,
            'user_id' => $user['id']
        ]);

        $project = $stmt->fetch();

        if (!$project) {
            Response::json([
                'success' => false,
                'message' => 'Projet introuvable ou non autorisé'
            ], 404);
        }

        $deleteStmt = $pdo->prepare("
            DELETE FROM projects
            WHERE id = :id AND user_id = :user_id
        ");

        $deleteStmt->execute([
            'id' => $projectId,
            'user_id' => $user['id']
        ]);

        Response::json([
            'success' => true,
            'message' => 'Projet supprimé avec succès',
            'data' => [
                'project_id' => $projectId
            ]
        ]);
    }
}
