package com.example.sysmonitor.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Dns
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
        route         = Routes.DASHBOARD,
        label         = "Tableau de bord",
        selectedIcon   = Icons.Filled.Dashboard,
        unselectedIcon = Icons.Outlined.Dashboard
    )

    object Servers : BottomNavItem(
        route         = Routes.SERVER_LIST,
        label         = "Serveurs",
        selectedIcon   = Icons.Filled.Dns,
        unselectedIcon = Icons.Outlined.Dns
    )
}

// ─────────────────────────────────────────
// BOTTOM NAV BAR
// ─────────────────────────────────────────

val bottomNavItems = listOf(
    BottomNavItem.Dashboard,
    BottomNavItem.Servers
)

// Screens where BottomBar should be VISIBLE
val bottomBarRoutes = setOf(
    Routes.DASHBOARD,
    Routes.SERVER_LIST
)

@Composable
fun BottomNavBar(navController: NavController) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x000A0E1A),
                        Color(0xFF0A0E1A)
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Glass card background
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xCC0D1B2A))
                .then(
                    Modifier.background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0x1AFFFFFF),
                                Color(0x0AFFFFFF)
                            )
                        )
                    )
                )
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                bottomNavItems.forEach { item ->
                    val isSelected = currentRoute == item.route

                    BottomNavItem(
                        item       = item,
                        isSelected = isSelected,
                        onClick    = {
                            if (!isSelected) {
                                navController.navigate(item.route) {
                                    // Pop to dashboard to avoid deep stack
                                    popUpTo(Routes.DASHBOARD) {
                                        saveState = true
                                    }
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
// SINGLE NAV ITEM
// ─────────────────────────────────────────

@Composable
private fun BottomNavItem(
    item: BottomNavItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val iconColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF00C2FF) else Color(0xFF4A5C6A),
        animationSpec = tween(durationMillis = 250),
        label = "iconColor"
    )

    val labelColor by animateColorAsState(
        targetValue = if (isSelected) Color(0xFF00C2FF) else Color(0xFF4A5C6A),
        animationSpec = tween(durationMillis = 250),
        label = "labelColor"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .background(
                if (isSelected) Color(0x1A00C2FF) else Color.Transparent
            )
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = if (isSelected)
                    item.selectedIcon else item.unselectedIcon,
                contentDescription = item.label,
                tint = iconColor,
                modifier = Modifier.size(22.dp)
            )
            Text(
                text = item.label,
                style = TextStyle(
                    fontSize = 10.sp,
                    fontWeight = if (isSelected)
                        FontWeight.SemiBold else FontWeight.Normal,
                    color = labelColor
                )
            )
        }
    }
}