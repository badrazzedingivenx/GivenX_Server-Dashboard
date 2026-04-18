package com.example.sysmonitor.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

fun routeToTitle(route: String?): String = when (route) {
    Routes.DASHBOARD       -> "Tableau de bord"
    Routes.SERVER_LIST     -> "Serveurs"
    Routes.PROJECT_LIST    -> "Projets"
    Routes.ALERTS          -> "Alertes"
    Routes.LOGS            -> "Logs système"
    Routes.ADMIN_DASHBOARD -> "Administration"
    Routes.USERS           -> "Utilisateurs"
    else                   -> "SysMonitor"
}

@Composable
fun AppTopBar(
    currentRoute: String?,
    onMenuClick: () -> Unit,
    onLogout: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            // ✅ FIX — navy background matching the app, not black
            .background(Color(0xFF0A0E1A))
            // ✅ FIX — statusBarsPadding pushes content below status bar icons
            // This is correct here because contentWindowInsets = 0 in Scaffold
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // ── Menu icon
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector        = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint               = Color(0xFFCDD9E5),
                    modifier           = Modifier.size(24.dp)
                )
            }

            // ── Dynamic title
            Text(
                text  = routeToTitle(currentRoute),
                style = TextStyle(
                    fontSize      = 17.sp,
                    fontWeight    = FontWeight.SemiBold,
                    color         = Color.White,
                    letterSpacing = 0.2.sp
                )
            )

            // ── Logout button
            Box(
                modifier = Modifier
                    .padding(end = 4.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0x1AFF4D6D))
            ) {
                IconButton(
                    onClick  = onLogout,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector        = Icons.Default.Logout,
                        contentDescription = "Déconnexion",
                        tint               = Color(0xFFFF7A93),
                        modifier           = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Bottom separator line
        HorizontalDivider(
            modifier  = Modifier.align(Alignment.BottomCenter),
            color     = Color(0x22FFFFFF),
            thickness = 0.5.dp
        )
    }
}