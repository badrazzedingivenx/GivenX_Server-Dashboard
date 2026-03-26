<?php
/*
Rôle du fichier :
Ce fichier gère l’authentification par token du backend.

Ce qu’il contient :
- la lecture du header Authorization
- l’extraction du token Bearer
- le hash du token
- la recherche de l’utilisateur connecté
- des méthodes utilitaires liées à l’authentification

Pourquoi il existe :
Il centralise la logique d’authentification pour éviter de la dupliquer dans tous les contrôleurs.

Comment il s’intègre dans le projet :
Les contrôleurs utiliseront cette classe pour identifier l’utilisateur courant et protéger les routes privées.
*/

class Auth
{
    public static function getBearerToken(): ?string
    {
        $header = $_SERVER['HTTP_AUTHORIZATION'] ?? $_SERVER['REDIRECT_HTTP_AUTHORIZATION'] ?? null;

        if (!$header && function_exists('getallheaders')) {
            $headers = getallheaders();
            $header = $headers['Authorization'] ?? $headers['authorization'] ?? null;
        }

        if (!$header) {
            return null;
        }

        if (preg_match('/Bearer\s+(.+)/i', $header, $matches)) {
            return trim($matches[1]);
        }

        return null;
    }

    public static function hashToken(string $token): string
    {
        return hash('sha256', $token);
    }

    public static function generateToken(): string
    {
        return bin2hex(random_bytes(32));
    }

    public static function user(): ?array
    {
        $token = self::getBearerToken();

        if (!$token) {
            return null;
        }

        $tokenHash = self::hashToken($token);
        $pdo = Database::getConnection();

        $sql = "
            SELECT 
                users.id,
                users.name,
                users.email,
                users.role,
                users.status,
                api_tokens.id AS api_token_id,
                api_tokens.expires_at,
                api_tokens.revoked_at
            FROM api_tokens
            INNER JOIN users ON users.id = api_tokens.user_id
            WHERE api_tokens.token_hash = :token_hash
              AND api_tokens.revoked_at IS NULL
            LIMIT 1
        ";

        $stmt = $pdo->prepare($sql);
        $stmt->execute([
            'token_hash' => $tokenHash
        ]);

        $user = $stmt->fetch();

        if (!$user) {
            return null;
        }

        if (!empty($user['expires_at']) && strtotime($user['expires_at']) < time()) {
            return null;
        }

        if ($user['status'] !== 'active') {
            return null;
        }

        return $user;
    }

    public static function requireAuth(): array
    {
        $user = self::user();

        if (!$user) {
            Response::json([
                'success' => false,
                'message' => 'Authentification requise'
            ], 401);
        }

        return $user;
    }
}
