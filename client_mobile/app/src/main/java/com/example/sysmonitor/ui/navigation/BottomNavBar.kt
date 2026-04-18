package com.example.sysmonitor.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

// ─────────────────────────────────────────
// NAV ITEM MODEL
// ─────────────────────────────────────────

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Dashboard : BottomNavItem(
        route          = Routes.DASHBOARD,
        label          = "Dashboard",
        selectedIcon   = Icons.Filled.Dashboard,
        unselectedIcon = Icons.Outlined.Dashboard
    )
    object Servers : BottomNavItem(
        route          = Routes.SERVER_LIST,
        label          = "Serveurs",
        selectedIcon   = Icons.Filled.Dns,
        unselectedIcon = Icons.Outlined.Dns
    )
    object Projects : BottomNavItem(
        route          = Routes.PROJECT_LIST,
        label          = "Projets",
        selectedIcon   = Icons.Filled.FolderOpen,
        unselectedIcon = Icons.Outlined.FolderOpen
    )
    object Alerts : BottomNavItem(
        route          = Routes.ALERTS,
        label          = "Alertes",
        selectedIcon   = Icons.Filled.Notifications,
        unselectedIcon = Icons.Outlined.Notifications
    )
}

// ─────────────────────────────────────────
// ITEMS + VISIBLE ROUTES
// ─────────────────────────────────────────

val bottomNavItems = listOf(
    BottomNavItem.Dashboard,
    BottomNavItem.Servers,
    BottomNavItem.Projects,
    BottomNavItem.Alerts
)

val bottomBarRoutes = setOf(
    Routes.DASHBOARD,
    Routes.SERVER_LIST,
    Routes.PROJECT_LIST,
    Routes.ALERTS
)

// ─────────────────────────────────────────
// BOTTOM NAV BAR
// ─────────────────────────────────────────

@Composable
fun BottomNavBar(navController: NavController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // ✅ FIX — outer wrapper uses the same dark navy as the app background
    // No more pure black — blends naturally with the gradient background
    Box(
        modifier = Modifier
            .fillMaxWidth()
            // ✅ FIX — same color as app background: 0xFF0A0E1A (dark navy)
            // gradient from transparent at top → solid at bottom
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x000D1B2A),   // fully transparent top
                        Color(0xF00A0E1A)    // near-solid navy bottom — matches app BG
                    )
                )
            )
            // ✅ FIX — push above system navigation bar
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        // ✅ FIX — pill container uses navy glass effect, NOT black
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                // Base navy layer
                .background(Color(0xEE0D1B2A))
                // Glass shimmer on top
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0x22FFFFFF),  // subtle white top-left
                            Color(0x08FFFFFF)   // fade to transparent
                        )
                    )
                )
                .padding(horizontal = 6.dp, vertical = 8.dp)
        ) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                bottomNavItems.forEach { item ->
                    val isSelected = currentRoute == item.route
                    BottomNavItemUI(
                        item       = item,
                        isSelected = isSelected,
                        onClick    = {
                            if (!isSelected) {
                                navController.navigate(item.route) {
                                    popUpTo(Routes.DASHBOARD) { saveState = true }
                                    launchSingleTop = true
                                    restoreState    = true
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────
// SINGLE ITEM
// ─────────────────────────────────────────

@Composable
private fun BottomNavItemUI(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val accentColor = when (item) {
        is BottomNavItem.Alerts -> Color(0xFFFF7A93)
        else                    -> Color(0xFF00C2FF)
    }

    val iconColor by animateColorAsState(
        targetValue   = if (isSelected) accentColor else Color(0xFF4A6580),
        animationSpec = tween(250),
        label         = "iconColor"
    )
    val labelColor by animateColorAsState(
        targetValue   = if (isSelected) accentColor else Color(0xFF4A6580),
        animationSpec = tween(250),
        label         = "labelColor"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isSelected) accentColor.copy(alpha = 0.13f) else Color.Transparent
            )
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Icon(
                imageVector        = if (isSelected) item.selectedIcon else item.unselectedIcon,
                contentDescription = item.label,
                tint               = iconColor,
                modifier           = Modifier.size(22.dp)
            )
            Text(
                text  = item.label,
                style = TextStyle(
                    fontSize   = 9.sp,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color      = labelColor
                )
            )
        }
    }
}