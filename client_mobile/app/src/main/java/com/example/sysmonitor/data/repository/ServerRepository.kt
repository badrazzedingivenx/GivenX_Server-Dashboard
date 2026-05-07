package com.example.sysmonitor.data.repository

import com.example.sysmonitor.data.model.CreateServerRequest
import com.example.sysmonitor.data.model.ServerModel

// Interface ONLY — implementation is in ServerRepositoryImpl.kt
interface ServerRepository {
    suspend fun getServers(): Result<List<ServerModel>>
    suspend fun createServer(request: CreateServerRequest): Result<ServerModel>
    suspend fun deleteServer(id: String): Result<Unit>
}