package com.cerotek.imonitor.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cerotek.imonitor.IMonitorApplication
import com.cerotek.imonitor.network.model.LoginRequest
import com.cerotek.imonitor.network.model.LoginResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val token: String, val loginResponse: LoginResponse) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    private val apiClient = IMonitorApplication.instance.apiClient
    
    var currentUsername: String = ""
        private set
    var currentPassword: String = ""
        private set
    
    fun setCredentials(username: String, password: String) {
        currentUsername = username
        currentPassword = password
    }

    fun login(username: String, password: String) {
        currentUsername = username
        currentPassword = password
        
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            
            try {
                val endpoint = IMonitorApplication.instance.apiClient.loginEndpoint
                val response = apiClient.apiService.login(
                    endpoint,
                    LoginRequest(username, password)
                )
                
                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    _loginState.value = LoginState.Success(loginResponse.token, loginResponse)
                } else {
                    val errorBody = response.errorBody()?.string() ?: "No error details"
                    val errorMsg = buildString {
                        appendLine("❌ Login fallito")
                        appendLine()
                        appendLine("Codice: ${response.code()}")
                        appendLine("Endpoint: $endpoint")
                        appendLine()
                        if (response.code() == 404) {
                            appendLine("⚠️ Endpoint non trovato!")
                            appendLine()
                            appendLine("Prova questi:")
                            appendLine("• login")
                            appendLine("• signin")
                            appendLine("• api/signin")
                            appendLine("• auth/login")
                            appendLine()
                            appendLine("Tocca 5 volte sul logo per debug mode")
                        } else {
                            appendLine("Dettagli: $errorBody")
                        }
                    }
                    _loginState.value = LoginState.Error(errorMsg)
                }
            } catch (e: Exception) {
                _loginState.value = LoginState.Error(
                    "Network error: ${e.message ?: e.localizedMessage}"
                )
            }
        }
    }
}
