<?php
/*
Rôle du fichier :
Ce fichier contient la classe chargée de lire les métriques système Linux.

Ce qu’il contient :
- la lecture du CPU
- la lecture de la RAM
- la lecture du disque
- la lecture du réseau
- la lecture de la température si disponible
- le comptage des processus
- une méthode globale qui regroupe toutes les métriques

Pourquoi il existe :
Le collecteur doit centraliser dans une classe dédiée toute la logique de lecture système.

Comment il s’intègre dans le projet :
La classe Collector utilisera SystemReader pour récupérer les métriques avant affichage ou envoi vers l’API.
*/

class SystemReader
{
    public function readAll(string $diskPath = '/'): array
    {
        return [
            'cpu' => $this->readCpuUsage(),
            'ram' => $this->readRamUsage(),
            'disk' => $this->readDiskUsage($diskPath),
            'network' => $this->readNetworkUsage(),
            'temperature' => $this->readTemperature(),
            'process_count' => $this->countProcesses(),
            'collected_at' => date('Y-m-d H:i:s'),
        ];
    }

    public function readCpuUsage(): float
    {
        $first = $this->readCpuStat();
        usleep(500000);
        $second = $this->readCpuStat();

        $idleDelta = $second['idle'] - $first['idle'];
        $totalDelta = $second['total'] - $first['total'];

        if ($totalDelta <= 0) {
            return 0.0;
        }

        $usage = 100 * (1 - ($idleDelta / $totalDelta));

        return round($usage, 2);
    }

    private function readCpuStat(): array
    {
        $line = @file('/proc/stat')[0] ?? '';

        if ($line === '') {
            return [
                'idle' => 0,
                'total' => 0
            ];
        }

        $parts = preg_split('/\s+/', trim($line));

        array_shift($parts);

        $values = array_map('intval', $parts);

        $idle = ($values[3] ?? 0) + ($values[4] ?? 0);
        $total = array_sum($values);

        return [
            'idle' => $idle,
            'total' => $total
        ];
    }

    public function readRamUsage(): array
    {
        $content = @file('/proc/meminfo', FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES);

        if (!$content) {
            return [
                'total_mb' => 0,
                'used_mb' => 0,
                'free_mb' => 0,
                'usage_percent' => 0.0
            ];
        }

        $memInfo = [];

        foreach ($content as $line) {
            if (preg_match('/^([^:]+):\s+(\d+)/', $line, $matches)) {
                $memInfo[$matches[1]] = (int) $matches[2];
            }
        }

        $totalKb = $memInfo['MemTotal'] ?? 0;
        $availableKb = $memInfo['MemAvailable'] ?? 0;

        $usedKb = max(0, $totalKb - $availableKb);

        $usagePercent = $totalKb > 0 ? ($usedKb / $totalKb) * 100 : 0;

        return [
            'total_mb' => round($totalKb / 1024, 2),
            'used_mb' => round($usedKb / 1024, 2),
            'free_mb' => round($availableKb / 1024, 2),
            'usage_percent' => round($usagePercent, 2)
        ];
    }

    public function readDiskUsage(string $path = '/'): array
    {
        $total = @disk_total_space($path);
        $free = @disk_free_space($path);

        if ($total === false || $free === false || $total <= 0) {
            return [
                'total_gb' => 0,
                'used_gb' => 0,
                'free_gb' => 0,
                'usage_percent' => 0.0
            ];
        }

        $used = $total - $free;
        $usagePercent = ($used / $total) * 100;

        return [
            'total_gb' => round($total / 1073741824, 2),
            'used_gb' => round($used / 1073741824, 2),
            'free_gb' => round($free / 1073741824, 2),
            'usage_percent' => round($usagePercent, 2)
        ];
    }

    public function readNetworkUsage(): array
    {
        $lines = @file('/proc/net/dev', FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES);

        if (!$lines || count($lines) < 3) {
            return [
                'bytes_in' => 0,
                'bytes_out' => 0
            ];
        }

        $bytesIn = 0;
        $bytesOut = 0;

        foreach ($lines as $index => $line) {
            if ($index < 2) {
                continue;
            }

            $line = trim($line);

            if (strpos($line, ':') === false) {
                continue;
            }

            [$interface, $data] = explode(':', $line, 2);
            $interface = trim($interface);

            if ($interface === 'lo') {
                continue;
            }

            $values = preg_split('/\s+/', trim($data));

            $bytesIn += (int) ($values[0] ?? 0);
            $bytesOut += (int) ($values[8] ?? 0);
        }

        return [
            'bytes_in' => $bytesIn,
            'bytes_out' => $bytesOut
        ];
    }

    public function readTemperature(): ?float
    {
        $thermalFiles = glob('/sys/class/thermal/thermal_zone*/temp');

        if (!$thermalFiles) {
            return null;
        }

        foreach ($thermalFiles as $file) {
            $raw = @file_get_contents($file);

            if ($raw === false) {
                continue;
            }

            $raw = trim($raw);

            if ($raw === '' || !is_numeric($raw)) {
                continue;
            }

            $value = (float) $raw;

            if ($value > 1000) {
                return round($value / 1000, 2);
            }

            return round($value, 2);
        }

        return null;
    }

    public function countProcesses(): int
    {
        $entries = @scandir('/proc');

        if (!$entries) {
            return 0;
        }

        $count = 0;

        foreach ($entries as $entry) {
            if (ctype_digit($entry)) {
                $count++;
            }
        }

        return $count;
    }
}
