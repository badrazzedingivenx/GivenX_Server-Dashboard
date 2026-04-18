package com.example.sysmonitor.ui.screens

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.List
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.PersonOff
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─────────────────────────────────────────
// MOCK DATA
// ─────────────────────────────────────────

private data class AdminStat(
    val label: String,
    val value: String,
    val icon: ImageVector,
    val color: Color,
    val subtitle: String = ""
)

private val adminStats = listOf(
    AdminStat("Utilisateurs",      "24",  Icons.Outlined.People,      Color(0xFF00C2FF), "Total inscrits"),
    AdminStat("Actifs",            "18",  Icons.Outlined.CheckCircle, Color(0xFF00D4AA), "Connectés 30j"),
    AdminStat("Serveurs actifs",   "4",   Icons.Outlined.Dns,         Color(0xFF4D9FFF), "En ligne"),
    AdminStat("Projets",           "12",  Icons.Outlined.FolderOpen,  Color(0xFFAB7EFF), "En cours"),
    AdminStat("Alertes",           "7",   Icons.Outlined.Notifications,Color(0xFFFF7A93), "Non résolues"),
    AdminStat("Logs aujourd'hui",  "143", Icons.Outlined.List,        Color(0xFFFFA500), "Entrées"),
)

private data class RecentLog(
    val user: String,
    val action: String,
    val time: String,
    val color: Color
)

private val recentLogs = listOf(
    RecentLog("Saad",    "Connexion réussie",           "08:02", Color(0xFF00D4AA)),
    RecentLog("Admin",   "Suppression serveur Backup",  "08:30", Color(0xFFFF4D6D)),
    RecentLog("Marwa",   "Création projet App Mobile",  "09:15", Color(0xFF00C2FF)),
    RecentLog("Youssef", "Mise à jour permissions",     "10:05", Color(0xFFFFA500)),
    RecentLog("Admin",   "Blocage utilisateur test01",  "11:20", Color(0xFFFF4D6D)),
)

// ─────────────────────────────────────────
// SCREEN
// ─────────────────────────────────────────

@Composable
fun AdminDashboardScreen() {

    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0A0E1A), Color(0xFF0D1B2A), Color(0xFF0A1628))
                )
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush  = Brush.radialGradient(
                    listOf(Color(0x1AFFA500), Color.Transparent),
                    Offset(size.width * 0.85f, size.height * 0.1f), size.width * 0.5f
                ),
                center = Offset(size.width * 0.85f, size.height * 0.1f),
                radius = size.width * 0.5f
            )
            drawCircle(
                brush  = Brush.radialGradient(
                    listOf(Color(0x0D00C2FF), Color.Transparent),
                    Offset(size.width * 0.1f, size.height * 0.7f), size.width * 0.4f
                ),
                center = Offset(size.width * 0.1f, size.height * 0.7f),
                radius = size.width * 0.4f
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text  = "Administration",
                        style = TextStyle(
                            fontSize   = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color.White
                        )
                    )
                    Text(
                        text  = "Vue globale du système",
                        style = TextStyle(fontSize = 13.sp, color = Color(0xFF7A8BA0))
                    )
                }
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x1AFFA500))
                        .border(1.dp, Color(0x22FFA500), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.Outlined.Shield,
                        contentDescription = null,
                        tint               = Color(0xFFFFA500),
                        modifier           = Modifier.size(22.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Stats grid 2 columns
            Text(
                text  = "Statistiques globales",
                style = TextStyle(
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = Color(0xFF9AAFC2)
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            adminStats.chunked(2).forEach { row ->
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { stat ->
                        AdminStatCard(
                            stat    = stat,
                            visible = visible,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            // System summary card
            Text(
                text  = "Résumé système",
                style = TextStyle(
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = Color(0xFF9AAFC2)
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        brush = Brush.linearGradient(
                            listOf(Color(0x1AFFFFFF), Color(0x0AFFFFFF))
                        )
                    )
                    .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(18.dp))
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    SummaryRow("Uptime système",    "99.8%",  Color(0xFF00D4AA))
                    HorizontalDivider(color = Color(0x0FFFFFFF))
                    SummaryRow("Charge CPU moy.",   "42.5%",  Color(0xFFFF6B6B))
                    HorizontalDivider(color = Color(0x0FFFFFFF))
                    SummaryRow("RAM utilisée",      "68.0%",  Color(0xFF4D9FFF))
                    HorizontalDivider(color = Color(0x0FFFFFFF))
                    SummaryRow("Stockage disque",   "55.3%",  Color(0xFF00D4AA))
                    HorizontalDivider(color = Color(0x0FFFFFFF))
                    SummaryRow("Processus actifs",  "124",    Color(0xFFAB7EFF))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Recent logs
            Text(
                text  = "Activité récente",
                style = TextStyle(
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = Color(0xFF9AAFC2)
                )
            )
            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        brush = Brush.linearGradient(
                            listOf(Color(0x1AFFFFFF), Color(0x0AFFFFFF))
                        )
                    )
                    .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(18.dp))
                    .padding(20.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    recentLogs.forEachIndexed { index, log ->
                        RecentLogRow(log = log)
                        if (index < recentLogs.lastIndex) {
                            HorizontalDivider(color = Color(0x0FFFFFFF))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

// ─────────────────────────────────────────
// COMPONENTS
// ─────────────────────────────────────────

@Composable
private fun AdminStatCard(
    stat: AdminStat,
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    val alpha by animateFloatAsState(
        targetValue   = if (visible) 1f else 0f,
        animationSpec = tween(600, easing = EaseOutCubic),
        label         = "alpha"
    )

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.linearGradient(listOf(Color(0x1AFFFFFF), Color(0x0AFFFFFF)))
            )
            .border(1.dp, Color(0x14FFFFFF), RoundedCornerShape(16.dp))
            .graphicsLayer { this.alpha = alpha }
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(stat.color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = stat.icon,
                    contentDescription = null,
                    tint               = stat.color,
                    modifier           = Modifier.size(18.dp)
                )
            }
            Text(
                text  = stat.value,
                style = TextStyle(
                    fontSize   = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White
                )
            )
            Text(
                text  = stat.label,
                style = TextStyle(
                    fontSize   = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = Color(0xFFCDD9E5)
                )
            )
            Text(
                text  = stat.subtitle,
                style = TextStyle(fontSize = 10.sp, color = Color(0xFF7A8BA0))
            )
        }
    }
}

@Composable
private fun SummaryRow(label: String, value: String, color: Color) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Text(
            text  = label,
            style = TextStyle(fontSize = 13.sp, color = Color(0xFF7A8BA0))
        )
        Text(
            text  = value,
            style = TextStyle(
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color      = color
            )
        )
    }
}

@Composable
private fun RecentLogRow(log: RecentLog) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(log.color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text  = log.user.first().uppercase(),
                style = TextStyle(
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color      = log.color
                )
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text  = log.action,
                style = TextStyle(
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color      = Color(0xFFCDD9E5)
                )
            )
            Text(
                text  = log.user,
                style = TextStyle(fontSize = 11.sp, color = Color(0xFF4A5C6A))
            )
        }
        Text(
            text  = log.time,
            style = TextStyle(fontSize = 11.sp, color = Color(0xFF3A4D5C))
        )
    }
}