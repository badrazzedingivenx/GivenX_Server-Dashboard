package com.example.sysmonitor.data.model

import com.google.gson.annotations.SerializedName

data class ProjectModel(
    val id: String,
    val name: String,
    val description: String? = null,
    val status: String = "active",

    @SerializedName("server_id")
    val serverId: String? = null,

    val server: ServerInfo? = null
)

data class ServerInfo(
    val id: String,
    val name: String,
    val ipAddress: String? = null
)

data class CreateProjectRequest(
    val name: String,
    val description: String?,

    @SerializedName("server_id")
    val serverId: String?
)

// Reuse ServerModel from your existing code
// or use this lightweight version for dropdown
data class ServerDropdownItem(
    val id: String,
    val name: String
)