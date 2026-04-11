package com.example.sysmonitor.ui.navigation

import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sysmonitor.ui.screens.DashboardScreen
import com.example.sysmonitor.ui.screens.LoginScreen
import com.example.sysmonitor.ui.screens.RegisterScreen
import com.example.sysmonitor.ui.screens.ServerCreateScreen
import com.example.sysmonitor.ui.screens.ServerDetailScreen
import com.example.sysmonitor.ui.screens.ServerListScreen

// ─────────────────────────────────────────
// ROUTE CONSTANTS
// ─────────────────────────────────────────

object Routes {
    const val LOGIN         = "login"
    const val REGISTER      = "register"
    const val DASHBOARD     = "dashboard"
    const val SERVER_LIST   = "server_list"
    const val SERVER_CREATE = "server_create"
    const val SERVER_DETAIL = "server_detail/{serverId}"

    fun serverDetail(id: String) = "server_detail/$id"
}

// ─────────────────────────────────────────
// MAIN NAV GRAPH WITH SCAFFOLD
// ─────────────────────────────────────────

@Composable
fun SysMonitorNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.LOGIN
) {
    // Track current route to show/hide bottom bar
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val showBottomBar = currentRoute in bottomBarRoutes

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(navController = navController)
            }
        }
    ) { innerPadding ->

        NavHost(
            navController    = navController,
            startDestination = startDestination,
            modifier         = Modifier.padding(innerPadding),

            enterTransition = {
                fadeIn(animationSpec = tween(350)) +
                        slideInHorizontally(
                            initialOffsetX = { it / 5 },
                            animationSpec  = tween(350, easing = EaseOutCubic)
                        )
            },
            exitTransition = {
                fadeOut(animationSpec = tween(250)) +
                        slideOutHorizontally(
                            targetOffsetX = { -it / 5 },
                            animationSpec = tween(250, easing = EaseInCubic)
                        )
            },
            popEnterTransition = {
                fadeIn(animationSpec = tween(350)) +
                        slideInHorizontally(
                            initialOffsetX = { -it / 5 },
                            animationSpec  = tween(350, easing = EaseOutCubic)
                        )
            },
            popExitTransition = {
                fadeOut(animationSpec = tween(250)) +
                        slideOutHorizontally(
                            targetOffsetX = { it / 5 },
                            animationSpec = tween(250, easing = EaseInCubic)
                        )
            }
        ) {
            // ── LOGIN ──────────────────────────────
            composable(Routes.LOGIN) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Routes.DASHBOARD) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate(Routes.REGISTER)
                    }
                )
            }

            // ── REGISTER ───────────────────────────
            composable(Routes.REGISTER) {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate(Routes.DASHBOARD) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }

            // ── DASHBOARD ──────────────────────────
            composable(Routes.DASHBOARD) {
                DashboardScreen()
            }

            // ── SERVER LIST ────────────────────────
            composable(Routes.SERVER_LIST) {
                ServerListScreen(
                    onNavigateToDetail = { id ->
                        navController.navigate(Routes.serverDetail(id))
                    },
                    onNavigateToCreate = {
                        navController.navigate(Routes.SERVER_CREATE)
                    }
                )
            }

            // ── SERVER CREATE ──────────────────────
            composable(Routes.SERVER_CREATE) {
                ServerCreateScreen(
                    onNavigateBack   = { navController.popBackStack() },
                    onCreatedSuccess = { navController.popBackStack() }
                )
            }

            // ── SERVER DETAIL ──────────────────────
            composable(Routes.SERVER_DETAIL) { backStackEntry ->
                val serverId = backStackEntry.arguments
                    ?.getString("serverId") ?: ""
                ServerDetailScreen(
                    serverId       = serverId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}