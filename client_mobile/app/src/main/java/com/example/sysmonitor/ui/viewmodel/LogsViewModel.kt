package com.example.sysmonitor.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sysmonitor.data.model.LogAction
import com.example.sysmonitor.data.model.LogModel
import com.example.sysmonitor.data.repository.RetrofitClient
import com.example.sysmonitor.data.repository.TokenManager
import com.example.sysmonitor.data.repository.safeApiCall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LogsUiState(
    val isLoading: Boolean        = false,
    val logs: List<LogModel>      = emptyList(),
    val filteredLogs: List<LogModel> = emptyList(),
    val searchQuery: String       = "",
    val selectedAction: LogAction? = null,
    val errorMessage: String?     = null,
    val totalLogs: Int            = 0,
    val totalLogins: Int          = 0,
    val totalActions: Int         = 0,
    val totalErrors: Int          = 0
)

class LogsViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(LogsUiState())
    val uiState: StateFlow<LogsUiState> = _uiState.asStateFlow()

    private val api          = RetrofitClient.apiService
    private val tokenManager = TokenManager(application)

    init { loadLogs() }

    fun loadLogs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            safeApiCall { api.getLogs(tokenManager.bearerToken()) }.fold(
                onSuccess = { logs ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading    = false,
                            logs         = logs,
                            filteredLogs = applyFilters(logs, state.searchQuery, state.selectedAction),
                            totalLogs    = logs.size,
                            totalLogins  = logs.count { it.action == LogAction.LOGIN },
                            totalActions = logs.count { it.action != LogAction.ERROR },
                            totalErrors  = logs.count { it.action == LogAction.ERROR }
                        )
                    }
                },
                onFailure = { err ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = err.message) }
                }
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { state ->
            state.copy(
                searchQuery  = query,
                filteredLogs = applyFilters(state.logs, query, state.selectedAction)
            )
        }
    }

    fun onActionFilterChange(action: LogAction?) {
        _uiState.update { state ->
            state.copy(
                selectedAction = action,
                filteredLogs   = applyFilters(state.logs, state.searchQuery, action)
            )
        }
    }

    private fun applyFilters(
        logs: List<LogModel>,
        query: String,
        action: LogAction?
    ): List<LogModel> {
        return logs
            .filter { action == null || it.action == action }
            .filter { query.isBlank() || it.user.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true) }
    }
}