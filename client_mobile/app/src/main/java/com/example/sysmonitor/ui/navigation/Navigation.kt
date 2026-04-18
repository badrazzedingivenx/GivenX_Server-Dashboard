package com.example.sysmonitor.ui.navigation

import androidx.compose.animation.core.EaseInCubic
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.sysmonitor.data.repository.TokenManager
import com.example.sysmonitor.ui.screens.AdminDashboardScreen
import com.example.sysmonitor.ui.screens.AlertsScreen
import com.example.sysmonitor.ui.screens.CreateProjectScreen
import com.example.sysmonitor.ui.screens.DashboardScreen
import com.example.sysmonitor.ui.screens.LoginScreen
import com.example.sysmonitor.ui.screens.LogsScreen
import com.example.sysmonitor.ui.screens.ProjectsListScreen
import com.example.sysmonitor.ui.screens.RegisterScreen
import com.example.sysmonitor.ui.screens.ServerCreateScreen
import com.example.sysmonitor.ui.screens.ServerDetailScreen
import com.example.sysmonitor.ui.screens.ServerListScreen
import com.example.sysmonitor.ui.screens.UsersScreen
import kotlinx.coroutines.launch

// ─────────────────────────────────────────
// ROUTES
// ─────────────────────────────────────────

object Routes {
    const val LOGIN            = "login"
    const val REGISTER         = "register"
    const val DASHBOARD        = "dashboard"
    const val SERVER_LIST      = "server_list"
    const val SERVER_CREATE    = "server_create"
    const val SERVER_DETAIL    = "server_detail/{serverId}"
    const val PROJECT_LIST     = "project_list"
    const val PROJECT_CREATE   = "project_create"
    const val ALERTS           = "alerts"
    const val LOGS             = "logs"
    const val ADMIN_DASHBOARD  = "admin_dashboard"
    const val USERS            = "users"

    fun serverDetail(id: String) = "server_detail/$id"
}

private val mainRoutes = setOf(
    Routes.DASHBOARD,
    Routes.SERVER_LIST,
    Routes.PROJECT_LIST,
    Routes.ALERTS,
    Routes.LOGS,
    Routes.ADMIN_DASHBOARD,
    Routes.USERS
)

// ─────────────────────────────────────────
// NAV GRAPH
// ─────────────────────────────────────────

@Composable
fun SysMonitorNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.LOGIN
) {
    val context        = LocalContext.current
    val tokenManager   = TokenManager(context)
    val drawerState    = rememberDrawerState(DrawerValue.Closed)
    val scope          = rememberCoroutineScope()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute   = backStackEntry?.destination?.route
    val isMainScreen   = currentRoute in mainRoutes
    val showBottomBar  = currentRoute in bottomBarRoutes

    fun handleLogout() {
        tokenManager.clearToken()
        navController.navigate(Routes.LOGIN) {
            popUpTo(0) { inclusive = true }
        }
    }

    ModalNavigationDrawer(
        drawerState     = drawerState,
        gesturesEnabled = isMainScreen,
        scrimColor      = Color(0xAA000000),
        drawerContent   = {
            AppDrawerContent(
                currentRoute = currentRoute,
                onNavigate   = { route ->
                    scope.launch { drawerState.close() }
                    if (currentRoute != route) {
                        navController.navigate(route) {
                            popUpTo(Routes.DASHBOARD) { saveState = true }
                            launchSingleTop = true
                            restoreState    = true
                        }
                    }
                },
                onClose = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            modifier       = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            // ✅ FIX 1 — Zero WindowInsets so Scaffold does NOT add any padding
            // AppTopBar handles statusBarsPadding itself inside
            // BottomNavBar handles navigationBarsPadding itself inside
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            topBar = {
                if (isMainScreen) {
                    AppTopBar(
                        currentRoute = currentRoute,
                        onMenuClick  = { scope.launch { drawerState.open() } },
                        onLogout     = { handleLogout() }
                    )
                }
            },
            bottomBar = {
                if (showBottomBar) {
                    BottomNavBar(navController = navController)
                }
            }
        ) { innerPadding ->
            NavHost(
                navController    = navController,
                startDestination = startDestination,
                modifier         = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),

                enterTransition = {
                    fadeIn(tween(300)) + slideInHorizontally(
                        initialOffsetX = { it / 6 },
                        animationSpec  = tween(300, easing = EaseOutCubic)
                    )
                },
                exitTransition = {
                    fadeOut(tween(200)) + slideOutHorizontally(
                        targetOffsetX = { -it / 6 },
                        animationSpec = tween(200, easing = EaseInCubic)
                    )
                },
                popEnterTransition = {
                    fadeIn(tween(300)) + slideInHorizontally(
                        initialOffsetX = { -it / 6 },
                        animationSpec  = tween(300, easing = EaseOutCubic)
                    )
                },
                popExitTransition = {
                    fadeOut(tween(200)) + slideOutHorizontally(
                        targetOffsetX = { it / 6 },
                        animationSpec = tween(200, easing = EaseInCubic)
                    )
                }
            ) {
                composable(Routes.LOGIN) {
                    LoginScreen(
                        onLoginSuccess       = {
                            navController.navigate(Routes.DASHBOARD) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        },
                        onNavigateToRegister = { navController.navigate(Routes.REGISTER) }
                    )
                }
                composable(Routes.REGISTER) {
                    RegisterScreen(
                        onRegisterSuccess = {
                            navController.navigate(Routes.DASHBOARD) {
                                popUpTo(Routes.LOGIN) { inclusive = true }
                            }
                        },
                        onNavigateToLogin = { navController.popBackStack() }
                    )
                }

                composable(Routes.DASHBOARD)       { DashboardScreen() }
                composable(Routes.ALERTS)          { AlertsScreen() }
                composable(Routes.LOGS)            { LogsScreen() }
                composable(Routes.ADMIN_DASHBOARD) { AdminDashboardScreen() }
                composable(Routes.USERS)           { UsersScreen() }

                composable(Routes.SERVER_LIST) {
                    ServerListScreen(
                        onNavigateToDetail = { id -> navController.navigate(Routes.serverDetail(id)) },
                        onNavigateToCreate = { navController.navigate(Routes.SERVER_CREATE) }
                    )
                }
                composable(Routes.SERVER_CREATE) {
                    ServerCreateScreen(
                        onNavigateBack   = { navController.popBackStack() },
                        onCreatedSuccess = { navController.popBackStack() }
                    )
                }
                composable(Routes.SERVER_DETAIL) { entry ->
                    val serverId = entry.arguments?.getString("serverId") ?: ""
                    ServerDetailScreen(
                        serverId       = serverId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
                composable(Routes.PROJECT_LIST) {
                    ProjectsListScreen(
                        onNavigateToCreate = { navController.navigate(Routes.PROJECT_CREATE) }
                    )
                }
                composable(Routes.PROJECT_CREATE) {
                    CreateProjectScreen(
                        onNavigateBack   = { navController.popBackStack() },
                        onCreatedSuccess = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}