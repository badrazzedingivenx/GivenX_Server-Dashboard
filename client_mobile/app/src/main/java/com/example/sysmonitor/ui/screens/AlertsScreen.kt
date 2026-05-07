package com.example.sysmonitor.ui.screens

import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.NotificationsOff
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import com.example.sysmonitor.data.model.AlertModel   // ✅ from ApiModels.kt
import com.example.sysmonitor.data.model.AlertType    // ✅ from ApiModels.kt
import kotlinx.coroutines.delay

// ─────────────────────────────────────────
// MOCK DATA (replace with ViewModel + API later)
// ─────────────────────────────────────────

private val fakePage1 = listOf(
    AlertModel("1",  "CPU critique",          "L'utilisation du CPU a dépassé 95%.", "critical", "Il y a 2 min"),
    AlertModel("2",  "Surcharge mémoire",     "La RAM est utilisée à 89%.",          "critical", "Il y a 5 min"),
    AlertModel("3",  "Serveur hors ligne",    "Le serveur de backup ne répond plus.","critical", "Il y a 8 min", true),
    AlertModel("4",  "Disque presque plein",  "Le disque /dev/sda1 est rempli à 87%.","warning", "Il y a 15 min"),
    AlertModel("5",  "Latence réseau élevée", "La latence dépasse 200ms sur eth0.",  "warning",  "Il y a 22 min"),
    AlertModel("6",  "Certificat SSL",        "Le certificat SSL expire dans 14j.",  "warning",  "Il y a 1h"),
    AlertModel("7",  "Sauvegarde complétée",  "La sauvegarde quotidienne réussie.",  "info",     "Il y a 2h", true),
    AlertModel("8",  "Mise à jour dispo",     "Une nouvelle version est disponible.","info",     "Il y a 3h"),
)

private val fakePage2 = listOf(
    AlertModel("9",  "Tentative connexion",   "5 tentatives de connexion échouées.", "critical", "Il y a 4h"),
    AlertModel("10", "Température élevée",    "La température dépasse 75°C.",        "warning",  "Il y a 5h"),
    AlertModel("11", "Service redémarré",     "nginx a été redémarré automatiquement.","info",   "Il y a 6h", true),
    AlertModel("12", "Bande passante saturée","Utilisation réseau à 95%.",           "critical", "Il y a 7h"),
)

enum class AlertFilter(val label: String) {
    ALL("Toutes"), CRITICAL("Critique"), WARNING("Attention"), INFO("Info")
}

// ─────────────────────────────────────────
// SCREEN
// ─────────────────────────────────────────

@Composable
fun AlertsScreen() {
    var selectedFilter by remember { mutableStateOf(AlertFilter.ALL) }
    var alerts         by remember { mutableStateOf(fakePage1) }
    var isLoadingMore  by remember { mutableStateOf(false) }
    var hasMorePages   by remember { mutableStateOf(true) }
    val listState      = rememberLazyListState()

    val filteredAlerts = remember(alerts, selectedFilter) {
        when (selectedFilter) {
            AlertFilter.ALL      -> alerts
            AlertFilter.CRITICAL -> alerts.filter { it.type == AlertType.CRITICAL }
            AlertFilter.WARNING  -> alerts.filter { it.type == AlertType.WARNING }
            AlertFilter.INFO     -> alerts.filter { it.type == AlertType.INFO }
        }
    }

    val shouldLoadMore by remember {
        derivedStateOf {
            val last  = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            val total = listState.layoutInfo.totalItemsCount
            last != null && last.index >= total - 3
        }
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !isLoadingMore && hasMorePages) {
            isLoadingMore = true
            delay(1500)
            alerts       = alerts + fakePage2
            hasMorePages = false
            isLoadingMore = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0A0E1A), Color(0xFF0D1B2A), Color(0xFF0A1628))
                )
            )
    ) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(
                brush  = Brush.radialGradient(listOf(Color(0x1AFF4D6D), Color.Transparent),
                    Offset(size.width * 0.85f, size.height * 0.1f), size.width * 0.5f),
                center = Offset(size.width * 0.85f, size.height * 0.1f),
                radius = size.width * 0.5f
            )
        }

        LazyColumn(
            state               = listState,
            modifier            = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { Spacer(Modifier.height(16.dp)) }

            // Header
            item {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Column {
                        Text(
                            text  = "Alertes",
                            style = TextStyle(
                                fontSize      = 26.sp,
                                fontWeight    = FontWeight.Bold,
                                color         = Color.White,
                                letterSpacing = (-0.5).sp
                            )
                        )
                        Text(
                            text  = "Surveillance du système",
                            style = TextStyle(fontSize = 13.sp, color = Color(0xFF7A8BA0))
                        )
                    }
                    Box(
                        modifier = Modifier.size(44.dp).clip(RoundedCornerShape(12.dp))
                            .background(Color(0x1AFF4D6D))
                            .border(1.dp, Color(0x22FF4D6D), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.NotificationsActive, null, tint = Color(0xFFFF7A93), modifier = Modifier.size(22.dp))
                    }
                }
            }

            // Summary cards
            item {
                Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(10.dp)) {
                    AlertSummaryCard("Total",     alerts.size.toString(),                   Icons.Outlined.Info,      Color(0xFF00C2FF), Modifier.weight(1f))
                    AlertSummaryCard("Critiques", alerts.count { it.type == AlertType.CRITICAL }.toString(), Icons.Outlined.Warning, Color(0xFFFF4D6D), Modifier.weight(1f))
                    AlertSummaryCard("Résolues",  alerts.count { it.isResolved }.toString(), Icons.Outlined.CheckCircle, Color(0xFF00D4AA), Modifier.weight(1f))
                }
            }

            // Filter chips
            item {
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), Arrangement.spacedBy(8.dp)) {
                    AlertFilter.values().forEach { filter ->
                        val isSelected = selectedFilter == filter
                        val color = when (filter) {
                            AlertFilter.ALL      -> Color(0xFF00C2FF)
                            AlertFilter.CRITICAL -> Color(0xFFFF4D6D)
                            AlertFilter.WARNING  -> Color(0xFFFFA500)
                            AlertFilter.INFO     -> Color(0xFF00D4AA)
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) color.copy(0.2f) else Color(0x0DFFFFFF))
                                .border(1.dp, if (isSelected) color.copy(0.5f) else Color(0x1AFFFFFF), RoundedCornerShape(20.dp))
                                .clickable { selectedFilter = filter }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text  = filter.label,
                                style = TextStyle(
                                    fontSize   = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                    color      = if (isSelected) color else Color(0xFF7A8BA0)
                                )
                            )
                        }
                    }
                }
            }

            // Count
            item {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text(
                        text  = "Liste des alertes",
                        style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF9AAFC2))
                    )
                    Text(
                        text  = "${filteredAlerts.size} résultat(s)",
                        style = TextStyle(fontSize = 12.sp, color = Color(0xFF4A5C6A))
                    )
                }
            }

            // Empty
            if (filteredAlerts.isEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(top = 40.dp), Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Icon(Icons.Outlined.NotificationsOff, null, tint = Color(0xFF2A3D52), modifier = Modifier.size(56.dp))
                            Text("Aucune alerte trouvée", style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color(0xFF7A8BA0)))
                        }
                    }
                }
            }

            // Items
            items(filteredAlerts, key = { it.id }) { alert ->
                AlertItemCard(alert = alert)
            }

            // Loading more
            if (isLoadingMore) {
                item {
                    Box(Modifier.fillMaxWidth().padding(vertical = 16.dp), Alignment.Center) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            CircularProgressIndicator(Modifier.size(18.dp), color = Color(0xFF00C2FF), strokeWidth = 2.dp)
                            Text("Chargement…", style = TextStyle(fontSize = 13.sp, color = Color(0xFF7A8BA0)))
                        }
                    }
                }
            }

            if (!hasMorePages && !isLoadingMore && filteredAlerts.isNotEmpty()) {
                item {
                    Box(Modifier.fillMaxWidth().padding(vertical = 12.dp), Alignment.Center) {
                        Text("— Fin des alertes —", style = TextStyle(fontSize = 11.sp, color = Color(0xFF2A3D52)))
                    }
                }
            }

            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

