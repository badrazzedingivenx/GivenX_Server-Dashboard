package com.example.sysmonitor.data.repository

import android.content.Context

class TokenManager(context: Context) {

    private val prefs = context.getSharedPreferences("sysmonitor_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) = prefs.edit().putString(KEY_TOKEN, token).apply()
    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)
    fun clearToken() = prefs.edit().remove(KEY_TOKEN).apply()
    fun isLoggedIn(): Boolean = getToken() != null

    // Returns "Bearer <token>" ready for Authorization header
    fun bearerToken(): String = "Bearer ${getToken().orEmpty()}"

    companion object {
        private const val KEY_TOKEN = "auth_token"
    }
}