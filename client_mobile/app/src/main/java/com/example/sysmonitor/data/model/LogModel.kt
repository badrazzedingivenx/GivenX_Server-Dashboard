package com.example.sysmonitor.data.model

data class LogModel(
    val id: String,
    val user: String,
    val action: LogAction,
    val description: String,
    val date: String,
    val isError: Boolean = false
)

enum class LogAction(val label: String) {
    LOGIN("login"),
    LOGOUT("logout"),
    CREATE("create"),
    DELETE("delete"),
    UPDATE("update"),
    ERROR("error")
}