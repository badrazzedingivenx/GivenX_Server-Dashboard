FROM php:8.2-apache

# ── PHP extensions ────────────────────────────────────────────────────────────
RUN docker-php-ext-install pdo pdo_mysql opcache

# ── Apache modules ────────────────────────────────────────────────────────────
RUN a2enmod rewrite headers

# ── Production PHP config ─────────────────────────────────────────────────────
COPY docker/php.ini /usr/local/etc/php/conf.d/production.ini

# ── Apache VirtualHost (public/ as docroot) ───────────────────────────────────
COPY docker/vhost.conf /etc/apache2/sites-available/000-default.conf

# ── Entrypoint: bind Apache to $PORT at runtime ───────────────────────────────
COPY docker/entrypoint.sh /usr/local/bin/entrypoint.sh
RUN chmod +x /usr/local/bin/entrypoint.sh

# ── Composer ─────────────────────────────────────────────────────────────────
COPY --from=composer:2 /usr/bin/composer /usr/bin/composer

# ── Application files ─────────────────────────────────────────────────────────
WORKDIR /var/www/html
COPY . .

# ── Install production dependencies ──────────────────────────────────────────
RUN composer install --no-dev --optimize-autoloader --no-interaction --no-progress

# ── Storage directory + permissions ──────────────────────────────────────────
RUN mkdir -p storage/logs \
    && chown -R www-data:www-data /var/www/html \
    && chmod -R 755 storage

EXPOSE 80

ENTRYPOINT ["entrypoint.sh"]
CMD ["apache2-foreground"]
