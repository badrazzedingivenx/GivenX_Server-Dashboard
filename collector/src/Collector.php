<?php
/*
Rôle du fichier :
Ce fichier contient la classe principale du collecteur.

Ce qu’il contient :
- la collecte des métriques serveur
- l’envoi des métriques serveur
- la collecte des métriques projet
- l’envoi des métriques projet

Pourquoi il existe :
Il orchestre tout le travail du collecteur.

Comment il s’intègre dans le projet :
run.php instancie cette classe pour lancer les différents modes du collecteur.
*/

class Collector
{
    private array $config;
    private SystemReader $systemReader;
    private ApiSender $apiSender;
    private ProjectMonitor $projectMonitor;

    public function __construct(array $config)
    {
        $this->config = $config;
        $this->systemReader = new SystemReader();
        $this->apiSender = new ApiSender(
            $config['api_base_url'],
            $config['collector_token']
        );
        $this->projectMonitor = new ProjectMonitor();
    }

    public function collectOnce(): array
    {
        $diskPath = $this->config['disk_path'] ?? '/';

        return [
            'server_id' => (int) ($this->config['server_id'] ?? 0),
            'server_name' => $this->config['server_name'] ?? 'Unknown Server',
            'metrics' => $this->systemReader->readAll($diskPath)
        ];
    }

    public function outputOnce(): void
    {
        echo json_encode($this->collectOnce(), JSON_PRETTY_PRINT | JSON_UNESCAPED_UNICODE) . PHP_EOL;
    }

    public function sendOnce(): array
    {
        return $this->apiSender->sendServerMetrics($this->collectOnce());
    }

    public function sendProjectMetricsOnce(): array
    {
        $projects = $this->config['projects'] ?? [];
        $results = [];

        foreach ($projects as $project) {
            $payload = [
                'server_id' => (int) ($this->config['server_id'] ?? 0),
                'project_id' => (int) ($project['project_id'] ?? 0),
                'metrics' => $this->projectMonitor->inspectProject($project)
            ];

            $results[] = [
                'project_name' => $project['name'] ?? 'Projet inconnu',
                'result' => $this->apiSender->sendProjectMetrics($payload)
            ];
        }

        return $results;
    }
}
