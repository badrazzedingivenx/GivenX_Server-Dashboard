package com.example.sysmonitor.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.HorizontalDivider
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

// ─────────────────────────────────────────
// DRAWER ITEM MODEL
// ─────────────────────────────────────────

data class DrawerItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val accentColor: Color = Color(0xFF00C2FF)
)

// ── Main navigation items
private val mainDrawerItems = listOf(
    DrawerItem(Routes.DASHBOARD,    "Tableau de bord", Icons.Outlined.Dashboard,    Color(0xFF00C2FF)),
    DrawerItem(Routes.SERVER_LIST,  "Serveurs",         Icons.Outlined.Dns,          Color(0xFF00D4AA)),
    DrawerItem(Routes.PROJECT_LIST, "Projets",          Icons.Outlined.FolderOpen,   Color(0xFF4D9FFF)),
    DrawerItem(Routes.ALERTS,       "Alertes",          Icons.Outlined.Notifications,Color(0xFFFF7A93)),
    DrawerItem(Routes.LOGS,         "Logs système",     Icons.Outlined.List,         Color(0xFFAB7EFF)),
)

// ── Admin items
private val adminDrawerItems = listOf(
    DrawerItem(Routes.ADMIN_DASHBOARD, "Administration", Icons.Outlined.AdminPanelSettings, Color(0xFFFFA500)),
    DrawerItem(Routes.USERS,           "Utilisateurs",   Icons.Outlined.People,             Color(0xFF00C2FF)),
)

// ─────────────────────────────────────────
// DRAWER CONTENT
// ─────────────────────────────────────────

@Composable
fun AppDrawerContent(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onClose: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0A0E1A), Color(0xFF0D1B2A))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // ── Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF00C2FF), Color(0xFF6E40FF))
                        )
                    )
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0x33FFFFFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text  = "SM",
                            style = TextStyle(
                                fontSize   = 18.sp,
                                fontWeight = FontWeight.Black,
                                color      = Color.White
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text  = "SysMonitor",
                        style = TextStyle(
                            fontSize   = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color.White
                        )
                    )
                    Text(
                        text  = "Tableau de surveillance",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color    = Color(0xBBFFFFFF)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Main navigation
            DrawerSection(
                title = "NAVIGATION",
                items = mainDrawerItems,
                currentRoute = currentRoute,
                onNavigate = onNavigate,
                onClose = onClose
            )

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(
                modifier  = Modifier.padding(horizontal = 20.dp),
                color     = Color(0x1AFFFFFF),
                thickness = 1.dp
            )
            Spacer(modifier = Modifier.height(8.dp))

            // ── Admin section
            DrawerSection(
                title = "ADMINISTRATION",
                items = adminDrawerItems,
                currentRoute = currentRoute,
                onNavigate = onNavigate,
                onClose = onClose
            )

            Spacer(modifier = Modifier.height(24.dp))

            // ── Footer
            HorizontalDivider(color = Color(0x0FFFFFFF), thickness = 1.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    imageVector        = Icons.Outlined.Info,
                    contentDescription = null,
                    tint               = Color(0xFF3A4D5C),
                    modifier           = Modifier.size(16.dp)
                )
                Text(
                    text  = "SysMonitor v1.0",
                    style = TextStyle(
                        fontSize = 11.sp,
                        color    = Color(0xFF3A4D5C)
                    )
                )
            }
        }
    }
}

// ─────────────────────────────────────────
// DRAWER SECTION
// ─────────────────────────────────────────

@Composable
private fun DrawerSection(
    title: String,
    items: List<DrawerItem>,
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier            = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text  = title,
            style = TextStyle(
                fontSize      = 10.sp,
                fontWeight    = FontWeight.SemiBold,
                color         = Color(0xFF3A4D5C),
                letterSpacing = 1.5.sp
            ),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
        )
        items.forEach { item ->
            DrawerItemRow(
                item       = item,
                isSelected = currentRoute == item.route,
                onClick    = {
                    onNavigate(item.route)
                    onClose()
                }
            )
        }
    }
}

// ─────────────────────────────────────────
// DRAWER ITEM ROW
// ─────────────────────────────────────────

@Composable
private fun DrawerItemRow(
    item: DrawerItem,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val bgColor by animateColorAsState(
        targetValue   = if (isSelected) item.accentColor.copy(alpha = 0.12f) else Color.Transparent,
        animationSpec = tween(200),
        label         = "drawerBg"
    )
    val contentColor by animateColorAsState(
        targetValue   = if (isSelected) item.accentColor else Color(0xFF7A8BA0),
        animationSpec = tween(200),
        label         = "drawerContent"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Box(
            modifier = Modifier
                .width(3.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (isSelected) item.accentColor else Color.Transparent)
        )
        Icon(
            imageVector        = item.icon,
            contentDescription = item.label,
            tint               = contentColor,
            modifier           = Modifier.size(20.dp)
        )
        Text(
            text  = item.label,
            style = TextStyle(
                fontSize   = 14.sp,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                color      = contentColor
            )
        )
    }
}