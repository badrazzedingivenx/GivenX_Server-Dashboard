import React, { useEffect, useState } from "react";
import client from "../api/client";
import MainLayout from "../layout/MainLayout";

const panelStyle = {
  background: "rgba(15, 23, 42, 0.88)",
  border: "1px solid rgba(148, 163, 184, 0.12)",
  borderRadius: "20px",
  padding: "20px",
  boxShadow: "0 10px 30px rgba(0,0,0,0.20)",
};

export default function AdminUsersPage() {
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);

  const loadUsers = async () => {
    try {
      setLoading(true);
      const res = await client.get("/api/admin/users");
      setUsers(res.data?.data?.users || []);
    } catch (err) {
      console.error("Erreur chargement users:", err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadUsers();
  }, []);

  const handleBlock = async (id) => {
    try {
      await client.put(`/api/admin/users/${id}/block`);
      loadUsers();
    } catch (err) {
      console.error("Erreur block:", err);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm("Supprimer cet utilisateur ?")) return;

    try {
      await client.delete(`/api/admin/users/${id}`);
      loadUsers();
    } catch (err) {
      console.error("Erreur delete:", err);
    }
  };

  return (
    <MainLayout>
      <h1 style={styles.title}>Gestion des utilisateurs</h1>

      <div style={panelStyle}>
        {loading ? (
          <p style={styles.info}>Chargement...</p>
        ) : (
          <table>
            <thead>
              <tr>
                <th>ID</th>
                <th>Nom</th>
                <th>Email</th>
                <th>Role</th>
                <th>Status</th>
                <th>Actions</th>
              </tr>
            </thead>

            <tbody>
              {users.map((user) => (
                <tr key={user.id}>
                  <td>{user.id}</td>
                  <td>{user.name}</td>
                  <td>{user.email}</td>
                  <td>{user.role}</td>
                  <td>
                    <span style={getStatusBadge(user.status)}>
                      {user.status}
                    </span>
                  </td>

                  <td>
                    <button
                      style={styles.blockBtn}
                      onClick={() => handleBlock(user.id)}
                    >
                      Bloquer
                    </button>

                    <button
                      style={styles.deleteBtn}
                      onClick={() => handleDelete(user.id)}
                    >
                      Supprimer
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </MainLayout>
  );
}

function getStatusBadge(status) {
  const value = (status || "").toLowerCase();

  if (value === "active") {
    return {
      ...styles.badge,
      background: "rgba(34,197,94,0.16)",
      color: "#4ade80",
    };
  }

  if (value === "blocked") {
    return {
      ...styles.badge,
      background: "rgba(239,68,68,0.16)",
      color: "#f87171",
    };
  }

  return {
    ...styles.badge,
    background: "rgba(250,204,21,0.16)",
    color: "#facc15",
  };
}

const styles = {
  title: {
    color: "#f8fafc",
    marginBottom: "20px",
  },
  info: {
    color: "#94a3b8",
  },
  badge: {
    padding: "5px 10px",
    borderRadius: "999px",
    fontSize: "12px",
    fontWeight: 700,
  },
  blockBtn: {
    marginRight: "8px",
    padding: "6px 10px",
    borderRadius: "8px",
    border: "none",
    background: "#f59e0b",
    color: "#fff",
    cursor: "pointer",
  },
  deleteBtn: {
    padding: "6px 10px",
    borderRadius: "8px",
    border: "none",
    background: "#dc2626",
    color: "#fff",
    cursor: "pointer",
  },
};
