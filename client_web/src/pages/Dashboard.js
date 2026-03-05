import React, { useMemo } from "react";
import { useStats } from "../providers/StatsProvider";

import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  ArcElement,
  Tooltip,
  Legend
} from "chart.js";

import { Line, Doughnut } from "react-chartjs-2";

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  ArcElement,
  Tooltip,
  Legend
);

function formatUptime(seconds) {
  const s = Number(seconds || 0);
  const days = Math.floor(s / 86400);
  const hours = Math.floor((s % 86400) / 3600);
  const mins = Math.floor((s % 3600) / 60);
  return `${days}d ${hours}h ${mins}m`;
}

function Card({ title, children }) {
  return (
    <div style={styles.card}>
      <div style={styles.cardTitle}>{title}</div>
      {children}
    </div>
  );
}

export default function Dashboard() {
  const { stats, loading, error } = useStats();

  const cpuLineData = useMemo(() => {
    const history = stats?.cpu?.history_percent || [];
    return {
      labels: history.map((_, i) => `${i + 1}`),
      datasets: [{ label: "CPU %", data: history }]
    };
  }, [stats]);

  const ramLineData = useMemo(() => {
    const history = stats?.ram?.history_percent || [];
    return {
      labels: history.map((_, i) => `${i + 1}`),
      datasets: [{ label: "RAM %", data: history }]
    };
  }, [stats]);

  const diskDoughnutData = useMemo(() => {
    const used = Number(stats?.disk?.used_gb || 0);
    const total = Number(stats?.disk?.total_gb || 0);
    const free = Math.max(0, total - used);
    return {
      labels: ["Used (GB)", "Free (GB)"],
      datasets: [{ data: [used, free] }]
    };
  }, [stats]);

  if (loading) return <div style={styles.page}>Loading dashboard...</div>;
  if (error) return <div style={styles.page}>Error: {error}</div>;
  if (!stats) return <div style={styles.page}>No data.</div>;

  return (
    <div style={styles.page}>
      <div style={styles.header}>
        <div>
          <h2 style={{ margin: 0 }}>Server Dashboard (Mock)</h2>
          <div style={{ opacity: 0.8, marginTop: 6 }}>
            Updated: {stats.timestamp} • Uptime: {formatUptime(stats.uptime_seconds)}
          </div>
        </div>
      </div>

      <div style={styles.grid}>
        <Card title="CPU">
          <div style={styles.big}>{stats.cpu.usage_percent}%</div>
        </Card>

        <Card title="RAM">
          <div style={styles.big}>{stats.ram.usage_percent}%</div>
          <div style={styles.small}>
            {stats.ram.used_mb} MB / {stats.ram.total_mb} MB
          </div>
        </Card>

        <Card title="Disk">
          <div style={styles.big}>{stats.disk.usage_percent}%</div>
          <div style={styles.small}>
            {stats.disk.used_gb} GB / {stats.disk.total_gb} GB
          </div>
        </Card>

        <Card title="CPU over time">
          <div style={{ height: 240 }}>
            <Line data={cpuLineData} options={{ responsive: true, maintainAspectRatio: false }} />
          </div>
        </Card>

        <Card title="RAM over time">
          <div style={{ height: 240 }}>
            <Line data={ramLineData} options={{ responsive: true, maintainAspectRatio: false }} />
          </div>
        </Card>

        <Card title="Disk usage">
          <div style={{ height: 240 }}>
            <Doughnut data={diskDoughnutData} options={{ responsive: true, maintainAspectRatio: false }} />
          </div>
        </Card>
      </div>
    </div>
  );
}

const styles = {
  page: {
    minHeight: "100vh",
    padding: 20,
    background: "#0b1220",
    color: "#e5e7eb",
    fontFamily: "system-ui, -apple-system, Segoe UI, Roboto, sans-serif"
  },
  header: { marginBottom: 16 },
  grid: {
    display: "grid",
    gridTemplateColumns: "repeat(auto-fit, minmax(260px, 1fr))",
    gap: 16
  },
  card: {
    background: "#111a2e",
    border: "1px solid rgba(255,255,255,0.08)",
    borderRadius: 12,
    padding: 16
  },
  cardTitle: { fontWeight: 800, marginBottom: 10 },
  big: { fontSize: 34, fontWeight: 900 },
  small: { marginTop: 6, opacity: 0.85 }
};
