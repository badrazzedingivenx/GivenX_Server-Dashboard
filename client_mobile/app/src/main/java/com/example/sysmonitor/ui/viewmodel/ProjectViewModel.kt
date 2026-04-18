package com.example.sysmonitor.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sysmonitor.data.model.CreateProjectRequest
import com.example.sysmonitor.data.model.ProjectModel
import com.example.sysmonitor.data.model.ServerDropdownItem
import com.example.sysmonitor.data.repository.ProjectRepository
import com.example.sysmonitor.data.repository.ProjectRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ─────────────────────────────────────────
// UI STATES
// ─────────────────────────────────────────

data class ProjectListUiState(
    val isLoading: Boolean = true,
    val projects: List<ProjectModel> = emptyList(),
    val errorMessage: String? = null
)

data class CreateProjectUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val servers: List<ServerDropdownItem> = emptyList(),
    val serversLoading: Boolean = true,
    val errorMessage: String? = null
)

// ─────────────────────────────────────────
// VIEWMODEL
// ─────────────────────────────────────────

class ProjectViewModel(
    private val repository: ProjectRepository = ProjectRepositoryImpl()
) : ViewModel() {

    // ── List state ────────────────────────
    private val _listState = MutableStateFlow(ProjectListUiState())
    val listState: StateFlow<ProjectListUiState> = _listState.asStateFlow()

    // ── Create state ──────────────────────
    private val _createState = MutableStateFlow(CreateProjectUiState())
    val createState: StateFlow<CreateProjectUiState> = _createState.asStateFlow()

    init {
        loadProjects()
    }

    // ── Load projects ─────────────────────
    fun loadProjects() {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getProjects().fold(
                onSuccess = { projects ->
                    _listState.update {
                        it.copy(isLoading = false, projects = projects)
                    }
                },
                onFailure = { error ->
                    _listState.update {
                        it.copy(isLoading = false, errorMessage = error.message)
                    }
                }
            )
        }
    }

    // ── Load servers for dropdown ─────────
    fun loadServers() {
        viewModelScope.launch {
            _createState.update { it.copy(serversLoading = true) }
            repository.getServersForDropdown().fold(
                onSuccess = { servers ->
                    _createState.update {
                        it.copy(serversLoading = false, servers = servers)
                    }
                },
                onFailure = { error ->
                    _createState.update {
                        it.copy(
                            serversLoading = false,
                            errorMessage   = error.message
                        )
                    }
                }
            )
        }
    }

    // ── Create project ────────────────────
    fun createProject(
        name: String,
        description: String?,
        serverId: String?
    ) {
        viewModelScope.launch {
            _createState.update {
                it.copy(isLoading = true, errorMessage = null)
            }
            repository.createProject(
                CreateProjectRequest(
                    name        = name,
                    description = description?.ifBlank { null },
                    serverId    = serverId
                )
            ).fold(
                onSuccess = {
                    _createState.update {
                        it.copy(isLoading = false, isSuccess = true)
                    }
                    loadProjects() // Refresh list
                },
                onFailure = { error ->
                    _createState.update {
                        it.copy(isLoading = false, errorMessage = error.message)
                    }
                }
            )
        }
    }

    fun resetCreateState() {
        _createState.update { CreateProjectUiState() }
    }

    fun clearListError() {
        _listState.update { it.copy(errorMessage = null) }
    }
}