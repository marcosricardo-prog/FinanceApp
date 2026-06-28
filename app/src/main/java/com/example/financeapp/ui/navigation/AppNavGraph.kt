package com.example.financeapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.financeapp.ui.screens.auth.AuthViewModel
import com.example.financeapp.ui.screens.auth.LoginScreen
import com.example.financeapp.ui.screens.home.HomeScreen
import com.example.financeapp.ui.screens.settings.SettingsScreen
import org.koin.androidx.compose.koinViewModel

// ── Rotas ─────────────────────────────────────────────────────────────
sealed class Screen(val route: String) {
    object Login    : Screen("login")
    object Home     : Screen("home")
    object Settings : Screen("settings")
}

// ── Grafo de navegação principal ──────────────────────────────────────
@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = koinViewModel()
) {
    // Guard: se o usuário já está logado, inicia em Home; caso contrário em Login
    val startDestination = if (authViewModel.state.isLoggedIn) {
        Screen.Home.route
    } else {
        Screen.Login.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onAuthenticated = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
    }
}
