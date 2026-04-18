package com.example.sysmonitor.data.repository

import com.example.sysmonitor.data.model.CreateServerRequest
import com.example.sysmonitor.data.model.ServerModel
import com.example.sysmonitor.data.model.ServerStatus
import kotlinx.coroutines.delay

class ServerRepositoryImpl : ServerRepository {

    // ✅ MOCK list — replace with api.getServers() when backend is ready
    private val fakeServers = mutableListOf(
        ServerModel("1", "Serveur Principal", "192.168.1.1",  ServerStatus.ONLINE,     45.5f, 62.3f, "Ubuntu 22.04", "Paris, FR",     720),
        ServerModel("2", "Serveur de Backup", "192.168.1.2",  ServerStatus.ONLINE,     12.1f, 30.0f, "Debian 11",    "Lyon, FR",      480),
        ServerModel("3", "Serveur de Test",   "192.168.1.3",  ServerStatus.STOPPED,    0f,    0f,    "CentOS 8",     "Marseille, FR", 0),
        ServerModel("4", "Serveur Prod",      "10.0.0.1",     ServerStatus.ONLINE,     78.2f, 85.0f, "Ubuntu 20.04", "Paris, FR",     1440),
        ServerModel("5", "Serveur Staging",   "10.0.0.2",     ServerStatus.RESTARTING, 5.0f,  20.0f, "Debian 11",    "Bordeaux, FR",  96),
    )

    override suspend fun getServers(): Result<List<ServerModel>> {
        delay(800)
        return Result.success(fakeServers.toList())
    }

    override suspend fun getServerById(id: String): Result<ServerModel> {
        delay(500)
        return fakeServers.find { it.id == id }
            ?.let { Result.success(it) }
            ?: Result.failure(Exception("Serveur introuvable"))
    }

    override suspend fun createServer(request: CreateServerRequest): Result<ServerModel> {
        delay(1000)
        val new = ServerModel(
            id = (fakeServers.size + 1).toString(),
            name = request.name, ipAddress = request.ipAddress,
            status = ServerStatus.ONLINE, cpuUsage = 0f, ramUsage = 0f,
            os = request.os, location = request.location, uptimeHours = 0
        )
        fakeServers.add(new)
        return Result.success(new)
    }

    override suspend fun restartServer(id: String): Result<Boolean> {
        delay(1000)
        val i = fakeServers.indexOfFirst { it.id == id }
        if (i != -1) fakeServers[i] = fakeServers[i].copy(status = ServerStatus.RESTARTING)
        return Result.success(true)
    }

    override suspend fun stopServer(id: String): Result<Boolean> {
        delay(1000)
        val i = fakeServers.indexOfFirst { it.id == id }
        if (i != -1) fakeServers[i] = fakeServers[i].copy(status = ServerStatus.STOPPED, cpuUsage = 0f, ramUsage = 0f)
        return Result.success(true)
    }
}