package com.example.sysmonitor.data.repository

import com.example.sysmonitor.data.model.CreateProjectRequest
import com.example.sysmonitor.data.model.ProjectModel
import com.example.sysmonitor.data.model.ServerDropdownItem

interface ProjectRepository {
    suspend fun getProjects(): Result<List<ProjectModel>>
    suspend fun createProject(request: CreateProjectRequest): Result<ProjectModel>
    suspend fun getServersForDropdown(): Result<List<ServerDropdownItem>>
}