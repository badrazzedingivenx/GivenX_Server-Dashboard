
# Collector

<!--
Rôle du fichier :
Ce fichier décrit le collecteur de métriques.

Ce qu’il contient :
- le rôle du collecteur
- son importance dans le projet

Pourquoi il existe :
Il explique la partie Linux qui récupère les informations système.

Comment il s’intègre dans le projet :
Le collecteur lit les métriques serveur et projet, puis les envoie au backend via l’API REST.
-->

## Rôle

Le collecteur est un composant exécuté sur Ubuntu/Linux.

## Responsabilités

- lire les métriques CPU, RAM, disque, réseau
- détecter les processus actifs
- surveiller les projets
- envoyer les métriques au backend
