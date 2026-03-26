# Backend

<!--
Rôle du fichier :
Ce fichier décrit la partie backend du projet.

Ce qu’il contient :
- le rôle du backend
- la structure principale
- la logique générale

Pourquoi il existe :
Il aide à comprendre l’organisation de l’API REST PHP.

Comment il s’intègre dans le projet :
Il documente la couche serveur qui communique avec le frontend, la base de données et le collecteur.
-->

## Rôle

Le backend fournit une API REST en PHP natif.

## Responsabilités

- authentification
- gestion des utilisateurs
- gestion des serveurs
- gestion des projets
- stockage des métriques
- gestion des alertes
- gestion des logs

## Structure principale

- `public/` : point d’entrée HTTP
- `app/config/` : configuration
- `app/core/` : classes techniques de base
- `app/controllers/` : contrôleurs API
- `app/models/` : modèles
- `app/services/` : logique métier
- `app/middlewares/` : contrôle d’accès
- `app/routes/` : définition des routes
- `storage/logs/` : fichiers de logs
