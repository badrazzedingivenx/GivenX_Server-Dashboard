package com.example.sysmonitor.data.repository

import com.example.sysmonitor.data.model.CreateServerRequest
import com.example.sysmonitor.data.model.ServerModel

interface ServerRepository {
    suspend fun getServers(): Result<List<ServerModel>>
    suspend fun getServerById(id: String): Result<ServerModel>
    suspend fun createServer(request: CreateServerRequest): Result<ServerModel>
    suspend fun restartServer(id: String): Result<Boolean>
    suspend fun stopServer(id: String): Result<Boolean>
}