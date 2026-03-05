import React, { createContext, useContext, useEffect, useMemo, useState } from "react";

const StatsContext = createContext(null);

export function StatsProvider({ children, refreshMs = 5000 }) {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  async function fetchStats() {
    try {
      setError("");
      const res = await fetch("/data/stats.json", { cache: "no-store" });
      if (!res.ok) throw new Error(`HTTP ${res.status}`);
      const data = await res.json();
      setStats(data);
      setLoading(false);
    } catch (e) {
      setError(e?.message || "Failed to load stats");
      setLoading(false);
    }
  }

  useEffect(() => {
    fetchStats();
    const id = setInterval(fetchStats, refreshMs);
    return () => clearInterval(id);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [refreshMs]);

  const value = useMemo(
    () => ({ stats, loading, error, refresh: fetchStats }),
    [stats, loading, error]
  );

  return <StatsContext.Provider value={value}>{children}</StatsContext.Provider>;
}

export function useStats() {
  const ctx = useContext(StatsContext);
  if (!ctx) throw new Error("useStats must be used within a StatsProvider");
  return ctx;
}
