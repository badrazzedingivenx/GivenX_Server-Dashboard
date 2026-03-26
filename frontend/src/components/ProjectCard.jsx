/*
Rôle du fichier :
Ce composant affiche les informations d’un projet surveillé sous forme de carte.

Ce qu’il contient :
- le nom du projet
- le serveur associé
- le port
- le nom du processus
- le statut
- le chemin du projet

Pourquoi il existe :
La page Projets doit afficher les données métier de façon lisible et professionnelle.

Comment il s’intègre dans le projet :
ProjectsPage utilise ce composant pour afficher chaque projet surveillé.
*/

import React from "react";

export default function ProjectCard({ project }) {
  const statusStyle =
    project.status === "running"
      ? styles.runningBadge
      : project.status === "warning"
      ? styles.warningBadge
      : project.status === "stopped"
      ? styles.stoppedBadge
      : styles.unknownBadge;

  return (
    <div style={styles.card}>
      <div style={styles.header}>
        <h3 style={styles.title}>{project.name}</h3>
        <span style={statusStyle}>{project.status}</span>
      </div>

      <div style={styles.row}>
        <strong>Serveur :</strong> <span>{project.server_name || "Non défini"}</span>
      </div>

      <div style={styles.row}>
        <strong>Port :</strong> <span>{project.port ?? "Non défini"}</span>
      </div>

      <div style={styles.row}>
        <strong>Processus :</strong> <span>{project.process_name || "Non défini"}</span>
      </div>

      <div style={styles.row}>
        <strong>Chemin :</strong> <span>{project.project_path || "Non défini"}</span>
      </div>

      <div style={styles.row}>
        <strong>Description :</strong> <span>{project.description || "Aucune description"}</span>
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
  runningBadge: {
    background: "#dcfce7",
    color: "#166534",
    padding: "4px 10px",
    borderRadius: "999px",
    fontSize: "12px",
    fontWeight: "bold",
    textTransform: "capitalize",
  },
  warningBadge: {
    background: "#fef3c7",
    color: "#92400e",
    padding: "4px 10px",
    borderRadius: "999px",
    fontSize: "12px",
    fontWeight: "bold",
    textTransform: "capitalize",
  },
  stoppedBadge: {
    background: "#fee2e2",
    color: "#b91c1c",
    padding: "4px 10px",
    borderRadius: "999px",
    fontSize: "12px",
    fontWeight: "bold",
    textTransform: "capitalize",
  },
  unknownBadge: {
    background: "#e2e8f0",
    color: "#334155",
    padding: "4px 10px",
    borderRadius: "999px",
    fontSize: "12px",
    fontWeight: "bold",
    textTransform: "capitalize",
  },
};
