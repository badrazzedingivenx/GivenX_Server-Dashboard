/*
Rôle du fichier :
Cette page affiche le dashboard administrateur.

Ce qu’il contient :
- le chargement des statistiques globales de la plateforme
- l’affichage des KPI administrateur sous forme de cartes

Pourquoi il existe :
Le cahier des charges demande un espace administrateur distinct permettant
de superviser globalement la plateforme.

Comment il s’intègre dans le projet :
Elle est accessible uniquement aux utilisateurs ayant le rôle admin.
*/
import React, { useEffect, useMemo, useState } from "react";
import client from "../api/client";
import MainLayout from "../layout/MainLayout";

const panelStyle = {
  background: "rgba(15, 23, 42, 0.88)",
  border: "1px solid rgba(148, 163, 184, 0.12)",
  borderRadius: "20px",
  padding: "20px",
  boxShadow: "0 10px 30px rgba(0,0,0,0.20)",
};

export default function AdminDashboardPage() {
  const [dashboard, setDashboard] = useState(null);
  const [systemLogs, setSystemLogs] = useState([]);
  const [userLogs, setUserLogs] = useState([]);
  const [alerts, setAlerts] = useState([]);
  const [loading, setLoading] = useState(true);

  const loadData = async () => {
    try {
      setLoading(true);

      const [dashboardRes, systemLogsRes, userLogsRes, alertsRes] = await Promise.all([
        client.get("/api/admin/dashboard"),
        client.get("/api/admin/logs/system"),
        client.get("/api/admin/logs/users"),
        client.get("/api/alerts"),
      ]);

      setDashboard(dashboardRes.data?.data || null);
      setSystemLogs(systemLogsRes.data?.data?.logs || []);
      setUserLogs(userLogsRes.data?.data?.logs || []);
      setAlerts(alertsRes.data?.data?.alerts || []);
    } catch (error) {
      console.error("Erreur chargement dashboard admin :", error);
      setDashboard(null);
      setSystemLogs([]);
      setUserLogs([]);
      setAlerts([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const stats = useMemo(() => {
    return {
      users: dashboard?.users_count ?? 0,
      servers: dashboard?.servers_count ?? 0,
      projects: dashboard?.projects_count ?? 0,
      alerts: alerts.length,
      criticalAlerts: alerts.filter((a) => (a.severity || "").toLowerCase() === "critical").length,
      openAlerts: alerts.filter((a) => !a.resolved_at && (a.status || "").toLowerCase() !== "resolved").length,
    };
  }, [dashboard, alerts]);

  return (
    <MainLayout>
      <div style={styles.pageHead}>
        <div>
          <h1 style={styles.title}>Administration</h1>
          <p style={styles.subtitle}>
            Vue globale de la plateforme, des utilisateurs, des alertes et des logs système
          </p>
        </div>
      </div>

      <div style={styles.statsGrid}>
        <StatCard title="Utilisateurs" value={stats.users} subtitle="Comptes enregistrés" />
        <StatCard title="Serveurs" value={stats.servers} subtitle="Infrastructure surveillée" />
        <StatCard title="Projets" value={stats.projects} subtitle="Services supervisés" />
        <StatCard title="Alertes" value={stats.alerts} subtitle="Total des alertes" />
        <StatCard title="Critiques" value={stats.criticalAlerts} subtitle="Priorité haute" />
        <StatCard title="Ouvertes" value={stats.openAlerts} subtitle="À traiter" />
      </div>

      <div style={styles.layout}>
        <div style={panelStyle}>
          <div style={styles.sectionHead}>
            <h3 style={styles.panelTitle}>Résumé système</h3>
          </div>

          {loading ? (
            <div style={styles.info}>Chargement...</div>
          ) : !dashboard ? (
            <div style={styles.info}>Aucune donnée dashboard disponible.</div>
          ) : (
            <div style={styles.summaryGrid}>
              <SummaryItem label="Utilisateurs actifs" value={dashboard.active_users_count ?? "-"} />
              <SummaryItem label="Utilisateurs bloqués" value={dashboard.blocked_users_count ?? "-"} />
              <SummaryItem label="Serveurs actifs" value={dashboard.active_servers_count ?? "-"} />
              <SummaryItem label="Projets running" value={dashboard.running_projects_count ?? "-"} />
              <SummaryItem label="Dernière mise à jour" value={dashboard.updated_at ?? "-"} />
            </div>
          )}
        </div>

        <div style={panelStyle}>
          <div style={styles.sectionHead}>
            <h3 style={styles.panelTitle}>Alertes récentes</h3>
            <span style={styles.counter}>{alerts.length} élément(s)</span>
          </div>

          {alerts.length === 0 ? (
            <div style={styles.info}>Aucune alerte récente.</div>
          ) : (
            <div style={styles.alertList}>
              {alerts.slice(0, 5).map((alert) => {
                const critical = (alert.severity || "").toLowerCase() === "critical";

                return (
                  <div key={alert.id} style={styles.alertCard}>
                    <div style={styles.alertTop}>
                      <strong style={{ color: "#f8fafc" }}>{alert.type || "Alerte"}</strong>
                      <span
                        style={{
                          ...styles.badge,
                          background: critical
                            ? "rgba(239,68,68,0.16)"
                            : "rgba(250,204,21,0.16)",
                          color: critical ? "#f87171" : "#facc15",
                        }}
                      >
                        {alert.severity || "info"}
                      </span>
                    </div>
                    <div style={styles.alertMessage}>{alert.message}</div>
                    <div style={styles.alertDate}>{alert.created_at}</div>
                  </div>
                );
              })}
            </div>
          )}
        </div>
      </div>

      <div style={styles.layout}>
        <div style={panelStyle}>
          <div style={styles.sectionHead}>
            <h3 style={styles.panelTitle}>Logs système</h3>
            <span style={styles.counter}>{systemLogs.length} élément(s)</span>
          </div>

          {systemLogs.length === 0 ? (
            <div style={styles.info}>Aucun log système.</div>
          ) : (
            <div style={{ overflowX: "auto" }}>
              <table>
                <thead>
                  <tr>
                    <th>Niveau</th>
                    <th>Message</th>
                    <th>Date</th>
                  </tr>
                </thead>
                <tbody>
                  {systemLogs.slice(0, 10).map((log) => (
                    <tr key={log.id}>
                      <td>
                        <span style={getLevelBadgeStyle(log.level)}>
                          {log.level || "-"}
                        </span>
                      </td>
                      <td>{log.message || "-"}</td>
                      <td>{log.created_at || "-"}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>

        <div style={panelStyle}>
          <div style={styles.sectionHead}>
            <h3 style={styles.panelTitle}>Logs utilisateurs</h3>
            <span style={styles.counter}>{userLogs.length} élément(s)</span>
          </div>

          {userLogs.length === 0 ? (
            <div style={styles.info}>Aucun log utilisateur.</div>
          ) : (
            <div style={{ overflowX: "auto" }}>
              <table>
                <thead>
                  <tr>
                    <th>Utilisateur</th>
                    <th>Action</th>
                    <th>Détails</th>
                    <th>Date</th>
                  </tr>
                </thead>
                <tbody>
                  {userLogs.slice(0, 10).map((log) => (
                    <tr key={log.id}>
                      <td>{log.name || "-"}</td>
                      <td>
                        <span style={getActionBadgeStyle(log.action)}>
                          {log.action || "-"}
                        </span>
                      </td>
                      <td>{log.details || "-"}</td>
                      <td>{log.created_at || "-"}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </MainLayout>
  );
}

function StatCard({ title, value, subtitle }) {
  return (
    <div style={styles.statCard}>
      <div style={styles.statTitle}>{title}</div>
      <div style={styles.statValue}>{value}</div>
      <div style={styles.statSubtitle}>{subtitle}</div>
    </div>
  );
}

function SummaryItem({ label, value }) {
  return (
    <div style={styles.summaryItem}>
      <div style={styles.summaryLabel}>{label}</div>
      <div style={styles.summaryValue}>{value}</div>
    </div>
  );
}

function getLevelBadgeStyle(level) {
  const value = (level || "").toLowerCase();

  if (value === "error") {
    return {
      ...styles.badge,
      background: "rgba(239,68,68,0.16)",
      color: "#f87171",
    };
  }

  if (value === "warning") {
    return {
      ...styles.badge,
      background: "rgba(250,204,21,0.16)",
      color: "#facc15",
    };
  }

  return {
    ...styles.badge,
    background: "rgba(59,130,246,0.16)",
    color: "#93c5fd",
  };
}

function getActionBadgeStyle(action) {
  const value = (action || "").toLowerCase();

  if (value.includes("login")) {
    return {
      ...styles.badge,
      background: "rgba(34,197,94,0.16)",
      color: "#4ade80",
    };
  }

  if (value.includes("delete") || value.includes("block")) {
    return {
      ...styles.badge,
      background: "rgba(239,68,68,0.16)",
      color: "#f87171",
    };
  }

  return {
    ...styles.badge,
    background: "rgba(59,130,246,0.16)",
    color: "#93c5fd",
  };
}

const styles = {
  pageHead: {
    marginBottom: "22px",
  },
  title: {
    margin: 0,
    color: "#f8fafc",
    fontSize: "28px",
    fontWeight: 700,
  },
  subtitle: {
    marginTop: "8px",
    color: "#94a3b8",
  },
  statsGrid: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fit, minmax(180px, 1fr))",
    gap: "18px",
    marginBottom: "20px",
  },
  statCard: {
    background: "rgba(15, 23, 42, 0.85)",
    border: "1px solid rgba(148, 163, 184, 0.12)",
    borderRadius: "18px",
    padding: "20px",
    boxShadow: "0 10px 30px rgba(0,0,0,0.25)",
  },
  statTitle: {
    color: "#94a3b8",
    fontSize: "14px",
    marginBottom: "10px",
  },
  statValue: {
    fontSize: "30px",
    fontWeight: 700,
    color: "#f8fafc",
  },
  statSubtitle: {
    marginTop: "8px",
    color: "#64748b",
    fontSize: "13px",
  },
  layout: {
    display: "grid",
    gridTemplateColumns: "1fr 1fr",
    gap: "20px",
    marginBottom: "20px",
  },
  sectionHead: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    gap: "12px",
    marginBottom: "18px",
  },
  panelTitle: {
    margin: 0,
    color: "#f8fafc",
  },
  counter: {
    color: "#94a3b8",
    fontSize: "14px",
  },
  info: {
    color: "#94a3b8",
    padding: "10px 0",
  },
  summaryGrid: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fit, minmax(180px, 1fr))",
    gap: "14px",
  },
  summaryItem: {
    background: "rgba(30, 41, 59, 0.78)",
    border: "1px solid rgba(148,163,184,0.10)",
    borderRadius: "14px",
    padding: "14px",
  },
  summaryLabel: {
    color: "#94a3b8",
    fontSize: "13px",
    marginBottom: "8px",
  },
  summaryValue: {
    color: "#f8fafc",
    fontWeight: 700,
    fontSize: "18px",
  },
  alertList: {
    display: "grid",
    gap: "12px",
  },
  alertCard: {
    background: "rgba(30, 41, 59, 0.78)",
    border: "1px solid rgba(148,163,184,0.10)",
    borderRadius: "14px",
    padding: "14px",
  },
  alertTop: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    gap: "12px",
    marginBottom: "8px",
  },
  alertMessage: {
    color: "#cbd5e1",
    marginBottom: "8px",
  },
  alertDate: {
    color: "#94a3b8",
    fontSize: "12px",
  },
  badge: {
    padding: "5px 10px",
    borderRadius: "999px",
    fontSize: "12px",
    fontWeight: 700,
    textTransform: "capitalize",
    display: "inline-block",
  },
};
