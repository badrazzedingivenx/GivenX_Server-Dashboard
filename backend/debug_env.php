<?php
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

echo "SMTP_HOST=" . ($_ENV['SMTP_HOST'] ?? '') . PHP_EOL;
echo "SMTP_PORT=" . ($_ENV['SMTP_PORT'] ?? '') . PHP_EOL;
echo "SMTP_USERNAME=" . ($_ENV['SMTP_USERNAME'] ?? '') . PHP_EOL;
echo "SMTP_PASSWORD_LENGTH=" . strlen($_ENV['SMTP_PASSWORD'] ?? '') . PHP_EOL;
echo "SMTP_ENCRYPTION=" . ($_ENV['SMTP_ENCRYPTION'] ?? '') . PHP_EOL;
echo "MAIL_FROM_ADDRESS=" . ($_ENV['MAIL_FROM_ADDRESS'] ?? '') . PHP_EOL;
