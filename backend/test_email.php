<?php
require_once __DIR__ . '/vendor/autoload.php';
require_once __DIR__ . '/app/services/EmailService.php';

function loadEnvFile(string $path): void
{
    if (!file_exists($path)) {
        return;
    }

    $lines = file($path, FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES);

    foreach ($lines as $line) {
        $line = trim($line);

        if ($line === '' || str_starts_with($line, '#')) {
            continue;
        }

        [$name, $value] = array_pad(explode('=', $line, 2), 2, '');

        $name = trim($name);
        $value = trim($value);

        if ($name !== '') {
            $_ENV[$name] = $value;
            putenv($name . '=' . $value);
        }
    }
}

loadEnvFile(__DIR__ . '/.env');

$ok = EmailService::send(
    'haninedaoudidoha@gmail.com',
    'Test SMTP Monitoring SaaS',
    '<h1>Test email OK</h1><p>Le SMTP fonctionne.</p>'
);

var_dump($ok);
