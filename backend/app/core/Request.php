<?php
/*
Rôle du fichier :
Ce fichier représente la requête HTTP reçue par l’API.

Ce qu’il contient :
- une classe Request
- des méthodes pour lire la méthode HTTP
- l’URI
- les paramètres JSON envoyés

Pourquoi il existe :
Il simplifie l’accès aux données de la requête dans les contrôleurs.

Comment il s’intègre dans le projet :
Le routeur et les contrôleurs utiliseront cette classe pour lire les données entrantes.
*/

class Request
{
    public static function method(): string
    {
        return $_SERVER['REQUEST_METHOD'] ?? 'GET';
    }

    public static function uri(): string
    {
        $uri = $_SERVER['REQUEST_URI'] ?? '/';
        $parsedUri = parse_url($uri, PHP_URL_PATH);

        return $parsedUri ?: '/';
    }

    public static function input(): array
    {
        $rawInput = file_get_contents('php://input');
        $decoded = json_decode($rawInput, true);

        return is_array($decoded) ? $decoded : [];
    }

    public static function query(string $key, $default = null)
    {
        return $_GET[$key] ?? $default;
    }
}
