package com.example.sysmonitor.data.model

import com.google.gson.annotations.SerializedName

// ═══════════════════════════════════════════
// AUTH
// ═══════════════════════════════════════════

data class LoginRequest(
    @SerializedName("email")    val email: String,
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("access_token") val accessToken: String?,
    @SerializedName("token")        val token: String?,
    @SerializedName("user")         val user: UserResponse?
) {
    val resolvedToken: String? get() = accessToken ?: token
}

data class UserResponse(
    @SerializedName("id")    val id: String?,
    @SerializedName("name")  val name: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("role")  val role: String?
)

data class ApiErrorResponse(
    @SerializedName("message") val message: String?,
    @SerializedName("detail")  val detail: String?
) {
    val resolved: String get() = message ?: detail ?: "Erreur inconnue"
}

// ═══════════════════════════════════════════
// DASHBOARD
// ═══════════════════════════════════════════

data class DashboardResponse(
    @SerializedName("total_servers")     val totalServers: Int = 0,
    @SerializedName("active_servers")    val activeServers: Int = 0,
    @SerializedName("total_projects")    val totalProjects: Int = 0,
    @SerializedName("total_alerts")      val totalAlerts: Int = 0,
    @SerializedName("total_users")       val totalUsers: Int = 0,
    @SerializedName("cpu_usage")         val cpuUsage: Float = 0f,
    @SerializedName("ram_usage")         val ramUsage: Float = 0f,
    @SerializedName("disk_usage")        val diskUsage: Float = 0f,
    @SerializedName("running_processes") val runningProcesses: Int = 0
)

// ═══════════════════════════════════════════
// METRICS
// ═══════════════════════════════════════════

data class MetricsModel(
    @SerializedName("cpu_usage")          val cpuUsage: Float = 0f,
    @SerializedName("ram_usage")          val ramUsage: Float = 0f,
    @SerializedName("disk_usage")         val diskUsage: Float = 0f,
    @SerializedName("running_processes")  val runningProcesses: Int = 0,
    @SerializedName("ram_used_gb")        val ramUsedGb: Float = 0f,
    @SerializedName("ram_total_gb")       val ramTotalGb: Float = 0f,
    @SerializedName("disk_used_gb")       val diskUsedGb: Float = 0f,
    @SerializedName("disk_total_gb")      val diskTotalGb: Float = 0f
)

// ═══════════════════════════════════════════
// SERVERS
// ═══════════════════════════════════════════

// ✅ Single enum — no duplicate in ServerModel.kt
enum class ServerStatus { ONLINE, STOPPED, RESTARTING, UNKNOWN }

data class ServerModel(
    @SerializedName("id")           val id: String,
    @SerializedName("name")         val name: String,
    @SerializedName("ip_address")   val ipAddress: String,
    @SerializedName("status")       val statusRaw: String = "unknown",
    @SerializedName("cpu_usage")    val cpuUsage: Float = 0f,
    @SerializedName("ram_usage")    val ramUsage: Float = 0f,
    @SerializedName("os")           val os: String = "",
    @SerializedName("location")     val location: String = "",
    @SerializedName("uptime_hours") val uptimeHours: Int = 0
) {
    val status: ServerStatus
        get() = when (statusRaw.lowercase()) {
            "online", "active", "running" -> ServerStatus.ONLINE
            "stopped", "offline"          -> ServerStatus.STOPPED
            "restarting"                  -> ServerStatus.RESTARTING
            else                          -> ServerStatus.UNKNOWN
        }
}

data class CreateServerRequest(
    @SerializedName("name")       val name: String,
    @SerializedName("ip_address") val ipAddress: String,
    @SerializedName("os")         val os: String = "",
    @SerializedName("location")   val location: String = ""
)

// ═══════════════════════════════════════════
// PROJECTS
// ═══════════════════════════════════════════

data class ProjectModel(
    @SerializedName("id")          val id: String,
    @SerializedName("name")        val name: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("status")      val status: String = "active",
    @SerializedName("server_id")   val serverId: String? = null,
    @SerializedName("server")      val server: ServerInfo? = null
)

data class ServerInfo(
    @SerializedName("id")   val id: String,
    @SerializedName("name") val name: String
)

data class ServerDropdownItem(
    val id: String,
    val name: String
)

data class CreateProjectRequest(
    @SerializedName("name")        val name: String,
    @SerializedName("description") val description: String? = null,
    @SerializedName("server_id")   val serverId: String? = null
)

// ═══════════════════════════════════════════
// ALERTS
// ═══════════════════════════════════════════

enum class AlertType { CRITICAL, WARNING, INFO }

data class AlertModel(
    @SerializedName("id")          val id: String,
    @SerializedName("title")       val title: String,
    @SerializedName("description") val description: String,
    @SerializedName("type")        val typeRaw: String = "info",
    @SerializedName("date")        val date: String = "",
    @SerializedName("is_resolved") val isResolved: Boolean = false
) {
    val type: AlertType
        get() = when (typeRaw.lowercase()) {
            "critical" -> AlertType.CRITICAL
            "warning"  -> AlertType.WARNING
            else       -> AlertType.INFO
        }
}

// ═══════════════════════════════════════════
// LOGS
// ═══════════════════════════════════════════

enum class LogAction(val label: String) {
    LOGIN("Login"), LOGOUT("Logout"),
    CREATE("Create"), DELETE("Delete"),
    UPDATE("Update"), ERROR("Erreur")
}

data class LogModel(
    @SerializedName("id")          val id: String,
    @SerializedName("user")        val user: String,
    @SerializedName("action")      val actionRaw: String,
    @SerializedName("description") val description: String,
    @SerializedName("date")        val date: String,
    @SerializedName("is_error")    val isError: Boolean = false
) {
    val action: LogAction
        get() = when (actionRaw.uppercase()) {
            "LOGIN"  -> LogAction.LOGIN
            "LOGOUT" -> LogAction.LOGOUT
            "CREATE" -> LogAction.CREATE
            "DELETE" -> LogAction.DELETE
            "UPDATE" -> LogAction.UPDATE
            "ERROR"  -> LogAction.ERROR
            else     -> LogAction.LOGIN
        }
}