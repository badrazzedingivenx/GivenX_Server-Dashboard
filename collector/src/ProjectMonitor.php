<?php
/*
Rôle du fichier :
Ce fichier contient la logique de surveillance des projets applicatifs.

Ce qu’il contient :
- la lecture des projets en base via l’API n’est pas encore faite ici
- la détection d’un projet par process_name
- le calcul simplifié du CPU et de la RAM du projet
- la détermination du statut running/stopped

Pourquoi il existe :
Le projet doit surveiller les applications elles-mêmes, pas seulement le serveur global.

Comment il s’intègre dans le projet :
La classe Collector utilisera ProjectMonitor pour construire et envoyer les métriques
des projets vers le backend.
*/

class ProjectMonitor
{
    public function inspectProject(array $project): array
    {
        $processName = trim((string) ($project['process_name'] ?? ''));
        $projectPath = trim((string) ($project['project_path'] ?? ''));
        $projectPort = isset($project['port']) ? (int) $project['port'] : null;

        if ($processName === '' && $projectPath === '' && !$projectPort) {
            return [
                'cpu_usage' => 0.0,
                'ram_usage_mb' => 0.0,
                'status' => 'unknown',
                'pid' => null,
                'port_detected' => null,
                'port_open' => false,
                'collected_at' => date('Y-m-d H:i:s')
            ];
        }

        $output = shell_exec("ps -eo pid,pcpu,rss,args --no-headers");

        if (!$output || trim($output) === '') {
            return [
                'cpu_usage' => 0.0,
                'ram_usage_mb' => 0.0,
                'status' => 'stopped',
                'pid' => null,
                'port_detected' => $projectPort,
                'port_open' => $this->isPortOpen($projectPort),
                'collected_at' => date('Y-m-d H:i:s')
            ];
        }

        $lines = preg_split('/\r\n|\r|\n/', trim($output));
        $matched = [];

        foreach ($lines as $line) {
            $parts = preg_split('/\s+/', trim($line), 4);

            $pid = isset($parts[0]) ? (int) $parts[0] : null;
            $pcpu = isset($parts[1]) ? (float) $parts[1] : 0.0;
            $rssKb = isset($parts[2]) ? (float) $parts[2] : 0.0;
            $args = $parts[3] ?? '';

            $matchesProcess = $processName !== '' && stripos($args, $processName) !== false;
            $matchesPath = $projectPath !== '' && stripos($args, $projectPath) !== false;

            if ($matchesProcess || $matchesPath) {
                $matched[] = [
                    'pid' => $pid,
                    'cpu' => $pcpu,
                    'rss_kb' => $rssKb,
                    'args' => $args,
                ];
            }
        }

        if (!$matched) {
            return [
                'cpu_usage' => 0.0,
                'ram_usage_mb' => 0.0,
                'status' => 'stopped',
                'pid' => null,
                'port_detected' => $projectPort,
                'port_open' => $this->isPortOpen($projectPort),
                'collected_at' => date('Y-m-d H:i:s')
            ];
        }

        $totalCpu = array_sum(array_column($matched, 'cpu'));
        $totalRamKb = array_sum(array_column($matched, 'rss_kb'));

        return [
            'cpu_usage' => round($totalCpu, 2),
            'ram_usage_mb' => round($totalRamKb / 1024, 2),
            'status' => 'running',
            'pid' => $matched[0]['pid'] ?? null,
            'port_detected' => $projectPort,
            'port_open' => $this->isPortOpen($projectPort),
            'collected_at' => date('Y-m-d H:i:s')
        ];
    }

    private function isPortOpen(?int $port): bool
    {
        if (!$port) {
            return false;
        }

        $connection = @fsockopen('127.0.0.1', $port, $errno, $errstr, 1);

        if ($connection) {
            fclose($connection);
            return true;
        }

        return false;
    }
}
