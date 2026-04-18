package com.example.sysmonitor.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
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
// TOP NAV ITEM MODEL
// ─────────────────────────────────────────

data class TopNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector
)

val topNavItems = listOf(
    TopNavItem(Routes.DASHBOARD,   "Dashboard", Icons.Outlined.Dashboard),
    TopNavItem(Routes.SERVER_LIST, "Serveurs",  Icons.Outlined.Dns),
    TopNavItem(Routes.PROJECT_LIST,"Projets",   Icons.Outlined.FolderOpen),
    TopNavItem(Routes.ALERTS,      "Alertes",   Icons.Outlined.Notifications),
    TopNavItem(Routes.LOGS,        "Logs",      Icons.Outlined.List)
)

// Screens where TopBar is visible
val topBarRoutes = setOf(
    Routes.DASHBOARD,
    Routes.SERVER_LIST,
    Routes.PROJECT_LIST,
    Routes.ALERTS,
    Routes.LOGS
)

// ─────────────────────────────────────────
// TOP NAV BAR
// ─────────────────────────────────────────

@Composable
fun TopNavBar(navController: NavController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0A0E1A), Color(0xCC0A0E1A))
                )
            )
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xCC0D1B2A))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0x1AFFFFFF), Color(0x0AFFFFFF))
                    )
                )
                .padding(horizontal = 6.dp, vertical = 6.dp)
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            topNavItems.forEach { item ->
                val isSelected = currentRoute == item.route
                TopNavItemUI(
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

// ─────────────────────────────────────────
// SINGLE ITEM
// ─────────────────────────────────────────

@Composable
private fun TopNavItemUI(
    item: TopNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val accentColor = when (item.route) {
        Routes.ALERTS -> Color(0xFFFF7A93)
        Routes.LOGS   -> Color(0xFFAB7EFF)
        else          -> Color(0xFF00C2FF)
    }

    val bgColor by animateColorAsState(
        targetValue   = if (isSelected) accentColor.copy(alpha = 0.15f)
        else Color.Transparent,
        animationSpec = tween(200),
        label         = "topNavBg"
    )
    val contentColor by animateColorAsState(
        targetValue   = if (isSelected) accentColor else Color(0xFF4A5C6A),
        animationSpec = tween(200),
        label         = "topNavContent"
    )

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector        = item.icon,
            contentDescription = item.label,
            tint               = contentColor,
            modifier           = Modifier.size(16.dp)
        )
        Text(
            text  = item.label,
            style = TextStyle(
                fontSize   = 13.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold
                else FontWeight.Normal,
                color      = contentColor
            )
        )
    }
}