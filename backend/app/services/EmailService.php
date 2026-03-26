<?php

use PHPMailer\PHPMailer\PHPMailer;
use PHPMailer\PHPMailer\Exception;

class EmailService
{
    public static function send(string $to, string $subject, string $html): bool
    {
        $mail = new PHPMailer(true);

        try {
            $mail->isSMTP();
            $mail->Host = $_ENV['SMTP_HOST'] ?? 'sandbox.smtp.mailtrap.io';
            $mail->SMTPAuth = true;
            $mail->Username = $_ENV['SMTP_USERNAME'] ?? '';
            $mail->Password = $_ENV['SMTP_PASSWORD'] ?? '';
            $mail->Port = (int) ($_ENV['SMTP_PORT'] ?? 2525);
            $mail->SMTPSecure = false;
            $mail->SMTPAutoTLS = true;

            $fromEmail = $_ENV['MAIL_FROM_ADDRESS'] ?? 'noreply@example.com';
            $fromName = $_ENV['MAIL_FROM_NAME'] ?? 'Monitoring SaaS';

            $mail->setFrom($fromEmail, $fromName);
            $mail->addAddress($to);

            $mail->isHTML(true);
            $mail->CharSet = 'UTF-8';
            $mail->Subject = $subject;
            $mail->Body = $html;
            $mail->AltBody = strip_tags(str_replace(['<br>', '<br/>', '<br />'], "\n", $html));

            $mail->send();
            return true;
        } catch (Exception $e) {
            error_log('[MAILTRAP SMTP ERROR] ' . $mail->ErrorInfo);
            return false;
        }
    }

    public static function sendAlertEmail(string $to, array $alert): bool
    {
        $severity = strtoupper($alert['severity'] ?? 'INFO');
        $subject = '[Monitoring SaaS] Alerte ' . $severity;

        $html = '
            <html>
            <body style="font-family: Arial, sans-serif; background:#0b1020; color:#e5e7eb; padding:20px;">
                <div style="max-width:600px; margin:auto; background:#111827; border-radius:16px; padding:24px; border:1px solid rgba(148,163,184,0.15);">
                    <h2 style="color:#f8fafc; margin-top:0;">Nouvelle alerte détectée</h2>

                    <p><strong>Type :</strong> ' . htmlspecialchars($alert['type'] ?? '-') . '</p>
                    <p><strong>Sévérité :</strong> ' . htmlspecialchars($alert['severity'] ?? '-') . '</p>
                    <p><strong>Message :</strong> ' . htmlspecialchars($alert['message'] ?? '-') . '</p>
                    <p><strong>Serveur :</strong> ' . htmlspecialchars((string) ($alert['server_id'] ?? '-')) . '</p>
                    <p><strong>Projet :</strong> ' . htmlspecialchars((string) ($alert['project_id'] ?? '-')) . '</p>
                    <p><strong>Date :</strong> ' . date('Y-m-d H:i:s') . '</p>

                    <hr style="border:none; border-top:1px solid #334155; margin:20px 0;">

                    <p style="color:#94a3b8; font-size:14px;">
                        Cet email a été envoyé automatiquement par Monitoring SaaS via Mailtrap.
                    </p>
                </div>
            </body>
            </html>
        ';

        return self::send($to, $subject, $html);
    }
}
