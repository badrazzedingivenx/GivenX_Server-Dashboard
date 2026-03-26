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

const inputStyle = {
  width: "100%",
  padding: "12px 14px",
  borderRadius: "10px",
  border: "1px solid rgba(148,163,184,0.2)",
  background: "#0f172a",
  color: "#fff",
  outline: "none",
};

export default function LogsPage() {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const [filters, setFilters] = useState({
    search: "",
    action: "",
  });

  const loadLogs = async () => {
    try {
      setLoading(true);
      setError("");

      const res = await client.get("/api/logs/activity");
      setLogs(res.data?.data?.logs || []);
    } catch (err) {
      console.error("Erreur chargement logs :", err);
      setError("Impossible de charger les logs.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadLogs();
  }, []);

  const filteredLogs = useMemo(() => {
    return logs.filter((log) => {
      const searchTarget = [
        log.name,
        log.action,
        log.details,
        log.created_at,
      ]
        .filter(Boolean)
        .join(" ")
        .toLowerCase();

      const searchOk =
        !filters.search || searchTarget.includes(filters.search.toLowerCase());

      const actionOk =
        !filters.action ||
        (log.action || "").toLowerCase().includes(filters.action.toLowerCase());

      return searchOk && actionOk;
    });
  }, [logs, filters]);

  const stats = useMemo(() => {
    const total = logs.length;
    const loginCount = logs.filter((l) =>
      (l.action || "").toLowerCase().includes("login")
    ).length;
    const logoutCount = logs.filter((l) =>
      (l.action || "").toLowerCase().includes("logout")
    ).length;
    const createCount = logs.filter((l) => {
      const action = (l.action || "").toLowerCase();
      return (
        action.includes("create") ||
        action.includes("created") ||
        action.includes("register")
      );
    }).length;

    return {
      total,
      loginCount,
      logoutCount,
      createCount,
    };
  }, [logs]);

  return (
    <MainLayout>
      <div style={styles.pageHead}>
        <div>
          <h1 style={styles.title}>Mes logs</h1>
          <p style={styles.subtitle}>
            Consulte l’historique de tes actions dans la plateforme
          </p>
        </div>
      </div>

      <div style={styles.statsGrid}>
        <StatCard title="Total logs" value={stats.total} subtitle="Toutes les actions" />
        <StatCard title="Connexions" value={stats.loginCount} subtitle="Actions login" />
        <StatCard title="Déconnexions" value={stats.logoutCount} subtitle="Actions logout" />
        <StatCard title="Créations" value={stats.createCount} subtitle="Actions create/register" />
      </div>

      <div style={{ ...panelStyle, marginBottom: "20px" }}>
        <h3 style={styles.panelTitle}>Filtres</h3>

        <div style={styles.filtersGrid}>
          <div>
            <label style={styles.label}>Recherche</label>
            <input
              type="text"
              placeholder="Nom, action, détails..."
              value={filters.search}
              onChange={(e) =>
                setFilters({ ...filters, search: e.target.value })
              }
              style={inputStyle}
            />
          </div>

          <div>
            <label style={styles.label}>Action</label>
            <input
              type="text"
              placeholder="Ex: login"
              value={filters.action}
              onChange={(e) =>
                setFilters({ ...filters, action: e.target.value })
              }
              style={inputStyle}
            />
          </div>
        </div>
      </div>

      <div style={panelStyle}>
        <div style={styles.headerRow}>
          <h3 style={styles.panelTitle}>Historique des actions</h3>
          <span style={styles.counter}>{filteredLogs.length} élément(s)</span>
        </div>

        {loading ? (
          <div style={styles.info}>Chargement des logs...</div>
        ) : error ? (
          <div style={styles.error}>{error}</div>
        ) : filteredLogs.length === 0 ? (
          <div style={styles.info}>Aucun log trouvé.</div>
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
                {filteredLogs.map((log) => (
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

function getActionBadgeStyle(action) {
  const value = (action || "").toLowerCase();

  if (value.includes("login")) {
    return {
      ...styles.badge,
      background: "rgba(34,197,94,0.16)",
      color: "#4ade80",
    };
  }

  if (value.includes("logout")) {
    return {
      ...styles.badge,
      background: "rgba(148,163,184,0.16)",
      color: "#cbd5e1",
    };
  }

  if (
    value.includes("delete") ||
    value.includes("blocked") ||
    value.includes("error")
  ) {
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
  error: {
    color: "#f87171",
    padding: "10px 0",
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
