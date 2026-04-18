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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    object Dashboard : BottomNavItem(Routes.DASHBOARD,    "Dashboard", Icons.Filled.Dashboard,     Icons.Outlined.Dashboard)
    object Servers   : BottomNavItem(Routes.SERVER_LIST,  "Serveurs",  Icons.Filled.Dns,           Icons.Outlined.Dns)
    object Projects  : BottomNavItem(Routes.PROJECT_LIST, "Projets",   Icons.Filled.FolderOpen,    Icons.Outlined.FolderOpen)
    object Alerts    : BottomNavItem(Routes.ALERTS,       "Alertes",   Icons.Filled.Notifications, Icons.Outlined.Notifications)
}

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
// COULEURS DU THÈME
// ─────────────────────────────────────────
// App background gradient : 0xFF0A0E1A → 0xFF0D1B2A → 0xFF0A1628
// BottomBar utilise 0xFF0D1B2A (mid navy) — couleur pure, aucun overlay

private val NAV_BG   = Color(0xFF0D1B2A)   // fond wrapper = mid navy du thème
private val PILL_BG  = Color(0xFF1A2C3D)   // pill légèrement plus claire = visible mais navy

@Composable
fun BottomNavBar(navController: NavController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // ✅ Un seul background — couleur plate navy, aucun gradient, aucun overlay
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(NAV_BG)
            .navigationBarsPadding()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        // ✅ Pill — couleur plate légèrement plus claire, aucun double background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(PILL_BG)
                .padding(horizontal = 6.dp, vertical = 6.dp)
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
        targetValue   = if (isSelected) accentColor else Color(0xFF4A5C6A),
        animationSpec = tween(250),
        label         = "iconColor"
    )
    val labelColor by animateColorAsState(
        targetValue   = if (isSelected) accentColor else Color(0xFF4A5C6A),
        animationSpec = tween(250),
        label         = "labelColor"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isSelected) accentColor.copy(alpha = 0.12f) else Color.Transparent
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp),
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