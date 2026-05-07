package com.example.sysmonitor.data.repository

import com.example.sysmonitor.data.model.CreateProjectRequest
import com.example.sysmonitor.data.model.ProjectModel
import com.example.sysmonitor.data.model.ServerDropdownItem

class ProjectRepositoryImpl(private val tokenManager: TokenManager) : ProjectRepository {

    private val api = RetrofitClient.apiService

    override suspend fun getProjects(): Result<List<ProjectModel>> =
        safeApiCall { api.getProjects(tokenManager.bearerToken()) }

    override suspend fun createProject(request: CreateProjectRequest): Result<ProjectModel> =
        safeApiCall { api.createProject(tokenManager.bearerToken(), request) }

    override suspend fun deleteProject(id: String): Result<Unit> =
        safeApiCall { api.deleteProject(tokenManager.bearerToken(), id) }

    override suspend fun getServersForDropdown(): Result<List<ServerDropdownItem>> {
        val result = safeApiCall { api.getServers(tokenManager.bearerToken()) }
        return result.map { servers ->
            servers.map { ServerDropdownItem(it.id, it.name) }
        }
    }
}