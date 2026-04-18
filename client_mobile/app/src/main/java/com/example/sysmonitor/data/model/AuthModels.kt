package com.example.sysmonitor.data.model

data class LoginRequest(
    val email: String,
    val password: String
)

data class LoginResponse(
    val token: String,
    val user: UserData? = null
)

data class UserData(
    val id: Int? = null,
    val name: String? = null,
    val email: String? = null
)