/*
Rôle du fichier :
Ce composant affiche la liste des alertes.

Ce qu’il contient :
- une liste simple d’alertes
- le type
- la sévérité
- le message

Pourquoi il existe :
Le dashboard doit montrer rapidement les incidents détectés.

Comment il s’intègre dans le projet :
Utilisé dans DashboardPage et AlertsPage.
*/
import React from "react";

export default function AlertList({ alerts = [] }) {
  if (!alerts.length) {
    return <div style={styles.empty}>Aucune alerte.</div>;
  }

  return (
    <div style={styles.wrap}>
      {alerts.map((alert) => {
        const critical = alert.severity === "critical";

        return (
          <div key={alert.id} style={styles.item}>
            <div style={styles.top}>
              <span
                style={{
                  ...styles.badge,
                  color: critical ? "#f87171" : "#facc15",
                  background: critical
                    ? "rgba(239,68,68,0.15)"
                    : "rgba(250,204,21,0.15)",
                }}
              >
                {alert.severity}
              </span>

              <span style={styles.date}>{alert.created_at}</span>
            </div>

            <div style={styles.type}>{alert.type}</div>
            <div style={styles.message}>{alert.message}</div>
          </div>
        );
      })}
    </div>
  );
}

const styles = {
  wrap: {
    display: "flex",
    flexDirection: "column",
    gap: "12px",
  },
  item: {
    padding: "14px",
    borderRadius: "14px",
    background: "rgba(30, 41, 59, 0.75)",
    border: "1px solid rgba(148,163,184,0.12)",
  },
  top: {
    display: "flex",
    justifyContent: "space-between",
    marginBottom: "8px",
  },
  badge: {
    padding: "4px 10px",
    borderRadius: "999px",
    fontSize: "12px",
    fontWeight: 700,
    textTransform: "capitalize",
  },
  date: {
    color: "#94a3b8",
    fontSize: "12px",
  },
  type: {
    color: "#cbd5e1",
    fontWeight: 700,
    marginBottom: "6px",
  },
  message: {
    color: "#f8fafc",
  },
  empty: {
    background: "rgba(15, 23, 42, 0.85)",
    border: "1px solid rgba(148, 163, 184, 0.12)",
    padding: "16px",
    borderRadius: "14px",
  },
};
