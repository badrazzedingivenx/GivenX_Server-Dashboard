package com.example.sysmonitor.data.repository

import com.example.sysmonitor.data.model.LoginRequest
import com.example.sysmonitor.data.model.LoginResponse

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<LoginResponse>
}

class AuthRepositoryImpl : AuthRepository {

    private val api = RetrofitClient.apiService

    override suspend fun login(
        email: String,
        password: String
    ): Result<LoginResponse> {
        return try {
            val response = api.login(LoginRequest(email, password))

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Réponse vide du serveur"))
                }
            } else {
                val error = when (response.code()) {
                    401  -> "Email ou mot de passe incorrect"
                    403  -> "Accès refusé"
                    404  -> "Service introuvable"
                    422  -> "Données invalides"
                    500  -> "Erreur serveur, réessayez plus tard"
                    else -> "Erreur ${response.code()}"
                }
                Result.failure(Exception(error))
            }

        } catch (e: Exception) {
            Result.failure(
                Exception("Connexion impossible. Vérifiez votre réseau.")
            )
        }
    }
}