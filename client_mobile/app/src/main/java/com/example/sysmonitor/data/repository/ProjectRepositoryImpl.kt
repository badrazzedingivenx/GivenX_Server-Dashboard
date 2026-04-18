package com.example.sysmonitor.data.repository

import com.example.sysmonitor.data.model.CreateProjectRequest
import com.example.sysmonitor.data.model.ProjectModel
import com.example.sysmonitor.data.model.ServerDropdownItem
import com.example.sysmonitor.data.model.ServerInfo
import kotlinx.coroutines.delay

class ProjectRepositoryImpl : ProjectRepository {

    // ✅ MOCK — replace with api.getProjects() when backend is ready
    private val fakeProjects = mutableListOf(
        ProjectModel("1", "Application Mobile",  "App Android SysMonitor",      "active",   "1", ServerInfo("1", "Serveur Principal")),
        ProjectModel("2", "API Backend",          "REST API Node.js",            "active",   "2", ServerInfo("2", "Serveur de Backup")),
        ProjectModel("3", "Dashboard Web",        "Interface React.js",          "inactive", "4", ServerInfo("4", "Serveur Prod")),
        ProjectModel("4", "Monitoring Service",   "Service de surveillance",     "active",   "1", ServerInfo("1", "Serveur Principal")),
        ProjectModel("5", "Base de données",      "PostgreSQL cluster",          "active",   "2", ServerInfo("2", "Serveur de Backup")),
    )

    override suspend fun getProjects(): Result<List<ProjectModel>> {
        delay(800)
        return Result.success(fakeProjects.toList())
    }

    override suspend fun createProject(request: CreateProjectRequest): Result<ProjectModel> {
        delay(1000)
        val new = ProjectModel(
            id          = (fakeProjects.size + 1).toString(),
            name        = request.name,
            description = request.description,
            status      = "active",
            serverId    = request.serverId
        )
        fakeProjects.add(new)
        return Result.success(new)
    }

    override suspend fun getServersForDropdown(): Result<List<ServerDropdownItem>> {
        delay(500)
        return Result.success(listOf(
            ServerDropdownItem("1", "Serveur Principal"),
            ServerDropdownItem("2", "Serveur de Backup"),
            ServerDropdownItem("4", "Serveur Prod"),
            ServerDropdownItem("5", "Serveur Staging"),
        ))
    }
}