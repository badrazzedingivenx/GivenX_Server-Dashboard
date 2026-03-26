/*
Rôle du fichier :
Cette page affiche toutes les alertes de l’utilisateur connecté.

Ce qu’il contient :
- le chargement des alertes depuis l’API
- un bouton pour régénérer les alertes
- un affichage sous forme de liste stylée

Pourquoi il existe :
Le cahier des charges impose une gestion des alertes automatiques.

Comment il s’intègre dans le projet :
Elle est accessible depuis le menu latéral et complète le dashboard principal.
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

export default function AlertsPage() {
  const [alerts, setAlerts] = useState([]);
  const [filteredAlerts, setFilteredAlerts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [resolvingId, setResolvingId] = useState(null);

  const [filters, setFilters] = useState({
    severity: "",
    status: "",
    search: "",
  });

  const loadAlerts = async () => {
    try {
      const res = await client.get("/api/alerts");
      const items = res.data?.data?.alerts || [];
      setAlerts(items);
      setFilteredAlerts(items);
    } catch (error) {
      console.error("Erreur chargement alertes :", error);
      setAlerts([]);
      setFilteredAlerts([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadAlerts();

    const interval = setInterval(() => {
      loadAlerts();
    }, 5000);

    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    const next = alerts.filter((alert) => {
      const severityOk =
        !filters.severity || (alert.severity || "").toLowerCase() === filters.severity.toLowerCase();

      const statusValue = alert.resolved_at ? "resolved" : "active";
      const statusOk = !filters.status || statusValue === filters.status;

      const searchTarget = [
        alert.type,
        alert.message,
        alert.server_name,
        alert.project_name,
        alert.severity,
      ]
        .filter(Boolean)
        .join(" ")
        .toLowerCase();

      const searchOk =
        !filters.search || searchTarget.includes(filters.search.toLowerCase());

      return severityOk && statusOk && searchOk;
    });

    setFilteredAlerts(next);
  }, [alerts, filters]);

  const stats = useMemo(() => {
    const active = alerts.filter((a) => !a.resolved_at).length;
    const resolved = alerts.filter((a) => !!a.resolved_at).length;
    const critical = alerts.filter((a) => (a.severity || "").toLowerCase() === "critical").length;
    const warning = alerts.filter((a) => (a.severity || "").toLowerCase() === "warning").length;

    return { active, resolved, critical, warning };
  }, [alerts]);

  const handleResolve = async (id) => {
    try {
      setResolvingId(id);
      await client.post(`/api/alerts/${id}/resolve`);
      await loadAlerts();
    } catch (error) {
      console.error("Erreur résolution alerte :", error);
    } finally {
      setResolvingId(null);
    }
  };

  const getSeverityStyle = (severity) => {
    const value = (severity || "").toLowerCase();

    if (value === "critical") {
      return {
        background: "rgba(239, 68, 68, 0.16)",
        color: "#f87171",
      };
    }

    if (value === "warning") {
      return {
        background: "rgba(250, 204, 21, 0.16)",
        color: "#facc15",
      };
    }

    return {
      background: "rgba(59, 130, 246, 0.16)",
      color: "#93c5fd",
    };
  };

  const getStatusStyle = (alert) => {
    if (alert.resolved_at) {
      return {
        background: "rgba(34, 197, 94, 0.16)",
        color: "#4ade80",
        label: "Résolue",
      };
    }

    return {
      background: "rgba(244, 63, 94, 0.16)",
      color: "#fb7185",
      label: "Active",
    };
  };

  return (
    <MainLayout>
      <div style={styles.pageHead}>
        <div>
          <h1 style={styles.title}>Alertes</h1>
          <p style={styles.subtitle}>
            Suivi des alertes critiques, warning et événements système
          </p>
        </div>
      </div>

      <div style={styles.statsGrid}>
        <StatCard title="Alertes actives" value={stats.active} subtitle="Non résolues" />
        <StatCard title="Alertes résolues" value={stats.resolved} subtitle="Historique traité" />
        <StatCard title="Critiques" value={stats.critical} subtitle="Priorité haute" />
        <StatCard title="Warnings" value={stats.warning} subtitle="À surveiller" />
      </div>

      <div style={{ ...panelStyle, marginBottom: "20px" }}>
        <h3 style={styles.panelTitle}>Filtres</h3>

        <div style={styles.filtersGrid}>
          <div>
            <label style={styles.label}>Recherche</label>
            <input
              type="text"
              placeholder="Type, message, projet, serveur..."
              value={filters.search}
              onChange={(e) => setFilters({ ...filters, search: e.target.value })}
              style={styles.input}
            />
          </div>

          <div>
            <label style={styles.label}>Sévérité</label>
            <select
              value={filters.severity}
              onChange={(e) => setFilters({ ...filters, severity: e.target.value })}
              style={styles.input}
            >
              <option value="">Toutes</option>
              <option value="critical">Critical</option>
              <option value="warning">Warning</option>
              <option value="info">Info</option>
            </select>
          </div>

          <div>
            <label style={styles.label}>Statut</label>
            <select
              value={filters.status}
              onChange={(e) => setFilters({ ...filters, status: e.target.value })}
              style={styles.input}
            >
              <option value="">Tous</option>
              <option value="active">Actives</option>
              <option value="resolved">Résolues</option>
            </select>
          </div>
        </div>
      </div>

      <div style={panelStyle}>
        <div style={styles.headerRow}>
          <h3 style={styles.panelTitle}>Liste des alertes</h3>
          <span style={styles.counter}>{filteredAlerts.length} élément(s)</span>
        </div>

        {loading ? (
          <div style={styles.info}>Chargement des alertes...</div>
        ) : filteredAlerts.length === 0 ? (
          <div style={styles.info}>Aucune alerte trouvée.</div>
        ) : (
          <div style={styles.list}>
            {filteredAlerts.map((alert) => {
              const severityStyle = getSeverityStyle(alert.severity);
              const statusStyle = getStatusStyle(alert);

              return (
                <div key={alert.id} style={styles.card}>
                  <div style={styles.cardTop}>
                    <div>
                      <div style={styles.cardTitleRow}>
                        <h4 style={styles.cardTitle}>{alert.type || "Alerte"}</h4>

                        <span
                          style={{
                            ...styles.badge,
                            background: severityStyle.background,
                            color: severityStyle.color,
                          }}
                        >
                          {alert.severity || "info"}
                        </span>

                        <span
                          style={{
                            ...styles.badge,
                            background: statusStyle.background,
                            color: statusStyle.color,
                          }}
                        >
                          {statusStyle.label}
                        </span>
                      </div>

                      <p style={styles.message}>
                        {alert.message || "Aucun message disponible"}
                      </p>
                    </div>

                    {!alert.resolved_at && (
                      <button
                        style={styles.resolveButton}
                        onClick={() => handleResolve(alert.id)}
                        disabled={resolvingId === alert.id}
                      >
                        {resolvingId === alert.id ? "Résolution..." : "Résoudre"}
                      </button>
                    )}
                  </div>

                  <div style={styles.metaGrid}>
                    <MetaItem label="ID" value={alert.id} />
                    <MetaItem label="Serveur" value={alert.server_name || alert.server_id || "-"} />
                    <MetaItem label="Projet" value={alert.project_name || alert.project_id || "-"} />
                    <MetaItem label="Créée le" value={alert.created_at || "-"} />
                    <MetaItem label="Résolue le" value={alert.resolved_at || "-"} />
                  </div>
                </div>
              );
            })}
          </div>
        )}
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

function MetaItem({ label, value }) {
  return (
    <div style={styles.metaItem}>
      <div style={styles.metaLabel}>{label}</div>
      <div style={styles.metaValue}>{value}</div>
    </div>
  );
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
  panelTitle: {
    margin: 0,
    color: "#f8fafc",
  },
  statsGrid: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))",
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
  filtersGrid: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))",
    gap: "16px",
    marginTop: "16px",
  },
  label: {
    display: "block",
    color: "#cbd5e1",
    marginBottom: "8px",
    fontSize: "14px",
    fontWeight: 500,
  },
  input: {
    width: "100%",
    padding: "12px 14px",
    borderRadius: "10px",
    border: "1px solid rgba(148,163,184,0.2)",
    background: "#0f172a",
    color: "#fff",
    outline: "none",
  },
  headerRow: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    marginBottom: "16px",
    gap: "12px",
  },
  counter: {
    color: "#94a3b8",
    fontSize: "14px",
  },
  info: {
    color: "#94a3b8",
    padding: "10px 0",
  },
  list: {
    display: "grid",
    gap: "14px",
  },
  card: {
    background: "rgba(30, 41, 59, 0.82)",
    border: "1px solid rgba(148,163,184,0.12)",
    borderRadius: "18px",
    padding: "18px",
  },
  cardTop: {
    display: "flex",
    justifyContent: "space-between",
    gap: "16px",
    alignItems: "flex-start",
    marginBottom: "14px",
  },
  cardTitleRow: {
    display: "flex",
    flexWrap: "wrap",
    alignItems: "center",
    gap: "10px",
    marginBottom: "8px",
  },
  cardTitle: {
    margin: 0,
    color: "#f8fafc",
    fontSize: "18px",
  },
  message: {
    margin: 0,
    color: "#cbd5e1",
    lineHeight: 1.5,
  },
  badge: {
    padding: "5px 10px",
    borderRadius: "999px",
    fontSize: "12px",
    fontWeight: 700,
    textTransform: "capitalize",
  },
  resolveButton: {
    background: "linear-gradient(135deg, #6366f1, #3b82f6)",
    color: "#fff",
    border: "none",
    padding: "10px 14px",
    borderRadius: "10px",
    cursor: "pointer",
    fontWeight: 600,
    minWidth: "110px",
  },
  metaGrid: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fit, minmax(160px, 1fr))",
    gap: "12px",
  },
  metaItem: {
    background: "rgba(15, 23, 42, 0.65)",
    border: "1px solid rgba(148,163,184,0.08)",
    borderRadius: "12px",
    padding: "12px",
  },
  metaLabel: {
    color: "#94a3b8",
    fontSize: "12px",
    marginBottom: "6px",
    textTransform: "uppercase",
    letterSpacing: "0.04em",
  },
  metaValue: {
    color: "#f8fafc",
    fontSize: "14px",
    wordBreak: "break-word",
  },
};
