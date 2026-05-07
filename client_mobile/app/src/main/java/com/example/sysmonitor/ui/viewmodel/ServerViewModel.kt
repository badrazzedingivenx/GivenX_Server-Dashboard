package com.example.sysmonitor.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sysmonitor.data.model.CreateServerRequest
import com.example.sysmonitor.data.model.ServerModel
import com.example.sysmonitor.data.repository.ServerRepositoryImpl
import com.example.sysmonitor.data.repository.TokenManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ─────────────────────────────────────────
// LIST STATE
// ─────────────────────────────────────────

data class ServerListUiState(
    val isLoading: Boolean         = false,
    val servers: List<ServerModel> = emptyList(),
    val errorMessage: String?      = null
)

// ─────────────────────────────────────────
// CREATE STATE
// ─────────────────────────────────────────

data class ServerCreateUiState(
    val isLoading: Boolean    = false,
    val isSuccess: Boolean    = false,   // ✅ isSuccess (not "success")
    val errorMessage: String? = null
)

// ─────────────────────────────────────────
// DETAIL STATE  ← NEW
// ─────────────────────────────────────────

data class ServerDetailUiState(
    val isLoading: Boolean    = false,
    val server: ServerModel?  = null,
    val errorMessage: String? = null,
    val actionSuccess: String? = null,  // e.g. "Serveur redémarré"
    val actionError: String?  = null
)

// ─────────────────────────────────────────
// VIEWMODEL
// ─────────────────────────────────────────

class ServerViewModel(application: Application) : AndroidViewModel(application) {

    private val _listState   = MutableStateFlow(ServerListUiState())
    val listState: StateFlow<ServerListUiState>   = _listState.asStateFlow()

    private val _createState = MutableStateFlow(ServerCreateUiState())
    val createState: StateFlow<ServerCreateUiState> = _createState.asStateFlow()

    // ✅ NEW — detail state for ServerDetailScreen
    private val _detailState = MutableStateFlow(ServerDetailUiState())
    val detailState: StateFlow<ServerDetailUiState> = _detailState.asStateFlow()

    private val repo = ServerRepositoryImpl(TokenManager(application))

    init { loadServers() }

    // ─────────────────────────────────────
    // LIST
    // ─────────────────────────────────────

    fun loadServers() {
        viewModelScope.launch {
            _listState.update { it.copy(isLoading = true, errorMessage = null) }
            repo.getServers().fold(
                onSuccess = { list ->
                    _listState.update { it.copy(isLoading = false, servers = list) }
                },
                onFailure = { err ->
                    _listState.update { it.copy(isLoading = false, errorMessage = err.message) }
                }
            )
        }
    }

    fun deleteServer(id: String) {
        viewModelScope.launch {
            repo.deleteServer(id).fold(
                onSuccess = { loadServers() },
                onFailure = { err ->
                    _listState.update { it.copy(errorMessage = err.message) }
                }
            )
        }
    }

    // ─────────────────────────────────────
    // CREATE — accepts separate params  ✅
    // ─────────────────────────────────────

    fun createServer(
        name: String,
        ipAddress: String,
        os: String = "",
        location: String = ""
    ) {
        viewModelScope.launch {
            _createState.update { it.copy(isLoading = true, errorMessage = null) }
            repo.createServer(CreateServerRequest(name, ipAddress, os, location)).fold(
                onSuccess = {
                    _createState.update { it.copy(isLoading = false, isSuccess = true) }
                    loadServers()
                },
                onFailure = { err ->
                    _createState.update { it.copy(isLoading = false, errorMessage = err.message) }
                }
            )
        }
    }

    fun resetCreateState() { _createState.update { ServerCreateUiState() } }

    // ─────────────────────────────────────
    // DETAIL — NEW functions  ✅
    // ─────────────────────────────────────

    // Load a single server from the already-loaded list
    fun loadServerDetail(serverId: String) {
        viewModelScope.launch {
            _detailState.update { it.copy(isLoading = true, errorMessage = null) }
            val server = _listState.value.servers.find { it.id == serverId }
            if (server != null) {
                _detailState.update { it.copy(isLoading = false, server = server) }
            } else {
                // Fallback: fetch full list then find
                repo.getServers().fold(
                    onSuccess = { list ->
                        val found = list.find { it.id == serverId }
                        _detailState.update {
                            it.copy(
                                isLoading = false,
                                server    = found,
                                errorMessage = if (found == null) "Serveur introuvable" else null
                            )
                        }
                    },
                    onFailure = { err ->
                        _detailState.update { it.copy(isLoading = false, errorMessage = err.message) }
                    }
                )
            }
        }
    }

    fun restartServer(serverId: String) {
        viewModelScope.launch {
            _detailState.update { it.copy(actionSuccess = null, actionError = null) }
            // Mock action — replace with real API call when endpoint is available
            // e.g. repo.restartServer(serverId)
            delay(1000)
            _detailState.update { it.copy(actionSuccess = "Serveur en cours de redémarrage…") }
        }
    }

    fun stopServer(serverId: String) {
        viewModelScope.launch {
            _detailState.update { it.copy(actionSuccess = null, actionError = null) }
            // Mock action — replace with real API call when endpoint is available
            // e.g. repo.stopServer(serverId)
            delay(1000)
            _detailState.update { it.copy(actionSuccess = "Serveur arrêté") }
        }
    }

    fun clearDetailMessages() {
        _detailState.update { it.copy(actionSuccess = null, actionError = null) }
    }
}