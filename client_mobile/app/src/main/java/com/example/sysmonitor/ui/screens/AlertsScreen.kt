package com.example.sysmonitor.data.model

data class AlertModel(
    val id: String,
    val title: String,
    val description: String,
    val type: AlertType,
    val date: String,
    val isResolved: Boolean = false
)

enum class AlertType {
    CRITICAL,
    WARNING,
    INFO
}