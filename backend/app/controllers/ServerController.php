
<?php
/*
Rôle du fichier :
Ce contrôleur gère toutes les opérations liées aux serveurs.

Ce qu’il contient :
- ajout serveur
- liste serveurs
- détail serveur
- modification serveur
- suppression serveur

Pourquoi il existe :
Il centralise la logique métier des serveurs.

Comment il s’intègre dans le projet :
Les routes /api/servers* appellent ce contrôleur.
*/

class ServerController
{
    private PDO $db;

    public function __construct()
    {
        $this->db = Database::getConnection();
    }

    public function index(array $params = []): void
    {
        $user = Auth::requireAuth();

        $stmt = $this->db->prepare("
            SELECT *
            FROM servers
            WHERE user_id = ?
            ORDER BY created_at DESC
        ");

        $stmt->execute([$user['id']]);
        $servers = $stmt->fetchAll(PDO::FETCH_ASSOC);

        Response::json([
            'success' => true,
            'message' => 'Liste des serveurs récupérée avec succès',
            'data' => [
                'servers' => $servers
            ]
        ]);
    }

    public function store(array $params = []): void
    {
        $user = Auth::requireAuth();
        $data = Request::input();

        $stmt = $this->db->prepare("
            INSERT INTO servers
            (user_id, name, host, ip_address, description, os_type, is_active, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())
        ");

        $stmt->execute([
            $user['id'],
            trim((string) ($data['name'] ?? '')),
            trim((string) ($data['host'] ?? '')),
            trim((string) ($data['ip_address'] ?? '')),
            trim((string) ($data['description'] ?? '')),
            trim((string) ($data['os_type'] ?? '')),
            isset($data['is_active']) ? (int) $data['is_active'] : 1
        ]);

        $serverId = (int) $this->db->lastInsertId();

        $stmt = $this->db->prepare("
            SELECT *
            FROM servers
            WHERE id = ?
              AND user_id = ?
            LIMIT 1
        ");
        $stmt->execute([$serverId, $user['id']]);
        $server = $stmt->fetch(PDO::FETCH_ASSOC);

        UserActionLogger::log((int) $user['id'], 'server_created', 'Serveur créé : ' . ($data['name'] ?? ''));
        SystemLogService::log('info', 'Serveur créé par ' . $user['email'] . ' : ' . ($data['name'] ?? ''));

        Response::json([
            'success' => true,
            'message' => 'Serveur créé avec succès',
            'data' => [
                'server' => $server
            ]
        ], 201);
    }

    public function show(array $params): void
    {
        $user = Auth::requireAuth();
        $id = (int) ($params['id'] ?? 0);

        $stmt = $this->db->prepare("
            SELECT *
            FROM servers
            WHERE id = ?
              AND user_id = ?
            LIMIT 1
        ");

        $stmt->execute([$id, $user['id']]);
        $server = $stmt->fetch(PDO::FETCH_ASSOC);

        if (!$server) {
            Response::json([
                'success' => false,
                'message' => 'Serveur introuvable ou non autorisé'
            ], 404);
            return;
        }

        Response::json([
            'success' => true,
            'message' => 'Serveur récupéré avec succès',
            'data' => [
                'server' => $server
            ]
        ]);
    }

    public function update(array $params): void
    {
        $user = Auth::requireAuth();
        $data = Request::input();
        $id = (int) ($params['id'] ?? 0);

        $stmt = $this->db->prepare("
            UPDATE servers
            SET
                name = ?,
                host = ?,
                ip_address = ?,
                description = ?,
                os_type = ?,
                is_active = ?,
                updated_at = NOW()
            WHERE id = ?
              AND user_id = ?
        ");

        $stmt->execute([
            trim((string) ($data['name'] ?? '')),
            trim((string) ($data['host'] ?? '')),
            trim((string) ($data['ip_address'] ?? '')),
            trim((string) ($data['description'] ?? '')),
            trim((string) ($data['os_type'] ?? '')),
            isset($data['is_active']) ? (int) $data['is_active'] : 1,
            $id,
            $user['id']
        ]);

        UserActionLogger::log((int) $user['id'], 'server_updated', 'Serveur modifié ID : ' . $id);
        SystemLogService::log('info', 'Serveur modifié par ' . $user['email'] . ' : ID ' . $id);

        Response::json([
            'success' => true,
            'message' => 'Serveur mis à jour avec succès'
        ]);
    }

    public function delete(array $params): void
    {
        $user = Auth::requireAuth();
        $id = (int) ($params['id'] ?? 0);

        $stmt = $this->db->prepare("
            DELETE FROM servers
            WHERE id = ?
              AND user_id = ?
        ");

        $stmt->execute([$id, $user['id']]);

        UserActionLogger::log((int) $user['id'], 'server_deleted', 'Serveur supprimé ID : ' . $id);
        SystemLogService::log('warning', 'Serveur supprimé par ' . $user['email'] . ' : ID ' . $id);

        Response::json([
            'success' => true,
            'message' => 'Serveur supprimé avec succès',
            'data' => [
                'server_id' => $id
            ]
        ]);
    }
}
