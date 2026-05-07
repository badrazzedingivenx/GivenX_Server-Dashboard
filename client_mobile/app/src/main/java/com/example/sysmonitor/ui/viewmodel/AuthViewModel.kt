package com.example.sysmonitor.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.sysmonitor.data.repository.AuthRepositoryImpl
import com.example.sysmonitor.data.repository.TokenManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean    = false,
    val isSuccess: Boolean    = false,
    val errorMessage: String? = null,
    val token: String?        = null
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val authRepository = AuthRepositoryImpl()
    val tokenManager = TokenManager(application)

    // ── LOGIN — REAL API
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            authRepository.login(email, password).fold(
                onSuccess = { response ->
                    tokenManager.saveToken(response.resolvedToken!!)
                    _uiState.update { it.copy(isLoading = false, isSuccess = true, token = response.resolvedToken) }
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
            )
        }
    }

    // ── REGISTER — MOCK (replace when endpoint is ready)
    fun register(email: String, password: String, name: String = "") {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            delay(1000)
            if (email.isBlank() || password.isBlank()) {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Champs requis") }
                return@launch
            }
            _uiState.update { it.copy(isLoading = false, isSuccess = true, token = "mock_token") }
        }
    }

    fun logout() { tokenManager.clearToken(); _uiState.update { AuthUiState() } }
    fun resetState() { _uiState.update { AuthUiState() } }
}