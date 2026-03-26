/*
Rôle du fichier :
Ce fichier gère l’état global d’authentification côté frontend.

Ce qu’il contient :
- l’utilisateur connecté
- le token
- la fonction login
- la fonction logout

Pourquoi il existe :
L’application a besoin de partager l’état d’authentification entre plusieurs pages.

Comment il s’intègre dans le projet :
Les pages React lisent ce contexte pour savoir si l’utilisateur est connecté.
*/

import React, { createContext, useContext, useEffect, useState } from "react";
import client from "../api/client";

const AuthContext = createContext();

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);

  const fetchMe = async () => {
    const token = localStorage.getItem("token");

    if (!token) {
      setLoading(false);
      return;
    }

    try {
      const response = await client.get("/api/auth/me");
      setUser(response.data.data.user);
    } catch (error) {
      localStorage.removeItem("token");
      setUser(null);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchMe();
  }, []);

  const login = async (email, password) => {
    const response = await client.post("/api/auth/login", { email, password });
    const token = response.data.data.token;

    localStorage.setItem("token", token);
    setUser(response.data.data.user);
    return response.data;
  };

  const logout = async () => {
    try {
      await client.post("/api/auth/logout");
    } catch (error) {
    } finally {
      localStorage.removeItem("token");
      setUser(null);
    }
  };

  return (
    <AuthContext.Provider value={{ user, loading, login, logout, setUser }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}
