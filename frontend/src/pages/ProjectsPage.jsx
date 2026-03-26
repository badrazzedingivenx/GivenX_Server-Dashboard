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

export default function ProjectsPage() {
  const [projects, setProjects] = useState([]);
  const [servers, setServers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [editingProjectId, setEditingProjectId] = useState(null);
  const [error, setError] = useState("");
  const [search, setSearch] = useState("");

  const [form, setForm] = useState({
    name: "",
    server_id: "",
    path: "",
    process_name: "",
    port: "",
    status: "running",
  });

  const loadData = async () => {
    try {
      setLoading(true);
      setError("");

      const [projectsRes, serversRes] = await Promise.all([
        client.get("/api/projects"),
        client.get("/api/servers"),
      ]);

      setProjects(projectsRes.data?.data?.projects || []);
      setServers(serversRes.data?.data?.servers || []);
    } catch (err) {
      console.error("Erreur chargement projets :", err);
      setError("Impossible de charger les projets.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const resetForm = () => {
    setForm({
      name: "",
      server_id: "",
      path: "",
      process_name: "",
      port: "",
      status: "running",
    });
    setEditingProjectId(null);
    setError("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!form.name.trim()) {
      setError("Le nom du projet est obligatoire.");
      return;
    }

    if (!form.server_id) {
      setError("Le serveur est obligatoire.");
      return;
    }

    try {
      setSaving(true);
      setError("");

      const payload = {
        name: form.name.trim(),
        server_id: Number(form.server_id),
        path: form.path.trim(),
        process_name: form.process_name.trim(),
        port: form.port ? Number(form.port) : null,
        status: form.status,
      };

      if (editingProjectId) {
        await client.put(`/api/projects/${editingProjectId}`, payload);
      } else {
        await client.post("/api/projects", payload);
      }

      resetForm();
      await loadData();
    } catch (err) {
      console.error("Erreur sauvegarde projet :", err);
      setError("Impossible d’enregistrer le projet.");
    } finally {
      setSaving(false);
    }
  };

  const handleEdit = (project) => {
    setEditingProjectId(project.id);
    setError("");

    setForm({
      name: project.name || "",
      server_id: project.server_id ? String(project.server_id) : "",
      path: project.path || "",
      process_name: project.process_name || "",
      port: project.port != null ? String(project.port) : "",
      status: project.status || "running",
    });

    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const handleDelete = async (projectId) => {
    const confirmed = window.confirm("Supprimer ce projet ?");
    if (!confirmed) return;

    try {
      await client.delete(`/api/projects/${projectId}`);

      if (editingProjectId === projectId) {
        resetForm();
      }

      await loadData();
    } catch (err) {
      console.error("Erreur suppression projet :", err);
      setError("Impossible de supprimer le projet.");
    }
  };

  const filteredProjects = useMemo(() => {
    return projects.filter((project) => {
      const target = [
        project.name,
        project.path,
        project.process_name,
        project.status,
        project.server_name,
      ]
        .filter(Boolean)
        .join(" ")
        .toLowerCase();

      return target.includes(search.toLowerCase());
    });
  }, [projects, search]);

  const stats = useMemo(() => {
    return {
      total: projects.length,
      running: projects.filter((p) => (p.status || "").toLowerCase() === "running").length,
      stopped: projects.filter((p) => (p.status || "").toLowerCase() === "stopped").length,
      withPort: projects.filter((p) => !!p.port).length,
    };
  }, [projects]);

  const getStatusBadge = (status) => {
    const value = (status || "").toLowerCase();

    if (value === "running") {
      return {
        background: "rgba(34,197,94,0.16)",
        color: "#4ade80",
        label: "Running",
      };
    }

    if (value === "stopped") {
      return {
        background: "rgba(239,68,68,0.16)",
        color: "#f87171",
        label: "Stopped",
      };
    }

    return {
      background: "rgba(250,204,21,0.16)",
      color: "#facc15",
      label: status || "Unknown",
    };
  };

  return (
    <MainLayout>
      <div style={styles.pageHead}>
        <div>
          <h1 style={styles.title}>Projets</h1>
          <p style={styles.subtitle}>
            Gère les projets surveillés, leurs processus, ports et serveurs liés
          </p>
        </div>
      </div>

      <div style={styles.statsGrid}>
        <StatCard title="Total projets" value={stats.total} subtitle="Tous les projets enregistrés" />
        <StatCard title="Running" value={stats.running} subtitle="Projets actifs" />
        <StatCard title="Stopped" value={stats.stopped} subtitle="Projets arrêtés" />
        <StatCard title="Avec port" value={stats.withPort} subtitle="Services exposés" />
      </div>

      <div style={styles.layout}>
        <div style={panelStyle}>
          <div style={styles.sectionHead}>
            <h3 style={styles.panelTitle}>
              {editingProjectId ? "Modifier un projet" : "Créer un projet"}
            </h3>

            {editingProjectId ? (
              <button style={styles.secondaryButton} onClick={resetForm}>
                Annuler
              </button>
            ) : null}
          </div>

          <form onSubmit={handleSubmit}>
            <div style={styles.formGrid}>
              <div>
                <label style={styles.label}>Nom du projet</label>
                <input
                  type="text"
                  value={form.name}
                  onChange={(e) => setForm({ ...form, name: e.target.value })}
                  placeholder="Ex: Frontend Monitoring"
                  style={inputStyle}
                />
              </div>

              <div>
                <label style={styles.label}>Serveur</label>
                <select
                  value={form.server_id}
                  onChange={(e) => setForm({ ...form, server_id: e.target.value })}
                  style={inputStyle}
                >
                  <option value="">Choisir un serveur</option>
                  {servers.map((server) => (
                    <option key={server.id} value={server.id}>
                      {server.name} (ID: {server.id})
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label style={styles.label}>Chemin du projet</label>
                <input
                  type="text"
                  value={form.path}
                  onChange={(e) => setForm({ ...form, path: e.target.value })}
                  placeholder="Ex: /var/www/project"
                  style={inputStyle}
                />
              </div>

              <div>
                <label style={styles.label}>Nom du processus</label>
                <input
                  type="text"
                  value={form.process_name}
                  onChange={(e) => setForm({ ...form, process_name: e.target.value })}
                  placeholder="Ex: node"
                  style={inputStyle}
                />
              </div>

              <div>
                <label style={styles.label}>Port</label>
                <input
                  type="number"
                  value={form.port}
                  onChange={(e) => setForm({ ...form, port: e.target.value })}
                  placeholder="Ex: 3000"
                  style={inputStyle}
                />
              </div>

              <div>
                <label style={styles.label}>Statut</label>
                <select
                  value={form.status}
                  onChange={(e) => setForm({ ...form, status: e.target.value })}
                  style={inputStyle}
                >
                  <option value="running">running</option>
                  <option value="stopped">stopped</option>
                  <option value="unknown">unknown</option>
                </select>
              </div>
            </div>

            {error ? <div style={styles.error}>{error}</div> : null}

            <div style={styles.actions}>
              <button type="submit" style={styles.primaryButton} disabled={saving}>
                {saving
                  ? "Enregistrement..."
                  : editingProjectId
                  ? "Mettre à jour"
                  : "Créer le projet"}
              </button>
            </div>
          </form>
        </div>

        <div style={panelStyle}>
          <div style={styles.sectionHead}>
            <h3 style={styles.panelTitle}>Liste des projets</h3>
            <span style={styles.counter}>{filteredProjects.length} élément(s)</span>
          </div>

          <div style={{ marginBottom: "16px" }}>
            <input
              type="text"
              placeholder="Rechercher un projet..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              style={inputStyle}
            />
          </div>

          {loading ? (
            <div style={styles.info}>Chargement des projets...</div>
          ) : filteredProjects.length === 0 ? (
            <div style={styles.info}>Aucun projet trouvé.</div>
          ) : (
            <div style={styles.projectsList}>
              {filteredProjects.map((project) => {
                const statusBadge = getStatusBadge(project.status);

                return (
                  <div key={project.id} style={styles.projectCard}>
                    <div style={styles.projectTop}>
                      <div style={styles.projectTitleWrap}>
                        <h4 style={styles.projectTitle}>{project.name || "Projet"}</h4>
                        <span
                          style={{
                            ...styles.badge,
                            background: statusBadge.background,
                            color: statusBadge.color,
                          }}
                        >
                          {statusBadge.label}
                        </span>
                      </div>
                    </div>

                    <div style={styles.metaGrid}>
                      <MetaItem label="ID" value={project.id} />
                      <MetaItem label="Serveur ID" value={project.server_id ?? "-"} />
                      <MetaItem label="Port" value={project.port ?? "-"} />
                      <MetaItem label="Process" value={project.process_name || "-"} />
                      <MetaItem label="Chemin" value={project.path || "-"} />
                    </div>

                    <div style={styles.projectActions}>
                      <button style={styles.editButton} onClick={() => handleEdit(project)}>
                        Modifier
                      </button>

                      <button style={styles.deleteButton} onClick={() => handleDelete(project.id)}>
                        Supprimer
                      </button>
                    </div>
                  </div>
                );
              })}
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
  layout: {
    display: "grid",
    gridTemplateColumns: "1.1fr 1fr",
    gap: "20px",
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
  formGrid: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))",
    gap: "16px",
  },
  label: {
    display: "block",
    color: "#cbd5e1",
    marginBottom: "8px",
    fontSize: "14px",
    fontWeight: 500,
  },
  actions: {
    marginTop: "18px",
    display: "flex",
    gap: "12px",
  },
  primaryButton: {
    background: "linear-gradient(135deg, #6366f1, #3b82f6)",
    color: "#fff",
    border: "none",
    padding: "12px 16px",
    borderRadius: "10px",
    cursor: "pointer",
    fontWeight: 700,
  },
  secondaryButton: {
    background: "rgba(148,163,184,0.12)",
    color: "#e5e7eb",
    border: "1px solid rgba(148,163,184,0.16)",
    padding: "10px 14px",
    borderRadius: "10px",
    cursor: "pointer",
  },
  editButton: {
    background: "#2563eb",
    color: "#fff",
    border: "none",
    padding: "10px 14px",
    borderRadius: "10px",
    cursor: "pointer",
    fontWeight: 600,
  },
  deleteButton: {
    background: "#dc2626",
    color: "#fff",
    border: "none",
    padding: "10px 14px",
    borderRadius: "10px",
    cursor: "pointer",
    fontWeight: 600,
  },
  error: {
    marginTop: "14px",
    color: "#f87171",
    fontSize: "14px",
  },
  info: {
    color: "#94a3b8",
    padding: "10px 0",
  },
  projectsList: {
    display: "grid",
    gap: "14px",
  },
  projectCard: {
    background: "rgba(30, 41, 59, 0.82)",
    border: "1px solid rgba(148,163,184,0.12)",
    borderRadius: "18px",
    padding: "18px",
  },
  projectTop: {
    marginBottom: "14px",
  },
  projectTitleWrap: {
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    gap: "16px",
    flexWrap: "wrap",
  },
  projectTitle: {
    margin: 0,
    color: "#f8fafc",
    fontSize: "18px",
  },
  badge: {
    padding: "5px 10px",
    borderRadius: "999px",
    fontSize: "12px",
    fontWeight: 700,
    textTransform: "capitalize",
  },
  metaGrid: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fit, minmax(140px, 1fr))",
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
  projectActions: {
    display: "flex",
    gap: "12px",
    marginTop: "16px",
    flexWrap: "wrap",
  },
};
