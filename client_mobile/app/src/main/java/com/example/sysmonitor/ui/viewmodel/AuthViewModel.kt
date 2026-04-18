package com.example.sysmonitor.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ─────────────────────────────────────────
// UI STATE
// ─────────────────────────────────────────

data class AuthUiState(
    val isLoading: Boolean   = false,
    val isSuccess: Boolean   = false,
    val errorMessage: String? = null,
    val token: String?        = null
)

// ─────────────────────────────────────────
// VIEWMODEL — MOCK MODE (no API)
// To reconnect API later:
//   1. Inject AuthRepository
//   2. Replace mock blocks with repository.login() / repository.register()
// ─────────────────────────────────────────

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    // ── LOGIN ─────────────────────────────
    // Mock: any non-empty email + password works
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            delay(1000) // simulate network

            if (email.isBlank() || password.isBlank()) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Email et mot de passe requis")
                }
                return@launch
            }

            // ✅ Mock success — accept any credentials
            _uiState.update {
                it.copy(isLoading = false, isSuccess = true, token = "mock_token_123")
            }
        }
    }

    // ── REGISTER ──────────────────────────
    // Mock: any valid email + password registers successfully
    fun register(email: String, password: String, name: String = "") {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            delay(1200) // simulate network

            if (email.isBlank() || password.isBlank()) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "Email et mot de passe requis")
                }
                return@launch
            }

            // ✅ Mock success
            _uiState.update {
                it.copy(isLoading = false, isSuccess = true, token = "mock_token_456")
            }
        }
    }

    fun resetState() {
        _uiState.update { AuthUiState() }
    }

    fun logout() {
        _uiState.update { AuthUiState() }
    }
}