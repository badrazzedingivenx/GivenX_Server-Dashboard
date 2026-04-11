package com.example.sysmonitor.data.repository

import com.example.sysmonitor.data.model.CreateServerRequest
import com.example.sysmonitor.data.model.ServerModel
import com.example.sysmonitor.data.model.ServerStatus
import kotlinx.coroutines.delay

class ServerRepositoryImpl : ServerRepository {

    // Fake data — replace with real API later
    private val fakeServers = mutableListOf(
        ServerModel(
            id          = "1",
            name        = "Serveur Principal",
            ipAddress   = "192.168.1.1",
            status      = ServerStatus.ONLINE,
            cpuUsage    = 45.5f,
            ramUsage    = 62.3f,
            os          = "Ubuntu 22.04",
            location    = "Paris, FR",
            uptimeHours = 720
        ),
        ServerModel(
            id          = "2",
            name        = "Serveur de Backup",
            ipAddress   = "192.168.1.2",
            status      = ServerStatus.ONLINE,
            cpuUsage    = 12.1f,
            ramUsage    = 30.0f,
            os          = "Debian 11",
            location    = "Lyon, FR",
            uptimeHours = 480
        ),
        ServerModel(
            id          = "3",
            name        = "Serveur de Test",
            ipAddress   = "192.168.1.3",
            status      = ServerStatus.STOPPED,
            cpuUsage    = 0f,
            ramUsage    = 0f,
            os          = "CentOS 8",
            location    = "Marseille, FR",
            uptimeHours = 0
        )
    )

    override suspend fun getServers(): Result<List<ServerModel>> {
        delay(1000)
        return Result.success(fakeServers.toList())
    }

    override suspend fun getServerById(id: String): Result<ServerModel> {
        delay(800)
        val server = fakeServers.find { it.id == id }
        return if (server != null) {
            Result.success(server)
        } else {
            Result.failure(Exception("Serveur introuvable"))
        }
    }

    override suspend fun createServer(
        request: CreateServerRequest
    ): Result<ServerModel> {
        delay(1200)
        val newServer = ServerModel(
            id          = (fakeServers.size + 1).toString(),
            name        = request.name,
            ipAddress   = request.ipAddress,
            status      = ServerStatus.ONLINE,
            cpuUsage    = 0f,
            ramUsage    = 0f,
            os          = request.os,
            location    = request.location,
            uptimeHours = 0
        )
        fakeServers.add(newServer)
        return Result.success(newServer)
    }

    override suspend fun restartServer(id: String): Result<Boolean> {
        delay(1500)
        val index = fakeServers.indexOfFirst { it.id == id }
        return if (index != -1) {
            fakeServers[index] = fakeServers[index].copy(
                status = ServerStatus.RESTARTING
            )
            Result.success(true)
        } else {
            Result.failure(Exception("Serveur introuvable"))
        }
    }

    override suspend fun stopServer(id: String): Result<Boolean> {
        delay(1500)
        val index = fakeServers.indexOfFirst { it.id == id }
        return if (index != -1) {
            fakeServers[index] = fakeServers[index].copy(
                status   = ServerStatus.STOPPED,
                cpuUsage = 0f,
                ramUsage = 0f
            )
            Result.success(true)
        } else {
            Result.failure(Exception("Serveur introuvable"))
        }
    }
}