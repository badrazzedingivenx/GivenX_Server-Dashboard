package com.example.sysmonitor.data.repository

import com.example.sysmonitor.data.model.CreateProjectRequest
import com.example.sysmonitor.data.model.ProjectModel
import com.example.sysmonitor.data.model.ServerDropdownItem

// Interface ONLY — implementation is in ProjectRepositoryImpl.kt
interface ProjectRepository {
    suspend fun getProjects(): Result<List<ProjectModel>>
    suspend fun createProject(request: CreateProjectRequest): Result<ProjectModel>
    suspend fun deleteProject(id: String): Result<Unit>
    suspend fun getServersForDropdown(): Result<List<ServerDropdownItem>>
}