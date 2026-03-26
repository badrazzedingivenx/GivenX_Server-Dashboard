/*
Rôle du fichier :
Ce fichier centralise les appels HTTP vers le backend PHP.

Ce qu’il contient :
- une instance axios
- l’URL de base de l’API
- l’ajout automatique du token Bearer

Pourquoi il existe :
Il évite de répéter la configuration HTTP dans chaque composant React.

Comment il s’intègre dans le projet :
Toutes les pages React utilisent ce client pour communiquer avec l’API.
*/

import axios from "axios";

const client = axios.create({
  baseURL: process.env.REACT_APP_API_URL || "http://localhost:8000",
  headers: {
    "Content-Type": "application/json",
  },
});

client.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});

export default client;
