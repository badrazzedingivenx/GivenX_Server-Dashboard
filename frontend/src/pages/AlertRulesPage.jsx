/*
Rôle du fichier :
Cette page permet de gérer les règles d’alertes de l’utilisateur.

Ce qu’il contient :
- affichage de la liste des règles
- formulaire de création
- formulaire de modification
- suppression d’une règle

Pourquoi il existe :
Le projet doit permettre de personnaliser les seuils et comportements d’alertes.

Comment il s’intègre dans le projet :
Cette page est accessible depuis l’interface React et communique avec l’API REST.
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

const ruleTypeLabels = {
  cpu_high: "CPU élevé",
  ram_high: "RAM élevée",
  disk_full: "Disque plein",
  server_down: "Serveur indisponible",
  process_stopped: "Process arrêté",
};

export default function AlertRulesPage() {
  const [rules, setRules] = useState([]);
  const [servers, setServers] = useState([]);
  const [projects, setProjects] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [editingRuleId, setEditingRuleId] = useState(null);
  const [error, setError] = useState("");

  const [form, setForm] = useState({
    type: "cpu_high",
    server_id: "",
    project_id: "",
    threshold: "",
    severity: "warning",
    cooldown_minutes: "10",
    is_active: true,
  });

  const loadData = async () => {
    try {
      setLoading(true);
      setError("");

      const [rulesRes, serversRes, projectsRes] = await Promise.all([
        client.get("/api/alert-rules"),
        client.get("/api/servers"),
        client.get("/api/projects"),
      ]);

      setRules(rulesRes.data?.data?.rules || []);
      setServers(serversRes.data?.data?.servers || []);
      setProjects(projectsRes.data?.data?.projects || []);
    } catch (err) {
      console.error("Erreur chargement règles alertes :", err);
      setError("Impossible de charger les règles d’alerte.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, []);

  const resetForm = () => {
    setForm({
      type: "cpu_high",
      server_id: "",
      project_id: "",
      threshold: "",
      severity: "warning",
      cooldown_minutes: "10",
      is_active: true,
    });
    setEditingRuleId(null);
    setError("");
  };

  const selectedRuleTypeNeedsThreshold = useMemo(() => {
    return ["cpu_high", "ram_high", "disk_full"].includes(form.type);
  }, [form.type]);

  const validateForm = () => {
    if (!form.server_id && !form.project_id) {
      return "Choisis au moins un serveur ou un projet.";
    }

    if (selectedRuleTypeNeedsThreshold && String(form.threshold).trim() === "") {
      return "Le seuil est obligatoire pour ce type de règle.";
    }

    if (form.cooldown_minutes === "" || Number(form.cooldown_minutes) < 0) {
      return "Le cooldown doit être supérieur ou égal à 0.";
    }

    return "";
  };

  const buildPayload = () => {
    return {
      type: form.type,
      server_id: form.server_id ? Number(form.server_id) : null,
      project_id: form.project_id ? Number(form.project_id) : null,
      threshold: selectedRuleTypeNeedsThreshold && form.threshold !== "" ? Number(form.threshold) : null,
      severity: form.severity,
      cooldown_minutes: Number(form.cooldown_minutes || 0),
      is_active: form.is_active ? 1 : 0,
    };
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const validationError = validateForm();
    if (validationError) {
      setError(validationError);
      return;
    }

    try {
      setSaving(true);
      setError("");

      const payload = buildPayload();

      if (editingRuleId) {
        await client.put(`/api/alert-rules/${editingRuleId}`, payload);
      } else {
        await client.post("/api/alert-rules", payload);
      }

      resetForm();
      await loadData();
    } catch (err) {
      console.error("Erreur sauvegarde règle :", err);
      setError("Impossible d’enregistrer la règle.");
    } finally {
      setSaving(false);
    }
  };

  const handleEdit = (rule) => {
    setEditingRuleId(rule.id);
    setError("");

    setForm({
      type: rule.type || "cpu_high",
      server_id: rule.server_id ? String(rule.server_id) : "",
      project_id: rule.project_id ? String(rule.project_id) : "",
      threshold: rule.threshold ?? "",
      severity: rule.severity || "warning",
      cooldown_minutes: String(rule.cooldown_minutes ?? 10),
      is_active: Boolean(Number(rule.is_active ?? 1)),
    });

    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const handleDelete = async (ruleId) => {
    const confirmed = window.confirm("Supprimer cette règle d’alerte ?");
    if (!confirmed) return;

    try {
      await client.delete(`/api/alert-rules/${ruleId}`);
      if (editingRuleId === ruleId) {
        resetForm();
      }
      await loadData();
    } catch (err) {
      console.error("Erreur suppression règle :", err);
      setError("Impossible de supprimer la règle.");
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

  const getStatusStyle = (isActive) => {
    if (Number(isActive) === 1 || isActive === true) {
      return {
        background: "rgba(34, 197, 94, 0.16)",
        color: "#4ade80",
        label: "Active",
      };
    }

    return {
      background: "rgba(148, 163, 184, 0.16)",
      color: "#cbd5e1",
      label: "Inactive",
    };
  };

  return (
    <MainLayout>
      <div style={styles.pageHead}>
        <div>
          <h1 style={styles.title}>Règles d’alertes</h1>
          <p style={styles.subtitle}>
            Configure les seuils et événements déclencheurs pour tes serveurs et projets
          </p>
        </div>
      </div>

      <div style={styles.statsGrid}>
        <StatCard
          title="Total règles"
          value={rules.length}
          subtitle="Toutes les règles enregistrées"
        />
        <StatCard
          title="Actives"
          value={rules.filter((r) => Number(r.is_active) === 1).length}
          subtitle="Règles actuellement appliquées"
        />
        <StatCard
          title="Critiques"
          value={rules.filter((r) => (r.severity || "").toLowerCase() === "critical").length}
          subtitle="Priorité haute"
        />
        <StatCard
          title="Serveurs"
          value={servers.length}
          subtitle="Disponibles pour association"
        />
      </div>

      <div style={styles.layout}>
        <div style={panelStyle}>
          <div style={styles.sectionHead}>
            <h3 style={styles.panelTitle}>
              {editingRuleId ? "Modifier une règle" : "Créer une règle"}
            </h3>
            {editingRuleId ? (
              <button style={styles.secondaryButton} onClick={resetForm}>
                Annuler
              </button>
            ) : null}
          </div>

          <form onSubmit={handleSubmit}>
            <div style={styles.formGrid}>
              <div>
                <label style={styles.label}>Type de règle</label>
                <select
                  value={form.type}
                  onChange={(e) => setForm({ ...form, type: e.target.value })}
                  style={inputStyle}
                >
                  <option value="cpu_high">CPU élevé</option>
                  <option value="ram_high">RAM élevée</option>
                  <option value="disk_full">Disque plein</option>
                  <option value="server_down">Serveur indisponible</option>
                  <option value="process_stopped">Process arrêté</option>
                </select>
              </div>

              <div>
                <label style={styles.label}>Sévérité</label>
                <select
                  value={form.severity}
                  onChange={(e) => setForm({ ...form, severity: e.target.value })}
                  style={inputStyle}
                >
                  <option value="warning">warning</option>
                  <option value="critical">critical</option>
                  <option value="info">info</option>
                </select>
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
                <label style={styles.label}>Projet</label>
                <select
                  value={form.project_id}
                  onChange={(e) => setForm({ ...form, project_id: e.target.value })}
                  style={inputStyle}
                >
                  <option value="">Choisir un projet</option>
                  {projects.map((project) => (
                    <option key={project.id} value={project.id}>
                      {project.name} (ID: {project.id})
                    </option>
                  ))}
                </select>
              </div>

              <div>
                <label style={styles.label}>
                  Seuil {selectedRuleTypeNeedsThreshold ? "*" : "(optionnel)"}
                </label>
                <input
                  type="number"
                  placeholder={selectedRuleTypeNeedsThreshold ? "Ex: 85" : "Non requis"}
                  value={form.threshold}
                  onChange={(e) => setForm({ ...form, threshold: e.target.value })}
                  style={inputStyle}
                  disabled={!selectedRuleTypeNeedsThreshold}
                />
              </div>

              <div>
                <label style={styles.label}>Cooldown (minutes)</label>
                <input
                  type="number"
                  min="0"
                  placeholder="Ex: 10"
                  value={form.cooldown_minutes}
                  onChange={(e) => setForm({ ...form, cooldown_minutes: e.target.value })}
                  style={inputStyle}
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
                <span>Activer immédiatement cette règle</span>
              </label>
            </div>

            {error ? <div style={styles.error}>{error}</div> : null}

            <div style={styles.actions}>
              <button type="submit" style={styles.primaryButton} disabled={saving}>
                {saving
                  ? "Enregistrement..."
                  : editingRuleId
                  ? "Mettre à jour"
                  : "Créer la règle"}
              </button>
            </div>
          </form>
        </div>

        <div style={panelStyle}>
          <div style={styles.sectionHead}>
            <h3 style={styles.panelTitle}>Liste des règles</h3>
            <span style={styles.counter}>{rules.length} élément(s)</span>
          </div>

          {loading ? (
            <div style={styles.info}>Chargement des règles...</div>
          ) : rules.length === 0 ? (
            <div style={styles.info}>Aucune règle enregistrée.</div>
          ) : (
            <div style={styles.rulesList}>
              {rules.map((rule) => {
                const severityStyle = getSeverityStyle(rule.severity);
                const statusStyle = getStatusStyle(rule.is_active);

                return (
                  <div key={rule.id} style={styles.ruleCard}>
                    <div style={styles.ruleTop}>
                      <div style={styles.ruleTitleWrap}>
                        <h4 style={styles.ruleTitle}>
                          {ruleTypeLabels[rule.type] || rule.type || "Règle"}
                        </h4>

                        <div style={styles.badgesRow}>
                          <span
                            style={{
                              ...styles.badge,
                              background: severityStyle.background,
                              color: severityStyle.color,
                            }}
                          >
                            {rule.severity || "info"}
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
                      </div>
                    </div>

                    <div style={styles.metaGrid}>
                      <MetaItem label="ID" value={rule.id} />
                      <MetaItem label="Server ID" value={rule.server_id ?? "-"} />
                      <MetaItem label="Project ID" value={rule.project_id ?? "-"} />
                      <MetaItem label="Seuil" value={rule.threshold ?? "-"} />
                      <MetaItem
                        label="Cooldown"
                        value={`${rule.cooldown_minutes ?? 0} min`}
                      />
                    </div>

                    <div style={styles.ruleActions}>
                      <button
                        style={styles.editButton}
                        onClick={() => handleEdit(rule)}
                      >
                        Modifier
                      </button>

                      <button
                        style={styles.deleteButton}
                        onClick={() => handleDelete(rule.id)}
                      >
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
  rulesList: {
    display: "grid",
    gap: "14px",
  },
  ruleCard: {
    background: "rgba(30, 41, 59, 0.82)",
    border: "1px solid rgba(148,163,184,0.12)",
    borderRadius: "18px",
    padding: "18px",
  },
  ruleTop: {
    marginBottom: "14px",
  },
  ruleTitleWrap: {
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    gap: "16px",
    flexWrap: "wrap",
  },
  ruleTitle: {
    margin: 0,
    color: "#f8fafc",
    fontSize: "18px",
  },
  badgesRow: {
    display: "flex",
    gap: "8px",
    flexWrap: "wrap",
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
  ruleActions: {
    display: "flex",
    gap: "12px",
    marginTop: "16px",
    flexWrap: "wrap",
  },
};
