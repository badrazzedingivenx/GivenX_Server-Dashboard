/*
Rôle du fichier :
Ce composant affiche un graphique simple des métriques serveur.

Ce qu’il contient :
- une courbe CPU
- une courbe RAM
- une courbe disque

Pourquoi il existe :
Le dashboard doit visualiser l’évolution des métriques dans le temps.

Comment il s’intègre dans le projet :
DashboardPage utilise ce composant avec les données de l’historique serveur.
*/
import React from "react";
import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer,
} from "recharts";

export default function ServerChart({ data = [] }) {
  return (
    <div style={styles.card}>
      <div style={styles.head}>
        <h3 style={styles.title}>Historique Serveur</h3>
        <p style={styles.subtitle}>Évolution CPU, RAM et disque</p>
      </div>

      <div style={{ width: "100%", height: 320 }}>
        <ResponsiveContainer>
          <LineChart data={data}>
            <CartesianGrid strokeDasharray="3 3" stroke="rgba(148,163,184,0.15)" />
            <XAxis dataKey="collected_at" stroke="#94a3b8" />
            <YAxis stroke="#94a3b8" />
            <Tooltip />
            <Legend />
            <Line type="monotone" dataKey="cpu_usage" name="CPU %" stroke="#6366f1" strokeWidth={2} dot={false} />
            <Line type="monotone" dataKey="ram_usage" name="RAM %" stroke="#3b82f6" strokeWidth={2} dot={false} />
            <Line type="monotone" dataKey="disk_usage" name="Disque %" stroke="#22c55e" strokeWidth={2} dot={false} />
          </LineChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}

const styles = {
  card: {
    background: "rgba(15, 23, 42, 0.88)",
    border: "1px solid rgba(148, 163, 184, 0.12)",
    borderRadius: "20px",
    padding: "20px",
    boxShadow: "0 10px 30px rgba(0,0,0,0.20)",
  },
  head: {
    marginBottom: "14px",
  },
  title: {
    margin: 0,
    color: "#f8fafc",
  },
  subtitle: {
    margin: "6px 0 0",
    color: "#94a3b8",
    fontSize: "14px",
  },
};
