package com.example.sysmonitor.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sysmonitor.data.model.MetricsModel   // ✅ now in ApiModels.kt
import com.example.sysmonitor.data.repository.RetrofitClient
import com.example.sysmonitor.data.repository.TokenManager
import com.example.sysmonitor.data.repository.safeApiCall
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DashboardUiState(
    val isLoading: Boolean     = false,
    val isRefreshing: Boolean  = false,
    val metrics: MetricsModel? = null,
    val errorMessage: String?  = null
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val api          = RetrofitClient.apiService
    private val tokenManager = TokenManager(application)

    init { loadMetrics() }

    fun loadMetrics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            safeApiCall { api.getServerMetrics(tokenManager.bearerToken()) }.fold(
                onSuccess = { metrics ->
                    _uiState.update { it.copy(isLoading = false, metrics = metrics) }
                },
                onFailure = { err ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = err.message) }
                }
            )
        }
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            safeApiCall { api.getServerMetrics(tokenManager.bearerToken()) }.fold(
                onSuccess = { metrics ->
                    _uiState.update { it.copy(isRefreshing = false, metrics = metrics) }
                },
                onFailure = { err ->
                    _uiState.update { it.copy(isRefreshing = false, errorMessage = err.message) }
                }
            )
        }
    }

    fun clearError() { _uiState.update { it.copy(errorMessage = null) } }
}