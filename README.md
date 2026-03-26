# Monitoring SaaS

<!--
Rôle du fichier :
Ce fichier présente le projet dans son ensemble.

Ce qu’il contient :
- le nom du projet
- son objectif principal
- la stack technique
- la structure générale

Pourquoi il existe :
Il sert de point d’entrée pour comprendre rapidement le projet.

Comment il s’intègre dans le projet :
C’est le document principal de présentation globale.
-->

## Présentation

Monitoring SaaS est une plateforme web de supervision de serveurs et de projets.

## Objectifs

- surveiller les ressources serveur
- surveiller des projets/applications
- générer des alertes automatiques
- afficher des tableaux de bord administrateur et utilisateur
- garantir l’isolation stricte des données entre utilisateurs

## Stack technique

- Backend : PHP natif (API REST)
- Frontend : React avec Create React App
- Base de données : MySQL
- Système : Ubuntu / Linux

## Structure du projet

- `backend/` : API REST PHP
- `frontend/` : application React
- `collector/` : collecteur de métriques Linux
- `docs/` : documentation technique
- `scripts/` : scripts utilitaires
