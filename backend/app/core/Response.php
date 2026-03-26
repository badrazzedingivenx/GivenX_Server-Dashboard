<?php
/*
Rôle du fichier :
Ce fichier gère les réponses HTTP envoyées par l’API.

Ce qu’il contient :
- une classe Response
- une méthode statique pour renvoyer du JSON proprement

Pourquoi il existe :
Il permet d’uniformiser toutes les réponses du backend.

Comment il s’intègre dans le projet :
Tous les contrôleurs utiliseront cette classe pour répondre au frontend ou au collecteur.
*/

class Response
{
    public static function json(array $data, int $statusCode = 200): void
    {
        http_response_code($statusCode);
        header('Content-Type: application/json; charset=utf-8');

        echo json_encode($data, JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE);
        exit;
    }
}
