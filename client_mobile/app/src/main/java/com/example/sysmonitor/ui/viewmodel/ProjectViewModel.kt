package com.example.sysmonitor.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sysmonitor.data.model.CreateProjectRequest
import com.example.sysmonitor.data.model.ProjectModel
import com.example.sysmonitor.data.model.ServerDropdownItem
import com.example.sysmonitor.data.repository.ProjectRepositoryImpl
import com.example.sysmonitor.data.repository.TokenManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ── List State
data class ProjectListUiState(
    val isLoading: Boolean         = false,
    val projects: List<ProjectModel> = emptyList(),
    val errorMessage: String?      = null
)

// ── Create State
data class ProjectCreateUiState(
    val isLoading: Boolean              = false,
    val isSuccess: Boolean              = false,
    val errorMessage: String?           = null,
    val servers: List<ServerDropdownItem> = emptyList(),
    val serversLoading: Boolean         = false
)

class ProjectViewModel(application: Application) : AndroidViewModel(application) {

    private val _listState   = MutableStateFlow(ProjectListUiState())
    val listState: StateFlow<ProjectListUiState> = _listState.asStateFlow()

    private val _createState = MutableStateFlow(ProjectCreateUiState())
    val createState: StateFlow<ProjectCreateUiState> = _createState.asStateFlow()

    private val repo = ProjectRepositoryImpl(TokenManager(application))

    init { loadProjects() }

    fun loadProjects() {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, errorMessage = null) }
            repo.getProjects().fold(
                onSuccess = { list -> _listState.update { it.copy(isLoading = false, projects = list) } },
                onFailure = { err -> _listState.update { it.copy(isLoading = false, errorMessage = err.message) } }
            )
        }
    }

    fun loadServers() {
        viewModelScope.launch {
            _createState.update { it.copy(serversLoading = true) }
            repo.getServersForDropdown().fold(
                onSuccess = { list -> _createState.update { it.copy(serversLoading = false, servers = list) } },
                onFailure = { _createState.update { it.copy(serversLoading = false) } }
            )
        }
    }

    fun createProject(name: String, description: String?, serverId: String?) {
        viewModelScope.launch {
            _createState.update { it.copy(isLoading = true, errorMessage = null) }
            repo.createProject(CreateProjectRequest(name, description, serverId)).fold(
                onSuccess = {
                    _createState.update { it.copy(isLoading = false, isSuccess = true) }
                    loadProjects()
                },
                onFailure = { err ->
                    _createState.update { it.copy(isLoading = false, errorMessage = err.message) }
                }
            )
        }
    }

    fun deleteProject(id: String) {
        viewModelScope.launch {
            repo.deleteProject(id).fold(
                onSuccess = { loadProjects() },
                onFailure = { err -> _listState.update { it.copy(errorMessage = err.message) } }
            )
        }
    }

    fun resetCreateState() { _createState.update { ProjectCreateUiState() } }
}