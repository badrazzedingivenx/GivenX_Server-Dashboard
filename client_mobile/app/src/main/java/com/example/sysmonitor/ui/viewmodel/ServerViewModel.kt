package com.example.sysmonitor.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sysmonitor.data.model.CreateServerRequest
import com.example.sysmonitor.data.model.ServerModel
import com.example.sysmonitor.data.repository.ServerRepository
import com.example.sysmonitor.data.repository.ServerRepositoryImpl
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ─────────────────────────────────────────
// UI STATES
// ─────────────────────────────────────────

data class ServerListUiState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val servers: List<ServerModel> = emptyList(),
    val errorMessage: String? = null
)

data class ServerDetailUiState(
    val isLoading: Boolean = true,
    val server: ServerModel? = null,
    val errorMessage: String? = null,
    val actionSuccess: String? = null,
    val actionLoading: Boolean = false
)

data class CreateServerUiState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val errorMessage: String? = null
)

// ─────────────────────────────────────────
// VIEWMODEL
// ─────────────────────────────────────────

class ServerViewModel(
    private val repository: ServerRepository = ServerRepositoryImpl()
) : ViewModel() {

    // List state
    private val _listState = MutableStateFlow(ServerListUiState())
    val listState: StateFlow<ServerListUiState> = _listState.asStateFlow()

    // Detail state
    private val _detailState = MutableStateFlow(ServerDetailUiState())
    val detailState: StateFlow<ServerDetailUiState> = _detailState.asStateFlow()

    // Create state
    private val _createState = MutableStateFlow(CreateServerUiState())
    val createState: StateFlow<CreateServerUiState> = _createState.asStateFlow()

    // ── LIST ──────────────────────────────

    init { loadServers() }

    fun loadServers() {
        viewModelScope.launch {
            _listState.update {
                it.copy(isLoading = true, errorMessage = null)
            }
            repository.getServers().fold(
                onSuccess = { servers ->
                    _listState.update {
                        it.copy(
                            isLoading    = false,
                            isRefreshing = false,
                            servers      = servers,
                            errorMessage = null
                        )
                    }
                },
                onFailure = { error ->
                    _listState.update {
                        it.copy(
                            isLoading    = false,
                            isRefreshing = false,
                            errorMessage = error.message
                        )
                    }
                }
            )
        }
    }

    fun refreshServers() {
        viewModelScope.launch {
            _listState.update { it.copy(isRefreshing = true) }
            repository.getServers().fold(
                onSuccess = { servers ->
                    _listState.update {
                        it.copy(
                            isRefreshing = false,
                            servers      = servers,
                            errorMessage = null
                        )
                    }
                },
                onFailure = { error ->
                    _listState.update {
                        it.copy(
                            isRefreshing = false,
                            errorMessage = error.message
                        )
                    }
                }
            )
        }
    }

    // ── DETAIL ────────────────────────────

    fun loadServerDetail(id: String) {
        viewModelScope.launch {
            _detailState.update {
                it.copy(isLoading = true, errorMessage = null)
            }
            repository.getServerById(id).fold(
                onSuccess = { server ->
                    _detailState.update {
                        it.copy(isLoading = false, server = server)
                    }
                },
                onFailure = { error ->
                    _detailState.update {
                        it.copy(
                            isLoading    = false,
                            errorMessage = error.message
                        )
                    }
                }
            )
        }
    }

    // ── ACTIONS ───────────────────────────

    fun restartServer(id: String) {
        viewModelScope.launch {
            _detailState.update {
                it.copy(actionLoading = true, actionSuccess = null)
            }
            repository.restartServer(id).fold(
                onSuccess = {
                    _detailState.update {
                        it.copy(
                            actionLoading = false,
                            actionSuccess = "Redémarrage en cours…"
                        )
                    }
                    loadServerDetail(id)
                },
                onFailure = { error ->
                    _detailState.update {
                        it.copy(
                            actionLoading = false,
                            errorMessage  = error.message
                        )
                    }
                }
            )
        }
    }

    fun stopServer(id: String) {
        viewModelScope.launch {
            _detailState.update {
                it.copy(actionLoading = true, actionSuccess = null)
            }
            repository.stopServer(id).fold(
                onSuccess = {
                    _detailState.update {
                        it.copy(
                            actionLoading = false,
                            actionSuccess = "Serveur arrêté avec succès"
                        )
                    }
                    loadServerDetail(id)
                },
                onFailure = { error ->
                    _detailState.update {
                        it.copy(
                            actionLoading = false,
                            errorMessage  = error.message
                        )
                    }
                }
            )
        }
    }

    fun clearDetailMessages() {
        _detailState.update {
            it.copy(actionSuccess = null, errorMessage = null)
        }
    }

    // ── CREATE ────────────────────────────

    fun createServer(request: CreateServerRequest) {
        viewModelScope.launch {
            _createState.update {
                it.copy(isLoading = true, errorMessage = null)
            }
            repository.createServer(request).fold(
                onSuccess = {
                    _createState.update {
                        it.copy(isLoading = false, success = true)
                    }
                    loadServers()
                },
                onFailure = { error ->
                    _createState.update {
                        it.copy(
                            isLoading    = false,
                            errorMessage = error.message
                        )
                    }
                }
            )
        }
    }

    fun resetCreateState() {
        _createState.update { CreateServerUiState() }
    }
}