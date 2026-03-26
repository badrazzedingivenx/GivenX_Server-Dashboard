/*
Rôle du fichier :
Ce fichier contient le routage principal du frontend React.

Ce qu’il contient :
- la gestion des routes publiques et privées
- la protection des pages nécessitant une authentification
- la protection des pages réservées aux administrateurs

Pourquoi il existe :
Il relie toutes les pages de l’application entre elles et contrôle l’accès
selon l’état de connexion et le rôle utilisateur.

Comment il s’intègre dans le projet :
C’est le point central de navigation du frontend React.
*/

import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import { AuthProvider, useAuth } from "./context/AuthContext";

import LoginPage from "./pages/LoginPage";
import DashboardPage from "./pages/DashboardPage";
import ServersPage from "./pages/ServersPage";
import ProjectsPage from "./pages/ProjectsPage";
import AlertsPage from "./pages/AlertsPage";
import AdminDashboardPage from "./pages/AdminDashboardPage";
import AdminUsersPage from "./pages/AdminUsersPage";
import LogsPage from "./pages/LogsPage";
import AlertRulesPage from "./pages/AlertRulesPage";
import "./index.css";
import AdminLogsPage from "./pages/AdminLogsPage";
function PrivateRoute({ children }) {
  const { user, loading } = useAuth();

  if (loading) {
    return <div style={{ padding: 20 }}>Chargement...</div>;
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  return children;
}

function AdminRoute({ children }) {
  const { user, loading } = useAuth();

  if (loading) {
    return <div style={{ padding: 20 }}>Chargement...</div>;
  }

  if (!user) {
    return <Navigate to="/login" replace />;
  }

  if (user.role !== "admin") {
    return <Navigate to="/" replace />;
  }

  return children;
}

export default function App() {
  return (
    <AuthProvider>
      <BrowserRouter>
        <Routes>
          {/* Route publique */}
          <Route path="/login" element={<LoginPage />} />

          {/* Routes privées */}
          <Route
            path="/"
            element={
              <PrivateRoute>
                <DashboardPage />
              </PrivateRoute>
            }
          />

          <Route
            path="/servers"
            element={
              <PrivateRoute>
                <ServersPage />
              </PrivateRoute>
            }
          />

          <Route
            path="/projects"
            element={
              <PrivateRoute>
                <ProjectsPage />
              </PrivateRoute>
            }
          />

          <Route
            path="/alerts"
            element={
              <PrivateRoute>
                <AlertsPage />
              </PrivateRoute>
            }
          />

          <Route
            path="/logs"
            element={
              <PrivateRoute>
                <LogsPage />
              </PrivateRoute>
            }
          />

          {/* Routes admin */}
          <Route
            path="/admin"
            element={
              <AdminRoute>
                <AdminDashboardPage />
              </AdminRoute>
            }
          />

          <Route
            path="/admin/users"
            element={
              <AdminRoute>
                <AdminUsersPage />
              </AdminRoute>
            }
          />

          {/* Redirection par défaut */}
          <Route path="*" element={<Navigate to="/" replace />} />

<Route
  path="/alert-rules"
  element={
    <PrivateRoute>
      <AlertRulesPage />
    </PrivateRoute>
  }
/>
<Route
  path="/admin/logs"
  element={
    <AdminRoute>
      <AdminLogsPage />
    </AdminRoute>
  }
/>
        </Routes>
      </BrowserRouter>
    </AuthProvider>
  );
}
