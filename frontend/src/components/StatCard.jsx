/*
Rôle du fichier :
Ce composant affiche une carte de statistique dans le dashboard.

Ce qu’il contient :
- un titre
- une valeur principale
- une couleur de fond légère

Pourquoi il existe :
Le dashboard a besoin de cartes simples pour résumer rapidement les données.

Comment il s’intègre dans le projet :
DashboardPage utilise ce composant pour CPU, RAM, disque, alertes, etc.
*/

import React from "react";

export default function StatCard({ title, value, subtitle }) {
  return (
    <div style={styles.card}>
      <div style={styles.title}>{title}</div>
      <div style={styles.value}>{value}</div>
      {subtitle ? <div style={styles.subtitle}>{subtitle}</div> : null}
    </div>
  );
}

const styles = {
  card: {
    background: "rgba(15, 23, 42, 0.85)",
    border: "1px solid rgba(148, 163, 184, 0.12)",
    borderRadius: "18px",
    padding: "20px",
    boxShadow: "0 10px 30px rgba(0,0,0,0.25)",
  },
  title: {
    color: "#94a3b8",
    fontSize: "14px",
    marginBottom: "10px",
  },
  value: {
    fontSize: "30px",
    fontWeight: 700,
    color: "#f8fafc",
  },
  subtitle: {
    marginTop: "8px",
    color: "#64748b",
    fontSize: "13px",
  },
};
