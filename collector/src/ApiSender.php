<?php
/*
Rôle du fichier :
Ce fichier contient la classe responsable de l’envoi HTTP des métriques vers le backend.

Ce qu’il contient :
- la construction de la requête POST JSON
- l’ajout du token Bearer du collecteur
- l’envoi des métriques serveur
- l’envoi des métriques projet

Pourquoi il existe :
Le collecteur doit pouvoir transmettre ses métriques à l’API REST du projet.

Comment il s’intègre dans le projet :
La classe Collector utilise ApiSender pour pousser les métriques vers le backend.
*/



class ApiSender
{
    private string $apiBaseUrl;
    private string $collectorToken;

    public function __construct(string $apiBaseUrl, string $collectorToken)
    {
        $this->apiBaseUrl = rtrim($apiBaseUrl, '/');
        $this->collectorToken = $collectorToken;
    }

    private function postJson(string $url, array $payload): array
    {
        $jsonPayload = json_encode($payload, JSON_UNESCAPED_UNICODE);

        if ($jsonPayload === false) {
            return [
                'success' => false,
                'http_code' => 0,
                'error' => 'Impossible d’encoder le payload JSON'
            ];
        }

        $ch = curl_init($url);

        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_POSTFIELDS, $jsonPayload);
        curl_setopt($ch, CURLOPT_HTTPHEADER, [
            'Content-Type: application/json',
            'Authorization: Bearer ' . $this->collectorToken,
            'Content-Length: ' . strlen($jsonPayload)
        ]);
        curl_setopt($ch, CURLOPT_CONNECTTIMEOUT, 3);
        curl_setopt($ch, CURLOPT_TIMEOUT, 10);

        $responseBody = curl_exec($ch);
        $httpCode = (int) curl_getinfo($ch, CURLINFO_HTTP_CODE);
        $curlError = curl_error($ch);

        if ($responseBody === false) {
            curl_close($ch);

            return [
                'success' => false,
                'http_code' => $httpCode,
                'error' => $curlError !== '' ? $curlError : 'Erreur HTTP inconnue'
            ];
        }

        curl_close($ch);

        $decoded = json_decode($responseBody, true);

        return [
            'success' => $httpCode >= 200 && $httpCode < 300,
            'http_code' => $httpCode,
            'response' => is_array($decoded) ? $decoded : $responseBody
        ];
    }

    public function sendServerMetrics(array $payload): array
    {
        return $this->postJson($this->apiBaseUrl . '/api/collector/server-metrics', $payload);
    }

    public function sendProjectMetrics(array $payload): array
    {
        return $this->postJson($this->apiBaseUrl . '/api/collector/project-metrics', $payload);
    }
}
