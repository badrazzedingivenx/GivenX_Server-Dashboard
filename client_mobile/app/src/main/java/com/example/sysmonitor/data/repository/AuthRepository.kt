package com.example.sysmonitor.data.repository

import com.example.sysmonitor.data.model.LoginRequest
import com.example.sysmonitor.data.model.LoginResponse

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<LoginResponse>
}

class AuthRepositoryImpl : AuthRepository {

    private val api = RetrofitClient.apiService

    override suspend fun login(email: String, password: String): Result<LoginResponse> {
        val result = safeApiCall { api.login(LoginRequest(email, password)) }

        return result.fold(
            onSuccess = { response ->
                when {
                    // ✅ Token found — success
                    response.resolvedToken != null ->
                        Result.success(response)

                    // ❌ 200 OK but no token in body — log the response to debug
                    else ->
                        Result.failure(Exception(
                            "Connexion réussie mais token absent. " +
                                    "Vérifiez le format de réponse de votre API dans Logcat (tag: OkHttp)"
                        ))
                }
            },
            onFailure = { error ->
                Result.failure(error)
            }
        )
    }
}