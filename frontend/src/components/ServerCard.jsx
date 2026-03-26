/*
Rôle du fichier :
Ce composant affiche les informations d’un serveur sous forme de carte lisible.

Ce qu’il contient :
- le nom du serveur
- l’adresse IP
- l’hôte
- le système
- le statut actif ou non

Pourquoi il existe :
La page Serveurs doit être professionnelle et ne pas afficher du JSON brut.

Comment il s’intègre dans le projet :
ServersPage utilise ce composant pour afficher chaque serveur de façon claire.
*/

import React from "react";

export default function ServerCard({ server }) {
  return (
    <div style={styles.card}>
      <div style={styles.header}>
        <h3 style={styles.title}>{server.name}</h3>
        <span style={server.is_active ? styles.activeBadge : styles.inactiveBadge}>
          {server.is_active ? "Actif" : "Inactif"}
        </span>
      </div>

      <div style={styles.row}>
        <strong>IP :</strong> <span>{server.ip_address || "Non définie"}</span>
      </div>

      <div style={styles.row}>
        <strong>Host :</strong> <span>{server.host || "Non défini"}</span>
      </div>

      <div style={styles.row}>
        <strong>OS :</strong> <span>{server.os_type || "Inconnu"}</span>
      </div>

      <div style={styles.row}>
        <strong>Description :</strong> <span>{server.description || "Aucune description"}</span>
      </div>

      <div style={styles.row}>
        <strong>Créé le :</strong> <span>{server.created_at}</span>
      </div>
    </div>
  );
}

const styles = {
  card: {
    background: "#fff",
    borderRadius: "14px",
    padding: "20px",
    boxShadow: "0 4px 12px rgba(0,0,0,0.08)",
    display: "grid",
    gap: "10px",
  },
  header: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: "8px",
  },
  title: {
    margin: 0,
    color: "#0f172a",
  },
  row: {
    color: "#334155",
    lineHeight: 1.5,
  },
  activeBadge: {
    background: "#dcfce7",
    color: "#166534",
    padding: "4px 10px",
    borderRadius: "999px",
    fontSize: "12px",
    fontWeight: "bold",
  },
  inactiveBadge: {
    background: "#fee2e2",
    color: "#b91c1c",
    padding: "4px 10px",
    borderRadius: "999px",
    fontSize: "12px",
    fontWeight: "bold",
  },
};
