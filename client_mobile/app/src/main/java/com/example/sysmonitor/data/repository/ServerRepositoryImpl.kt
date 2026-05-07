package com.example.sysmonitor.data.repository

import com.example.sysmonitor.data.model.CreateServerRequest
import com.example.sysmonitor.data.model.ServerModel

class ServerRepositoryImpl(private val tokenManager: TokenManager) : ServerRepository {

    private val api = RetrofitClient.apiService

    override suspend fun getServers(): Result<List<ServerModel>> =
        safeApiCall { api.getServers(tokenManager.bearerToken()) }

    override suspend fun createServer(request: CreateServerRequest): Result<ServerModel> =
        safeApiCall { api.createServer(tokenManager.bearerToken(), request) }

    override suspend fun deleteServer(id: String): Result<Unit> =
        safeApiCall { api.deleteServer(tokenManager.bearerToken(), id) }
}