import React, { useEffect, useState } from "react";
import client from "../api/client";
import MainLayout from "../layout/MainLayout";

export default function AdminLogsPage() {
  const [logs, setLogs] = useState([]);
  const [loading, setLoading] = useState(true);

  const loadLogs = async () => {
    try {
      const res = await client.get("/api/admin/logs/system");
      setLogs(res.data?.data?.logs || []);
    } catch (error) {
      console.error("Erreur chargement logs admin :", error);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadLogs();
  }, []);

  return (
    <MainLayout>
      <h1 style={styles.title}>Logs système</h1>

      <div style={styles.card}>
        {loading ? (
          <div>Chargement...</div>
        ) : logs.length === 0 ? (
          <div>Aucun log système.</div>
        ) : (
          <table>
            <thead>
              <tr>
                <th>Niveau</th>
                <th>Message</th>
                <th>Date</th>
              </tr>
            </thead>
            <tbody>
              {logs.map((log) => (
                <tr key={log.id}>
                  <td>{log.level}</td>
                  <td>{log.message}</td>
                  <td>{log.created_at}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </MainLayout>
  );
}

const styles = {
  title: {
    marginBottom: "20px",
    color: "#f8fafc",
  },
  card: {
    background: "rgba(15, 23, 42, 0.88)",
    border: "1px solid rgba(148, 163, 184, 0.12)",
    borderRadius: "20px",
    padding: "20px",
  },
};
