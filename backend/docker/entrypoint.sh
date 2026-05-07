#!/bin/sh
set -e

PORT="${PORT:-80}"

# Render injects $PORT at runtime. Apache is configured for 80 by default.
# Patch both the Listen directive and the VirtualHost port before starting.
sed -i "s/^Listen 80$/Listen ${PORT}/" /etc/apache2/ports.conf
sed -i "s/<VirtualHost \*:80>/<VirtualHost *:${PORT}>/" /etc/apache2/sites-enabled/000-default.conf

exec "$@"
