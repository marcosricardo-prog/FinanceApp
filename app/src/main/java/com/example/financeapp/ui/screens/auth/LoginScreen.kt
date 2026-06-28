package com.example.financeapp.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import org.koin.androidx.compose.koinViewModel

enum class AuthTab { LOGIN, CADASTRO, RECUPERAR }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel = koinViewModel(),
    onAuthenticated: () -> Unit
) {
    val state = viewModel.state
    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTab by remember { mutableStateOf(AuthTab.LOGIN) }

    // Navega ao ser autenticado
    LaunchedEffect(state.isLoggedIn) {
        if (state.isLoggedIn) onAuthenticated()
    }

    // Exibe mensagens via Snackbar
    LaunchedEffect(state.errorMessage) {
        state.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }
    LaunchedEffect(state.successMessage) {
        state.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))

            Text(
                text = "FinanceApp",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Controle suas finanças com segurança",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(32.dp))

            // Abas de navegação
            TabRow(selectedTabIndex = selectedTab.ordinal) {
                Tab(
                    selected = selectedTab == AuthTab.LOGIN,
                    onClick = { selectedTab = AuthTab.LOGIN },
                    text = { Text("Entrar") }
                )
                Tab(
                    selected = selectedTab == AuthTab.CADASTRO,
                    onClick = { selectedTab = AuthTab.CADASTRO },
                    text = { Text("Cadastrar") }
                )
                Tab(
                    selected = selectedTab == AuthTab.RECUPERAR,
                    onClick = { selectedTab = AuthTab.RECUPERAR },
                    text = { Text("Recuperar") }
                )
            }

            Spacer(Modifier.height(24.dp))

            when (selectedTab) {
                AuthTab.LOGIN -> LoginTab(viewModel = viewModel, isLoading = state.isLoading)
                AuthTab.CADASTRO -> CadastroTab(viewModel = viewModel, isLoading = state.isLoading)
                AuthTab.RECUPERAR -> RecuperarTab(viewModel = viewModel, isLoading = state.isLoading)
            }
        }
    }
}

// ── Tab: Login ────────────────────────────────────────────────────────

@Composable
private fun LoginTab(viewModel: AuthViewModel, isLoading: Boolean) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    EmailField(email) { email = it }
    Spacer(Modifier.height(12.dp))
    PasswordField(password, passwordVisible, { password = it }, { passwordVisible = !passwordVisible })
    Spacer(Modifier.height(24.dp))
    AuthButton(label = "Entrar", isLoading = isLoading) {
        viewModel.signIn(email, password)
    }
}

// ── Tab: Cadastro ─────────────────────────────────────────────────────

@Composable
private fun CadastroTab(viewModel: AuthViewModel, isLoading: Boolean) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    EmailField(email) { email = it }
    Spacer(Modifier.height(12.dp))
    PasswordField(password, passwordVisible, { password = it }, { passwordVisible = !passwordVisible })
    Spacer(Modifier.height(12.dp))
    OutlinedTextField(
        value = confirm,
        onValueChange = { confirm = it },
        label = { Text("Confirmar senha") },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        singleLine = true
    )
    Spacer(Modifier.height(24.dp))
    AuthButton(label = "Criar conta", isLoading = isLoading) {
        viewModel.signUp(email, password, confirm)
    }
}

// ── Tab: Recuperar ────────────────────────────────────────────────────

@Composable
private fun RecuperarTab(viewModel: AuthViewModel, isLoading: Boolean) {
    var email by remember { mutableStateOf("") }

    Text(
        text = "Informe seu e-mail e enviaremos um link para redefinir sua senha.",
        style = MaterialTheme.typography.bodyMedium
    )
    Spacer(Modifier.height(16.dp))
    EmailField(email) { email = it }
    Spacer(Modifier.height(24.dp))
    AuthButton(label = "Enviar link", isLoading = isLoading) {
        viewModel.sendPasswordReset(email)
    }
}

// ── Componentes reutilizáveis ─────────────────────────────────────────

@Composable
private fun EmailField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("E-mail") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true
    )
}

@Composable
private fun PasswordField(
    value: String,
    visible: Boolean,
    onValueChange: (String) -> Unit,
    onToggleVisibility: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Senha") },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            IconButton(onClick = onToggleVisibility) {
                Icon(
                    imageVector = if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = if (visible) "Ocultar senha" else "Mostrar senha"
                )
            }
        },
        singleLine = true
    )
}

@Composable
private fun AuthButton(label: String, isLoading: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier.fillMaxWidth().height(50.dp)
    ) {
        AnimatedVisibility(visible = isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(text = label)
    }
}
