package com.example.financeapp.ui.screens.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.domain.repository.AuthRepository
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isLoggedIn: Boolean = false
)

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    var state by mutableStateOf(AuthUiState())
        private set

    init {
        // Observa o estado de autenticação em tempo real (controle de sessão)
        viewModelScope.launch {
            authRepository.currentUser.collect { user ->
                state = state.copy(isLoggedIn = user != null)
            }
        }
    }

    fun signIn(email: String, password: String) {
        if (!validate(email, password)) return
        viewModelScope.launch {
            state = state.copy(isLoading = true, errorMessage = null)
            val result = authRepository.signIn(email.trim(), password)
            state = if (result.isSuccess) {
                state.copy(isLoading = false)
            } else {
                state.copy(
                    isLoading = false,
                    errorMessage = friendlyError(result.exceptionOrNull())
                )
            }
        }
    }

    fun signUp(email: String, password: String, confirmPassword: String) {
        if (password != confirmPassword) {
            state = state.copy(errorMessage = "As senhas não coincidem.")
            return
        }
        if (!validate(email, password)) return
        viewModelScope.launch {
            state = state.copy(isLoading = true, errorMessage = null)
            val result = authRepository.signUp(email.trim(), password)
            state = if (result.isSuccess) {
                state.copy(isLoading = false, successMessage = "Conta criada com sucesso!")
            } else {
                state.copy(
                    isLoading = false,
                    errorMessage = friendlyError(result.exceptionOrNull())
                )
            }
        }
    }

    fun sendPasswordReset(email: String) {
        if (email.isBlank()) {
            state = state.copy(errorMessage = "Informe seu e-mail para redefinir a senha.")
            return
        }
        viewModelScope.launch {
            state = state.copy(isLoading = true, errorMessage = null)
            val result = authRepository.sendPasswordReset(email.trim())
            state = if (result.isSuccess) {
                state.copy(
                    isLoading = false,
                    successMessage = "E-mail de redefinição enviado para $email"
                )
            } else {
                state.copy(
                    isLoading = false,
                    errorMessage = friendlyError(result.exceptionOrNull())
                )
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
    }

    fun clearMessages() {
        state = state.copy(errorMessage = null, successMessage = null)
    }

    // ── helpers ──────────────────────────────────────────────────────────

    private fun validate(email: String, password: String): Boolean {
        return when {
            email.isBlank() -> {
                state = state.copy(errorMessage = "E-mail não pode ser vazio.")
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches() -> {
                state = state.copy(errorMessage = "E-mail inválido.")
                false
            }
            password.length < 6 -> {
                state = state.copy(errorMessage = "A senha deve ter ao menos 6 caracteres.")
                false
            }
            else -> true
        }
    }

    private fun friendlyError(e: Throwable?): String {
        val msg = e?.message ?: "Erro desconhecido."
        return when {
            // Usuário não encontrado
            "no user record" in msg.lowercase() ||
                    "user-not-found" in msg.lowercase()
                -> "Usuário não encontrado."

            // Senha incorreta
            "password is invalid" in msg.lowercase() ||
                    "wrong-password" in msg.lowercase() ||
                    "credential is incorrect" in msg.lowercase() ||
                    "malformed" in msg.lowercase()
                -> "E-mail ou senha incorretos."

            // E-mail já cadastrado
            "email address is already" in msg.lowercase() ||
                    "email-already-in-use" in msg.lowercase()
                -> "Este e-mail já está cadastrado."

            // E-mail inválido
            "badly formatted" in msg.lowercase() ||
                    "invalid-email" in msg.lowercase()
                -> "E-mail inválido."

            // Sem conexão
            "network" in msg.lowercase() ||
                    "unable to resolve host" in msg.lowercase()
                -> "Sem conexão com a internet."

            // Token expirado / sessão inválida
            "expired" in msg.lowercase()
                -> "Sessão expirada. Faça login novamente."

            // Muitas tentativas
            "too many requests" in msg.lowercase() ||
                    "blocked" in msg.lowercase()
                -> "Muitas tentativas. Tente novamente mais tarde."

            else -> "Erro de autenticação. Tente novamente."
        }
    }
}
