/*
Rôle du fichier :
Ce fichier contient la structure visuelle principale de l’application.

Ce qu’il contient :
- une barre latérale de navigation
- un header utilisateur
- l’affichage conditionnel des liens admin
- le lien vers la page des logs

Pourquoi il existe :
Il assure une interface cohérente sur toutes les pages du dashboard.

Comment il s’intègre dans le projet :
Toutes les pages privées sont affichées dans ce layout.
*/
import React from "react";
import { Link, useLocation } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const baseMenu = [
  { to: "/", label: "Dashboard" },
  { to: "/servers", label: "Serveurs" },
  { to: "/projects", label: "Projets" },
  { to: "/alerts", label: "Alertes" },
  { to: "/alert-rules", label: "Règles alertes" },
  { to: "/logs", label: "Logs" },
];

const adminMenu = [
  { to: "/admin", label: "Administration" },
  { to: "/admin/users", label: "Utilisateurs" },
  { to: "/admin/logs", label: "Logs système" },
];

export default function MainLayout({ children }) {
  const { user, logout } = useAuth();
  const location = useLocation();

  return (
    <div style={styles.app}>
      <aside style={styles.sidebar}>
        <div style={styles.brand}>
          <div style={styles.logo}>M</div>
          <div>
            <div style={styles.brandTitle}>Monitoring SaaS</div>
            <div style={styles.brandSub}>Control Center</div>
          </div>
        </div>

        <nav style={styles.nav}>
          {baseMenu.map((item) => {
            const active = location.pathname === item.to;
            return (
              <Link
                key={item.to}
                to={item.to}
                style={{
                  ...styles.link,
                  ...(active ? styles.activeLink : {}),
                }}
              >
                {item.label}
              </Link>
            );
          })}

          {user?.role === "admin" &&
            adminMenu.map((item) => {
              const active = location.pathname === item.to;
              return (
                <Link
                  key={item.to}
                  to={item.to}
                  style={{
                    ...styles.link,
                    ...(active ? styles.activeLink : {}),
                  }}
                >
                  {item.label}
                </Link>
              );
            })}
        </nav>
      </aside>

      <main style={styles.main}>
        <header style={styles.header}>
          <div>
            <strong style={styles.userName}>{user?.name || "Utilisateur"}</strong>
            <div style={styles.email}>{user?.email}</div>
            <div style={styles.role}>Rôle : {user?.role}</div>
          </div>

          <button onClick={logout} style={styles.button}>
            Déconnexion
          </button>
        </header>

        <section>{children}</section>
      </main>
    </div>
  );
}

const styles = {
  app: {
    display: "flex",
    minHeight: "100vh",
    background: "transparent",
  },
  sidebar: {
    width: "260px",
    background: "rgba(15, 23, 42, 0.92)",
    borderRight: "1px solid rgba(148, 163, 184, 0.12)",
    padding: "24px 18px",
    backdropFilter: "blur(10px)",
  },
  brand: {
    display: "flex",
    alignItems: "center",
    gap: "12px",
    marginBottom: "30px",
  },
  logo: {
    width: "42px",
    height: "42px",
    borderRadius: "12px",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    background: "linear-gradient(135deg, #6366f1, #3b82f6)",
    color: "#fff",
    fontWeight: 700,
  },
  brandTitle: {
    fontWeight: 700,
    fontSize: "16px",
    color: "#f8fafc",
  },
  brandSub: {
    fontSize: "12px",
    color: "#94a3b8",
  },
  nav: {
    display: "flex",
    flexDirection: "column",
    gap: "10px",
  },
  link: {
    padding: "12px 14px",
    borderRadius: "12px",
    color: "#cbd5e1",
    textDecoration: "none",
    background: "transparent",
    transition: "0.2s ease",
  },
  activeLink: {
    background: "linear-gradient(135deg, rgba(99,102,241,0.25), rgba(59,130,246,0.18))",
    color: "#fff",
    border: "1px solid rgba(99,102,241,0.35)",
  },
  main: {
    flex: 1,
    padding: "28px",
  },
  header: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    background: "rgba(15, 23, 42, 0.72)",
    border: "1px solid rgba(148,163,184,0.12)",
    padding: "16px 20px",
    borderRadius: "16px",
    marginBottom: "24px",
    backdropFilter: "blur(8px)",
  },
  userName: {
    color: "#f8fafc",
  },
  email: {
    color: "#94a3b8",
    fontSize: "14px",
  },
  role: {
    color: "#818cf8",
    fontSize: "13px",
    marginTop: "4px",
  },
  button: {
    background: "#ef4444",
    color: "#fff",
    border: "none",
    padding: "10px 14px",
    borderRadius: "10px",
    cursor: "pointer",
  },
};
