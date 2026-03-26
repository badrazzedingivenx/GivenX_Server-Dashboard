<?php
/*
Rôle du fichier :
Ce fichier gère les règles d’alertes personnalisables.

Ce qu’il contient :
- liste des règles
- création d’une règle
- mise à jour d’une règle
- suppression d’une règle

Pourquoi il existe :
Le cahier des charges demande des règles d’alertes configurables.

Comment il s’intègre dans le projet :
Le frontend admin ou utilisateur avancé pourra piloter les seuils d’alertes.
*/

class AlertRuleController
{
    public function index(array $params = []): void
    {
        $user = Auth::requireAuth();
        $pdo = Database::getConnection();

        $stmt = $pdo->prepare("
            SELECT *
            FROM alert_rules
            WHERE user_id = :user_id
            ORDER BY id DESC
        ");
        $stmt->execute(['user_id' => $user['id']]);
        $rules = $stmt->fetchAll(PDO::FETCH_ASSOC);

        Response::json([
            'success' => true,
            'message' => 'Règles d’alertes récupérées',
            'data' => [
                'rules' => $rules
            ]
        ]);
    }

    public function store(array $params = []): void
    {
        $user = Auth::requireAuth();
        $input = Request::input();
        $pdo = Database::getConnection();

        $stmt = $pdo->prepare("
            INSERT INTO alert_rules (
                user_id,
                server_id,
                project_id,
                rule_type,
                threshold_value,
                severity,
                cooldown_minutes,
                is_active
            ) VALUES (
                :user_id,
                :server_id,
                :project_id,
                :rule_type,
                :threshold_value,
                :severity,
                :cooldown_minutes,
                :is_active
            )
        ");

        $stmt->bindValue(':user_id', $user['id'], PDO::PARAM_INT);
        $stmt->bindValue(':server_id', $input['server_id'] ?? null, ($input['server_id'] ?? null) === null ? PDO::PARAM_NULL : PDO::PARAM_INT);
        $stmt->bindValue(':project_id', $input['project_id'] ?? null, ($input['project_id'] ?? null) === null ? PDO::PARAM_NULL : PDO::PARAM_INT);
        $stmt->bindValue(':rule_type', $input['rule_type'] ?? '', PDO::PARAM_STR);
        $stmt->bindValue(':threshold_value', $input['threshold_value'] ?? null);
        $stmt->bindValue(':severity', $input['severity'] ?? 'warning', PDO::PARAM_STR);
        $stmt->bindValue(':cooldown_minutes', $input['cooldown_minutes'] ?? 10, PDO::PARAM_INT);
        $stmt->bindValue(':is_active', $input['is_active'] ?? 1, PDO::PARAM_INT);
        $stmt->execute();

        Response::json([
            'success' => true,
            'message' => 'Règle d’alerte créée',
            'data' => [
                'id' => (int)$pdo->lastInsertId()
            ]
        ], 201);
    }

    public function update(array $params): void
    {
        $user = Auth::requireAuth();
        $ruleId = isset($params['id']) ? (int)$params['id'] : 0;
        $input = Request::input();
        $pdo = Database::getConnection();

        $stmt = $pdo->prepare("
            UPDATE alert_rules
            SET
                server_id = :server_id,
                project_id = :project_id,
                rule_type = :rule_type,
                threshold_value = :threshold_value,
                severity = :severity,
                cooldown_minutes = :cooldown_minutes,
                is_active = :is_active
            WHERE id = :id AND user_id = :user_id
        ");

        $stmt->bindValue(':server_id', $input['server_id'] ?? null, ($input['server_id'] ?? null) === null ? PDO::PARAM_NULL : PDO::PARAM_INT);
        $stmt->bindValue(':project_id', $input['project_id'] ?? null, ($input['project_id'] ?? null) === null ? PDO::PARAM_NULL : PDO::PARAM_INT);
        $stmt->bindValue(':rule_type', $input['rule_type'] ?? '', PDO::PARAM_STR);
        $stmt->bindValue(':threshold_value', $input['threshold_value'] ?? null);
        $stmt->bindValue(':severity', $input['severity'] ?? 'warning', PDO::PARAM_STR);
        $stmt->bindValue(':cooldown_minutes', $input['cooldown_minutes'] ?? 10, PDO::PARAM_INT);
        $stmt->bindValue(':is_active', $input['is_active'] ?? 1, PDO::PARAM_INT);
        $stmt->bindValue(':id', $ruleId, PDO::PARAM_INT);
        $stmt->bindValue(':user_id', $user['id'], PDO::PARAM_INT);
        $stmt->execute();

        Response::json([
            'success' => true,
            'message' => 'Règle d’alerte mise à jour'
        ]);
    }

    public function delete(array $params): void
    {
        $user = Auth::requireAuth();
        $ruleId = isset($params['id']) ? (int)$params['id'] : 0;
        $pdo = Database::getConnection();

        $stmt = $pdo->prepare("
            DELETE FROM alert_rules
            WHERE id = :id AND user_id = :user_id
        ");
        $stmt->execute([
            'id' => $ruleId,
            'user_id' => $user['id']
        ]);

        Response::json([
            'success' => true,
            'message' => 'Règle d’alerte supprimée'
        ]);
    }
}
