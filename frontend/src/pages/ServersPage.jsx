/*
Rôle du fichier :
Cette page affiche la liste des serveurs de l’utilisateur connecté.

Ce qu’il contient :
- le chargement des serveurs depuis l’API
- un affichage en cartes
- un état de chargement
- un message si aucun serveur n’existe

Pourquoi il existe :
Le projet doit fournir une page claire de gestion et de visualisation des serveurs surveillés.

Comment il s’intègre dans le projet :
Elle est accessible depuis le menu latéral du dashboard.
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

const inputStyle = {
  width: "100%",
  padding: "12px 14px",
  borderRadius: "10px",
  border: "1px solid rgba(148,163,184,0.2)",
  background: "#0f172a",
  color: "#fff",
  outline: "none",
};

export default function ServersPage() {
  const [servers, setServers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [editingServerId, setEditingServerId] = useState(null);
  const [error, setError] = useState("");
  const [search, setSearch] = useState("");

  const [form, setForm] = useState({
    name: "",
    host: "",
    ip_address: "",
    description: "",
    os_type: "",
    is_active: true,
  });

  const loadServers = async () => {
    try {
      setLoading(true);
      setError("");

      const res = await client.get("/api/servers");
      setServers(res.data?.data?.servers || []);
    } catch (err) {
      console.error("Erreur chargement serveurs :", err);
      setError("Impossible de charger les serveurs.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadServers();
  }, []);

  const resetForm = () => {
    setForm({
      name: "",
      host: "",
      ip_address: "",
      description: "",
      os_type: "",
      is_active: true,
    });
    setEditingServerId(null);
    setError("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!form.name.trim()) {
      setError("Le nom du serveur est obligatoire.");
      return;
    }

    if (!form.host.trim() && !form.ip_address.trim()) {
      setError("Le host ou l’adresse IP est obligatoire.");
      return;
    }

    try {
      setSaving(true);
      setError("");

      const payload = {
        name: form.name.trim(),
        host: form.host.trim(),
        ip_address: form.ip_address.trim(),
        description: form.description.trim(),
        os_type: form.os_type.trim(),
        is_active: form.is_active ? 1 : 0,
      };

      if (editingServerId) {
        await client.put(`/api/servers/${editingServerId}`, payload);
      } else {
        await client.post("/api/servers", payload);
      }

      resetForm();
      await loadServers();
    } catch (err) {
      console.error("Erreur sauvegarde serveur :", err);
      setError("Impossible d’enregistrer le serveur.");
    } finally {
      setSaving(false);
    }
  };

  const handleEdit = (server) => {
    setEditingServerId(server.id);
    setError("");

    setForm({
      name: server.name || "",
      host: server.host || "",
      ip_address: server.ip_address || "",
      description: server.description || "",
      os_type: server.os_type || "",
      is_active: Number(server.is_active) === 1 || server.is_active === true,
    });

    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const handleDelete = async (serverId) => {
    const confirmed = window.confirm("Supprimer ce serveur ?");
    if (!confirmed) return;

    try {
      await client.delete(`/api/servers/${serverId}`);

      if (editingServerId === serverId) {
        resetForm();
      }

      await loadServers();
    } catch (err) {
      console.error("Erreur suppression serveur :", err);
      setError("Impossible de supprimer le serveur.");
    }
  };

  const filteredServers = useMemo(() => {
    return servers.filter((server) => {
      const target = [
        server.name,
        server.host,
        server.ip_address,
        server.description,
        server.os_type,
        Number(server.is_active) === 1 ? "active" : "inactive",
      ]
        .filter(Boolean)
        .join(" ")
        .toLowerCase();

      return target.includes(search.toLowerCase());
    });
  }, [servers, search]);

  const stats = useMemo(() => {
    return {
      total: servers.length,
      active: servers.filter((s) => Number(s.is_active) === 1).length,
      inactive: servers.filter((s) => Number(s.is_active) !== 1).length,
      withIp: servers.filter((s) => !!s.ip_address).length,
    };
  }, [servers]);

  const getStatusBadge = (isActive) => {
    if (Number(isActive) === 1 || isActive === true) {
      return {
        background: "rgba(34,197,94,0.16)",
        color: "#4ade80",
        label: "Active",
      };
    }

    return {
      background: "rgba(239,68,68,0.16)",
      color: "#f87171",
      label: "Inactive",
    };
  };

  return (
    <MainLayout>
      <div style={styles.pageHead}>
        <div>
          <h1 style={styles.title}>Serveurs</h1>
          <p style={styles.subtitle}>
            Gère les serveurs surveillés et leurs informations de connexion
          </p>
        </div>
      </div>

      <div style={styles.statsGrid}>
        <StatCard title="Total serveurs" value={stats.total} subtitle="Tous les serveurs enregistrés" />
        <StatCard title="Actifs" value={stats.active} subtitle="Serveurs actuellement actifs" />
        <StatCard title="Inactifs" value={stats.inactive} subtitle="Serveurs désactivés" />
        <StatCard title="Avec IP" value={stats.withIp} subtitle="Serveurs avec adresse IP" />
      </div>

      <div style={styles.layout}>
        <div style={panelStyle}>
          <div style={styles.sectionHead}>
            <h3 style={styles.panelTitle}>
              {editingServerId ? "Modifier un serveur" : "Créer un serveur"}
            </h3>

            {editingServerId ? (
              <button style={styles.secondaryButton} onClick={resetForm}>
                Annuler
              </button>
            ) : null}
          </div>

          <form onSubmit={handleSubmit}>
            <div style={styles.formGrid}>
              <div>
                <label style={styles.label}>Nom du serveur</label>
                <input
                  type="text"
                  value={form.name}
                  onChange={(e) => setForm({ ...form, name: e.target.value })}
                  placeholder="Ex: Serveur Ubuntu Principal"
                  style={inputStyle}
                />
              </div>

              <div>
                <label style={styles.label}>Host</label>
                <input
                  type="text"
                  value={form.host}
                  onChange={(e) => setForm({ ...form, host: e.target.value })}
                  placeholder="Ex: localhost"
                  style={inputStyle}
                />
              </div>

              <div>
                <label style={styles.label}>Adresse IP</label>
                <input
                  type="text"
                  value={form.ip_address}
                  onChange={(e) => setForm({ ...form, ip_address: e.target.value })}
                  placeholder="Ex: 192.168.1.10"
                  style={inputStyle}
                />
              </div>

              <div>
                <label style={styles.label}>Système d’exploitation</label>
                <input
                  type="text"
                  value={form.os_type}
                  onChange={(e) => setForm({ ...form, os_type: e.target.value })}
                  placeholder="Ex: Ubuntu 22.04"
                  style={inputStyle}
                />
              </div>

              <div style={{ gridColumn: "1 / -1" }}>
                <label style={styles.label}>Description</label>
                <textarea
                  value={form.description}
                  onChange={(e) => setForm({ ...form, description: e.target.value })}
                  placeholder="Description du serveur"
                  style={{ ...inputStyle, minHeight: "100px", resize: "vertical" }}
                />
              </div>
            </div>

            <div style={styles.checkboxRow}>
              <label style={styles.checkboxLabel}>
                <input
                  type="checkbox"
                  checked={form.is_active}
                  onChange={(e) => setForm({ ...form, is_active: e.target.checked })}
                />
                <span>Serveur actif</span>
              </label>
            </div>

            {error ? <div style={styles.error}>{error}</div> : null}

            <div style={styles.actions}>
              <button type="submit" style={styles.primaryButton} disabled={saving}>
                {saving
                  ? "Enregistrement..."
                  : editingServerId
                  ? "Mettre à jour"
                  : "Créer le serveur"}
              </button>
            </div>
          </form>
        </div>

        <div style={panelStyle}>
          <div style={styles.sectionHead}>
            <h3 style={styles.panelTitle}>Liste des serveurs</h3>
            <span style={styles.counter}>{filteredServers.length} élément(s)</span>
          </div>

          <div style={{ marginBottom: "16px" }}>
            <input
              type="text"
              placeholder="Rechercher un serveur..."
              value={search}
              onChange={(e) => setSearch(e.target.value)}
              style={inputStyle}
            />
          </div>

          {loading ? (
            <div style={styles.info}>Chargement des serveurs...</div>
          ) : filteredServers.length === 0 ? (
            <div style={styles.info}>Aucun serveur trouvé.</div>
          ) : (
            <div style={styles.serversList}>
              {filteredServers.map((server) => {
                const statusBadge = getStatusBadge(server.is_active);

                return (
                  <div key={server.id} style={styles.serverCard}>
                    <div style={styles.serverTop}>
                      <div style={styles.serverTitleWrap}>
                        <h4 style={styles.serverTitle}>{server.name || "Serveur"}</h4>
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
                      <MetaItem label="ID" value={server.id} />
                      <MetaItem label="Host" value={server.host || "-"} />
                      <MetaItem label="IP" value={server.ip_address || "-"} />
                      <MetaItem label="OS" value={server.os_type || "-"} />
                      <MetaItem label="Description" value={server.description || "-"} />
                    </div>

                    <div style={styles.serverActions}>
                      <button style={styles.editButton} onClick={() => handleEdit(server)}>
                        Modifier
                      </button>

                      <button style={styles.deleteButton} onClick={() => handleDelete(server.id)}>
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
  checkboxRow: {
    marginTop: "16px",
  },
  checkboxLabel: {
    display: "flex",
    alignItems: "center",
    gap: "10px",
    color: "#cbd5e1",
    fontSize: "14px",
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
  serversList: {
    display: "grid",
    gap: "14px",
  },
  serverCard: {
    background: "rgba(30, 41, 59, 0.82)",
    border: "1px solid rgba(148,163,184,0.12)",
    borderRadius: "18px",
    padding: "18px",
  },
  serverTop: {
    marginBottom: "14px",
  },
  serverTitleWrap: {
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    gap: "16px",
    flexWrap: "wrap",
  },
  serverTitle: {
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
  serverActions: {
    display: "flex",
    gap: "12px",
    marginTop: "16px",
    flexWrap: "wrap",
  },
};
