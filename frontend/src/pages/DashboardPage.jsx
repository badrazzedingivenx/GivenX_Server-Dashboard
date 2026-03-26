/*
Rôle du fichier :
Cette page affiche le tableau de bord principal du projet.

Ce qu’il contient :
- les cartes KPI
- le graphique historique serveur
- les alertes récentes
- un rechargement automatique périodique

Pourquoi il existe :
C’est la page principale de supervision présentée à l’utilisateur.

Comment il s’intègre dans le projet :
Elle consomme les endpoints backend de métriques et d’alertes.
*/
import React, { useEffect, useState } from "react";
import client from "../api/client";
import StatCard from "../components/StatCard";
import AlertList from "../components/AlertList";
import ServerChart from "../components/ServerChart";
import MainLayout from "../layout/MainLayout";

const panelStyle = {
  background: "rgba(15, 23, 42, 0.88)",
  border: "1px solid rgba(148, 163, 184, 0.12)",
  borderRadius: "20px",
  padding: "20px",
  boxShadow: "0 10px 30px rgba(0,0,0,0.20)",
};

export default function DashboardPage() {
  const [servers, setServers] = useState([]);
  const [latest, setLatest] = useState(null);
  const [history, setHistory] = useState([]);
  const [alerts, setAlerts] = useState([]);
  const [loading, setLoading] = useState(true);

  const serverId = servers[0]?.id;

  const loadData = async () => {
    try {
      const serversRes = await client.get("/api/servers");
      const serverList = serversRes.data?.data?.servers || [];
      setServers(serverList);

      if (!serverList.length) {
        setLatest(null);
        setHistory([]);
        setAlerts([]);
        setLoading(false);
        return;
      }

      const selectedServerId = serverList[0].id;

      const [latestRes, historyRes, alertsRes] = await Promise.all([
        client.get(`/api/servers/${selectedServerId}/metrics/latest`),
        client.get(`/api/servers/${selectedServerId}/metrics/history`),
        client.get("/api/alerts"),
      ]);

      setLatest(latestRes.data?.data?.metric || null);
      setHistory(historyRes.data?.data?.metrics || []);
      setAlerts(alertsRes.data?.data?.alerts || []);
    } catch (error) {
      console.error("Erreur dashboard :", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();

    const interval = setInterval(() => {
      loadData();
    }, 5000);

    return () => clearInterval(interval);
  }, []);

  return (
    <MainLayout>
      <div style={styles.pageHead}>
        <div>
          <h1 style={styles.title}>Dashboard</h1>
          <p style={styles.subtitle}>
            Vue globale de votre infrastructure et de vos alertes
          </p>
        </div>
      </div>

      <div style={styles.grid}>
        <StatCard
          title="CPU"
          value={latest ? `${latest.cpu_usage}%` : "..."}
          subtitle="Utilisation processeur"
        />
        <StatCard
          title="RAM"
          value={latest ? `${latest.ram_usage}%` : "..."}
          subtitle="Utilisation mémoire"
        />
        <StatCard
          title="Disque"
          value={latest ? `${latest.disk_usage}%` : "..."}
          subtitle="Occupation disque"
        />
        <StatCard
          title="Processus"
          value={latest ? latest.process_count : "..."}
          subtitle="Processus actifs"
        />
      </div>

      <div style={styles.layout}>
        <div style={{ ...panelStyle, minHeight: 420 }}>
          <ServerChart data={history} />
        </div>

        <div style={{ ...panelStyle, minHeight: 420 }}>
          <h3 style={styles.panelTitle}>Alertes récentes</h3>
          <AlertList alerts={alerts} />
        </div>
      </div>

      <div style={panelStyle}>
        <h3 style={styles.panelTitle}>Serveur sélectionné</h3>

        {loading ? (
          <div style={styles.info}>Chargement...</div>
        ) : !serverId ? (
          <div style={styles.info}>Aucun serveur enregistré.</div>
        ) : (
          <div style={styles.serverBox}>
            <div><strong>ID :</strong> {servers[0].id}</div>
            <div><strong>Nom :</strong> {servers[0].name}</div>
            <div><strong>IP :</strong> {servers[0].ip_address}</div>
            <div><strong>OS :</strong> {servers[0].os_type}</div>
          </div>
        )}
      </div>
    </MainLayout>
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
  grid: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fit, minmax(220px, 1fr))",
    gap: "18px",
    marginBottom: "20px",
  },
  layout: {
    display: "grid",
    gridTemplateColumns: "2fr 1fr",
    gap: "20px",
    marginBottom: "20px",
  },
  panelTitle: {
    marginTop: 0,
    color: "#f8fafc",
  },
  info: {
    color: "#94a3b8",
  },
  serverBox: {
    display: "grid",
    gap: "10px",
    color: "#e5e7eb",
  },
};
