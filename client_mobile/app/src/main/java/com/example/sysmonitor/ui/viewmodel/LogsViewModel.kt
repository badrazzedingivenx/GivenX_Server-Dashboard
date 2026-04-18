package com.example.sysmonitor.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sysmonitor.data.model.LogAction
import com.example.sysmonitor.data.model.LogModel
import com.example.sysmonitor.data.repository.LogsRepository
import com.example.sysmonitor.data.repository.MockLogsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LogsUiState(
    val isLoading: Boolean = true,
    val allLogs: List<LogModel> = emptyList(),
    val filteredLogs: List<LogModel> = emptyList(),
    val searchQuery: String = "",
    val selectedAction: LogAction? = null,
    val errorMessage: String? = null,
    val totalLogs: Int = 0,
    val totalLogins: Int = 0,
    val totalActions: Int = 0,
    val totalErrors: Int = 0
)

class LogsViewModel(
    private val repository: LogsRepository = MockLogsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(LogsUiState())
    val uiState: StateFlow<LogsUiState> = _uiState.asStateFlow()

    init { loadLogs() }

    fun loadLogs() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            repository.getLogs().fold(
                onSuccess = { logs ->
                    _uiState.update {
                        it.copy(
                            isLoading    = false,
                            allLogs      = logs,
                            filteredLogs = applyFilters(logs, it.searchQuery, it.selectedAction),
                            totalLogs    = logs.size,
                            totalLogins  = logs.count { l -> l.action == LogAction.LOGIN },
                            totalActions = logs.count { l ->
                                l.action == LogAction.CREATE ||
                                        l.action == LogAction.DELETE ||
                                        l.action == LogAction.UPDATE
                            },
                            totalErrors = logs.count { l -> l.isError }
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.message)
                    }
                }
            )
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update {
            it.copy(
                searchQuery  = query,
                filteredLogs = applyFilters(it.allLogs, query, it.selectedAction)
            )
        }
    }

    fun onActionFilterChange(action: LogAction?) {
        _uiState.update {
            it.copy(
                selectedAction = action,
                filteredLogs   = applyFilters(it.allLogs, it.searchQuery, action)
            )
        }
    }

    private fun applyFilters(
        logs: List<LogModel>,
        query: String,
        action: LogAction?
    ): List<LogModel> = logs.filter { log ->
        val matchesQuery = query.isBlank() ||
                log.user.contains(query, ignoreCase = true) ||
                log.action.label.contains(query, ignoreCase = true) ||
                log.description.contains(query, ignoreCase = true)
        val matchesAction = action == null || log.action == action
        matchesQuery && matchesAction
    }
}