// ─────────────────────────────────────────
// ALERT SUMMARY CARD
// ─────────────────────────────────────────

@Composable
private fun AlertSummaryCard(
    label: String, value: String, icon: ImageVector,
    color: Color, modifier: Modifier
) {
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(if (visible) 1f else 0f, tween(600, easing = EaseOutCubic), label = "a")
    LaunchedEffect(Unit) { visible = true }
    Box(
        modifier.clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(listOf(Color(0x1AFFFFFF), Color(0x0AFFFFFF))))
            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(16.dp))
            .graphicsLayer { this.alpha = alpha }
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(Modifier.size(34.dp).clip(RoundedCornerShape(10.dp)).background(color.copy(0.15f)), Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(18.dp))
            }
            Text(value, style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White))
            Text(label, style = TextStyle(fontSize = 11.sp, color = Color(0xFF7A8BA0)))
        }
    }
}

// ─────────────────────────────────────────
// ALERT ITEM CARD
// ─────────────────────────────────────────

@Composable
private fun AlertItemCard(alert: AlertModel) {
    val (accentColor, bgColor, typeLabel, typeIcon) = when (alert.type) {
        AlertType.CRITICAL -> Quad(Color(0xFFFF4D6D), Color(0x1AFF4D6D), "Critique", Icons.Outlined.Error)
        AlertType.WARNING  -> Quad(Color(0xFFFFA500), Color(0x1AFFA500), "Attention", Icons.Outlined.Warning)
        AlertType.INFO     -> Quad(Color(0xFF00C2FF), Color(0x1A00C2FF), "Info", Icons.Outlined.Info)
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(listOf(Color(0x1AFFFFFF), Color(0x0AFFFFFF))))
            .border(1.dp, if (alert.isResolved) Color(0x0FFFFFFF) else accentColor.copy(0.2f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(14.dp), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape)
                    .background(if (alert.isResolved) Color(0x0DFFFFFF) else bgColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (alert.isResolved) Icons.Outlined.CheckCircle else typeIcon,
                    contentDescription = null,
                    tint = if (alert.isResolved) Color(0xFF00D4AA) else accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text(
                        text     = alert.title,
                        style    = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
                            color = if (alert.isResolved) Color(0xFF7A8BA0) else Color.White),
                        maxLines = 1, overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    Box(
                        modifier = Modifier.clip(RoundedCornerShape(6.dp))
                            .background(if (alert.isResolved) Color(0x0D00D4AA) else bgColor)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text  = if (alert.isResolved) "Résolu" else typeLabel,
                            style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Medium,
                                color = if (alert.isResolved) Color(0xFF00D4AA) else accentColor)
                        )
                    }
                }
                Text(
                    text     = alert.description,
                    style    = TextStyle(fontSize = 12.sp, color = Color(0xFF7A8BA0)),
                    maxLines = 2, overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Outlined.AccessTime, null, tint = Color(0xFF3A4D5C), modifier = Modifier.size(11.dp))
                    Text(alert.date, style = TextStyle(fontSize = 11.sp, color = Color(0xFF3A4D5C)))
                }
            }
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)