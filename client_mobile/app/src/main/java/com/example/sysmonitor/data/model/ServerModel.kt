package com.example.sysmonitor.data.model

data class ServerModel(
    val id: String,
    val name: String,
    val ipAddress: String,
    val status: ServerStatus,
    val cpuUsage: Float,
    val ramUsage: Float,
    val os: String,
    val location: String,
    val uptimeHours: Int
)

enum class ServerStatus {
    ONLINE,
    OFFLINE,
    RESTARTING,
    STOPPED
}

data class CreateServerRequest(
    val name: String,
    val ipAddress: String,
    val os: String,
    val location: String
)