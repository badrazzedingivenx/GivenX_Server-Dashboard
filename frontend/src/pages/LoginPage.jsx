/*
Rôle du fichier :
Cette page permet à l’utilisateur de se connecter.

Ce qu’il contient :
- un formulaire email / mot de passe
- l’appel API de connexion

Pourquoi il existe :
Le dashboard doit être protégé par authentification.

Comment il s’intègre dans le projet :
C’est la page d’entrée publique de l’application React.
*/
import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();

  const [form, setForm] = useState({
    email: "",
    password: "",
  });

  const [error, setError] = useState("");
  const [submitting, setSubmitting] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setSubmitting(true);

    try {
      await login(form.email, form.password);
      navigate("/");
    } catch (err) {
      setError("Connexion impossible. Vérifie tes identifiants.");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div style={styles.page}>
      <form onSubmit={handleSubmit} style={styles.card}>
        <div style={styles.top}>
          <div style={styles.logo}>M</div>
          <div>
            <h2 style={styles.title}>Connexion</h2>
            <p style={styles.subtitle}>Accède à ton dashboard monitoring</p>
          </div>
        </div>

        <input
          style={styles.input}
          type="email"
          placeholder="Email"
          value={form.email}
          onChange={(e) => setForm({ ...form, email: e.target.value })}
          required
        />

        <input
          style={styles.input}
          type="password"
          placeholder="Mot de passe"
          value={form.password}
          onChange={(e) => setForm({ ...form, password: e.target.value })}
          required
        />

        {error && <div style={styles.error}>{error}</div>}

        <button style={styles.button} type="submit" disabled={submitting}>
          {submitting ? "Connexion..." : "Se connecter"}
        </button>
      </form>
    </div>
  );
}

const styles = {
  page: {
    minHeight: "100vh",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    background:
      "radial-gradient(circle at top left, rgba(99,102,241,0.22), transparent 28%), radial-gradient(circle at top right, rgba(59,130,246,0.16), transparent 24%), #0b1020",
    padding: "24px",
  },
  card: {
    width: "100%",
    maxWidth: "420px",
    background: "rgba(15,23,42,0.95)",
    border: "1px solid rgba(148,163,184,0.14)",
    borderRadius: "24px",
    padding: "28px",
    display: "grid",
    gap: "14px",
    boxShadow: "0 22px 60px rgba(0,0,0,0.35)",
  },
  top: {
    display: "flex",
    alignItems: "center",
    gap: "14px",
    marginBottom: "6px",
  },
  logo: {
    width: "46px",
    height: "46px",
    borderRadius: "14px",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    background: "linear-gradient(135deg, #6366f1, #3b82f6)",
    color: "#fff",
    fontWeight: 700,
    fontSize: "18px",
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
  input: {
    width: "100%",
    padding: "14px 16px",
    borderRadius: "12px",
    border: "1px solid rgba(148,163,184,0.16)",
    background: "#0f172a",
    color: "#fff",
    outline: "none",
  },
  button: {
    width: "100%",
    padding: "14px",
    border: "none",
    borderRadius: "12px",
    background: "linear-gradient(135deg, #6366f1, #3b82f6)",
    color: "#fff",
    fontWeight: 700,
    cursor: "pointer",
  },
  error: {
    color: "#f87171",
    fontSize: "14px",
  },
};
