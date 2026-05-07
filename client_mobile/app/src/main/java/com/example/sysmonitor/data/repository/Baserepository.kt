package com.example.sysmonitor.data.repository

import com.google.gson.Gson
import com.example.sysmonitor.data.model.ApiErrorResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response
import java.io.IOException

val gson = Gson()

// ✅ Single generic function — wraps every API call with error handling
suspend fun <T> safeApiCall(call: suspend () -> Response<T>): Result<T> {
    return withContext(Dispatchers.IO) {
        try {
            val response = call()
            when {
                response.isSuccessful -> {
                    val body = response.body()
                    if (body != null) Result.success(body)
                    else Result.failure(Exception("Réponse vide du serveur"))
                }
                response.code() == 401 ->
                    Result.failure(Exception("Non autorisé — reconnectez-vous"))
                response.code() == 403 ->
                    Result.failure(Exception("Accès refusé"))
                response.code() == 404 ->
                    Result.failure(Exception("Ressource introuvable"))
                response.code() >= 500 ->
                    Result.failure(Exception("Erreur serveur (${response.code()})"))
                else -> {
                    val errMsg = try {
                        val err = gson.fromJson(response.errorBody()?.string(), ApiErrorResponse::class.java)
                        err.resolved
                    } catch (e: Exception) { "Erreur ${response.code()}" }
                    Result.failure(Exception(errMsg))
                }
            }
        } catch (e: IOException) {
            Result.failure(Exception("Serveur inaccessible — vérifiez votre connexion"))
        } catch (e: Exception) {
            Result.failure(Exception("Erreur : ${e.message}"))
        }
    }
}