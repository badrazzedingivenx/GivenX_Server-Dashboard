#  Monitoring SaaS Platform

##  Description

Monitoring SaaS est une plateforme web complète permettant de superviser des serveurs et des projets en temps réel.

Le système collecte des données système (CPU, RAM, état des services), analyse les anomalies, génère des alertes critiques et notifie les utilisateurs via email, dashboard et logs.

---

##  Objectifs du projet

* Superviser des serveurs et projets
* Détecter automatiquement les anomalies système
* Générer des alertes intelligentes
* Envoyer des notifications (email)
* Centraliser les logs système et utilisateur
* Fournir un dashboard moderne et interactif

---

##  Architecture du système

Le projet est basé sur une architecture **3-tiers** :

###  Frontend (React)

* Interface utilisateur moderne (Dark UI)
* Dashboard en temps réel
* Gestion des serveurs / projets / alertes
* Authentification utilisateur

###  Backend (PHP - API REST)

* Authentification sécurisée (token)
* Gestion des utilisateurs
* Gestion des serveurs et projets
* Moteur d’alertes
* Gestion des logs
* Envoi d’emails SMTP

###  Collector (Agent Linux)

* Collecte CPU, RAM, disque
* Vérifie état des processus
* Envoie les données au backend

---

## ⚙️rérequis

Avant installation, vous devez avoir :

* Node.js (v16+)
* npm
* PHP (8+)
* MySQL
* Composer
* Git

---

## nstallation

### 1. Cloner le projet

```bash
git clone https://github.com/KARAM022/GivenX_Server-Dashboard.git
cd monitoring-saas
```

---

### 2. Configuration Backend

```bash
cd backend
composer install
```

Créer le fichier `.env` :

```env
DB_HOST=localhost
DB_NAME=monitoring_saas
DB_USER=root
DB_PASS=

SMTP_HOST=sandbox.smtp.mailtrap.io
SMTP_PORT=2525
SMTP_USERNAME=YOUR_MAILTRAP_USERNAME
SMTP_PASSWORD=YOUR_MAILTRAP_PASSWORD
SMTP_ENCRYPTION=tls
MAIL_FROM_ADDRESS=monitoring@test.com
```

Lancer le serveur backend :

```bash
php -S localhost:8000 -t public index.php
```

---

### 3. Configuration Frontend

```bash
cd frontend
npm install
npm start
```

👉 Accès :
http://localhost:3000

---

### 4. Base de données

Créer la base :

```sql
CREATE DATABASE monitoring_saas;
```

Importer les tables nécessaires.

---

##  Authentification

* Login / Register
* Génération de token sécurisé
* Accès API via Bearer Token

---

##  Système d’alertes

Les alertes sont déclenchées en cas de :

* CPU élevé
* Serveur indisponible
* Processus arrêté

### Fonctionnalités :

* Création automatique d’alertes
* Stockage en base de données
* Affichage dans dashboard
* Notification email
* Historique des alertes

---

##  Notifications Email

* Intégration SMTP (Mailtrap ou Gmail)
* Envoi automatique lors d’alertes critiques

---

##  Dashboard

Le dashboard affiche :

* Nombre de serveurs
* Nombre de projets
* Nombre d’alertes
* Activité récente
* Statistiques système

---

##  Gestion des logs

### Logs système

* erreurs système
* événements techniques

### Logs utilisateur

* connexions
* actions administrateur

---

##  Fonctionnalités principales

✔ Authentification sécurisée
✔ Dashboard dynamique
✔ Gestion des serveurs
✔ Gestion des projets
✔ Gestion des alertes
✔ Gestion des utilisateurs (admin)
✔ Logs système et utilisateur
✔ Notifications email
✔ API REST complète

---

##  Structure du projet

```
monitoring-saas/
│
├── backend/
│   ├── app/
│   ├── public/
│   ├── storage/
│
├── frontend/
│   ├── src/
│   ├── public/
│
├── collector/
│
├── docs/
├── scripts/
└── README.md
```

---

##  Workflow du système

1. Le collector envoie les données
2. Le backend analyse
3. Détection d’anomalies
4. Création d’alerte
5. Envoi email
6. Affichage dans dashboard




**Doha Hanine Daoudi**

