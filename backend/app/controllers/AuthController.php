<?php

/*
Rôle du fichier :
Ce contrôleur gère l’authentification des utilisateurs.

Ce qu’il contient :
- inscription
- connexion
- utilisateur connecté
- déconnexion

Pourquoi il existe :
L’application a besoin d’un système d’accès sécurisé.

Comment il s’intègre dans le projet :
Les routes /api/auth/* appellent ce contrôleur.
*/



class AuthController
{
    public function register(array $params = []): void
    {
        $input = Request::input();
        $pdo = Database::getConnection();

        $name = trim($input['name'] ?? '');
        $email = trim($input['email'] ?? '');
        $password = (string) ($input['password'] ?? '');

        if ($name === '' || $email === '' || $password === '') {
            Response::json([
                "success" => false,
                "message" => "Les champs name, email et password sont obligatoires"
            ], 422);
        }

        if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
            Response::json([
                "success" => false,
                "message" => "Adresse email invalide"
            ], 422);
        }

        $checkStmt = $pdo->prepare("SELECT id FROM users WHERE email = ? LIMIT 1");
        $checkStmt->execute([$email]);

        if ($checkStmt->fetch()) {
            Response::json([
                "success" => false,
                "message" => "Cet email existe déjà"
            ], 409);
        }

        $hashedPassword = password_hash($password, PASSWORD_BCRYPT);

        $stmt = $pdo->prepare("
            INSERT INTO users (name, email, password_hash, role, status, created_at)
            VALUES (?, ?, ?, 'user', 'active', NOW())
        ");

        $stmt->execute([$name, $email, $hashedPassword]);

        $userId = (int) $pdo->lastInsertId();

        UserActionLogger::log($userId, "register", "Inscription utilisateur : {$email}");
        SystemLogService::log("info", "Nouvel utilisateur inscrit : {$email}");

        Response::json([
            "success" => true,
            "message" => "Utilisateur créé avec succès"
        ], 201);
    }

    public function login(array $params = []): void
    {
        $input = Request::input();
        $pdo = Database::getConnection();

        $email = trim($input['email'] ?? '');
        $password = (string) ($input['password'] ?? '');

        if ($email === '' || $password === '') {
            Response::json([
                "success" => false,
                "message" => "Email et mot de passe obligatoires"
            ], 422);
        }

        $stmt = $pdo->prepare("
            SELECT id, name, email, password_hash, role, status
            FROM users
            WHERE email = ?
            LIMIT 1
        ");

        $stmt->execute([$email]);
        $user = $stmt->fetch(PDO::FETCH_ASSOC);

        if (!$user || !password_verify($password, $user['password_hash'])) {
            SystemLogService::log("warning", "Tentative de connexion échouée : {$email}");

            Response::json([
                "success" => false,
                "message" => "Identifiants invalides"
            ], 401);
        }

        if (($user['status'] ?? 'active') !== 'active') {
            Response::json([
                "success" => false,
                "message" => "Compte bloqué ou inactif"
            ], 403);
        }

        $plainToken = bin2hex(random_bytes(32));
        $tokenHash = hash('sha256', $plainToken);

        $tokenStmt = $pdo->prepare("
            INSERT INTO api_tokens (user_id, token_hash, expires_at, created_at)
            VALUES (?, ?, DATE_ADD(NOW(), INTERVAL 7 DAY), NOW())
        ");

        $tokenStmt->execute([
            $user['id'],
            $tokenHash
        ]);

        UserActionLogger::log((int)$user['id'], "login", "Connexion utilisateur : {$email}");
        SystemLogService::log("info", "Connexion réussie : {$email}");

        Response::json([
            "success" => true,
            "message" => "Connexion réussie",
            "data" => [
                "token" => $plainToken,
                "token_type" => "Bearer",
                "user" => [
                    "id" => (int)$user['id'],
                    "name" => $user['name'],
                    "email" => $user['email'],
                    "role" => $user['role'],
                    "status" => $user['status']
                ]
            ]
        ]);
    }

    public function me(array $params = []): void
    {
        $user = Auth::requireAuth();

        Response::json([
            "success" => true,
            "message" => "Utilisateur authentifié",
            "data" => [
                "user" => [
                    "id" => (int)$user['id'],
                    "name" => $user['name'],
                    "email" => $user['email'],
                    "role" => $user['role'],
                    "status" => $user['status']
                ]
            ]
        ]);
    }

    public function logout(array $params = []): void
    {
        $user = Auth::requireAuth();
        $token = Auth::getBearerToken();

        if ($token) {
            $tokenHash = hash('sha256', $token);
            $pdo = Database::getConnection();

            $stmt = $pdo->prepare("
                UPDATE api_tokens
                SET revoked_at = NOW()
                WHERE token_hash = ?
            ");
            $stmt->execute([$tokenHash]);
        }

        UserActionLogger::log((int)$user['id'], "logout", "Déconnexion utilisateur : {$user['email']}");
        SystemLogService::log("info", "Déconnexion : {$user['email']}");

        Response::json([
            "success" => true,
            "message" => "Déconnexion réussie"
        ]);
    }
}
