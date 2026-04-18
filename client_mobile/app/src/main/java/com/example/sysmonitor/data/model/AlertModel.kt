package com.example.sysmonitor.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import com.example.sysmonitor.data.model.AlertModel
import com.example.sysmonitor.data.model.AlertType
import kotlinx.coroutines.delay

// ─────────────────────────────────────────
// FAKE DATA
// ─────────────────────────────────────────

private val fakePage1 = listOf(
    AlertModel("1", "CPU critique", "L'utilisation du CPU a dépassé 95% sur le serveur principal.", AlertType.CRITICAL, "Il y a 2 min"),
    AlertModel("2", "Surcharge mémoire", "La RAM est utilisée à 89%. Risque de ralentissement.", AlertType.CRITICAL, "Il y a 5 min"),
    AlertModel("3", "Serveur hors ligne", "Le serveur de backup ne répond plus depuis 3 minutes.", AlertType.CRITICAL, "Il y a 8 min", true),
    AlertModel("4", "Disque presque plein", "Le disque /dev/sda1 est rempli à 87%.", AlertType.WARNING, "Il y a 15 min"),
    AlertModel("5", "Latence réseau élevée", "La latence moyenne dépasse 200ms sur eth0.", AlertType.WARNING, "Il y a 22 min"),
    AlertModel("6", "Certificat SSL bientôt expiré", "Le certificat expire dans 14 jours.", AlertType.WARNING, "Il y a 1h"),
    AlertModel("7", "Sauvegarde complétée", "La sauvegarde quotidienne s'est terminée avec succès.", AlertType.INFO, "Il y a 2h", true),
    AlertModel("8", "Mise à jour disponible", "Une nouvelle version du système est disponible.", AlertType.INFO, "Il y a 3h"),
)

private val fakePage2 = listOf(
    AlertModel("9", "Tentative de connexion", "5 tentatives de connexion échouées détectées.", AlertType.CRITICAL, "Il y a 4h"),
    AlertModel("10", "Temperature élevée", "La température du serveur dépasse 75°C.", AlertType.WARNING, "Il y a 5h"),
    AlertModel("11", "Service redémarré", "Le service nginx a été redémarré automatiquement.", AlertType.INFO, "Il y a 6h", true),
    AlertModel("12", "Bande passante saturée", "Utilisation réseau à 95% de la capacité maximale.", AlertType.CRITICAL, "Il y a 7h"),
    AlertModel("13", "Synchronisation réussie", "Synchronisation des données complétée.", AlertType.INFO, "Il y a 8h", true),
    AlertModel("14", "Processus zombie détecté", "3 processus zombies détectés sur le serveur.", AlertType.WARNING, "Il y a 9h"),
    AlertModel("15", "Base de données lente", "Les requêtes SQL dépassent 5s en moyenne.", AlertType.WARNING, "Il y a 10h"),
    AlertModel("16", "Firewall mis à jour", "Les règles du pare-feu ont été mises à jour.", AlertType.INFO, "Il y a 12h", true),
)

// ─────────────────────────────────────────
// FILTER ENUM
// ─────────────────────────────────────────

enum class AlertFilter(val label: String) {
    ALL("Toutes"),
    CRITICAL("Critique"),
    WARNING("Attention"),
    INFO("Info")
}

// ─────────────────────────────────────────
// ALERTS SCREEN
// ─────────────────────────────────────────

@Composable
fun AlertsScreen() {
    var selectedFilter  by remember { mutableStateOf(AlertFilter.ALL) }
    var alerts          by remember { mutableStateOf(fakePage1) }
    var isLoadingMore   by remember { mutableStateOf(false) }
    var hasMorePages    by remember { mutableStateOf(true) }
    val listState       = rememberLazyListState()

    // Filtered list
    val filteredAlerts = remember(alerts, selectedFilter) {
        when (selectedFilter) {
            AlertFilter.ALL      -> alerts
            AlertFilter.CRITICAL -> alerts.filter { it.type == AlertType.CRITICAL }
            AlertFilter.WARNING  -> alerts.filter { it.type == AlertType.WARNING }
            AlertFilter.INFO     -> alerts.filter { it.type == AlertType.INFO }
        }
    }

    // Summary counts
    val totalAlerts    = alerts.size
    val criticalAlerts = alerts.count { it.type == AlertType.CRITICAL }
    val resolvedAlerts = alerts.count { it.isResolved }

    // Pagination — detect end of list
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            val totalItems  = listState.layoutInfo.totalItemsCount
            lastVisible != null && lastVisible.index >= totalItems - 3
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value && !isLoadingMore && hasMorePages) {
            isLoadingMore = true
            delay(1500)
            alerts = alerts + fakePage2
            hasMorePages  = false
            isLoadingMore = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0A0E1A),
                        Color(0xFF0D1B2A),
                        Color(0xFF0A1628)
                    )
                )
            )
    ) {
        // Ambient orbs
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x1AFF4D6D), Color.Transparent),
                    center = Offset(size.width * 0.85f, size.height * 0.1f),
                    radius = size.width * 0.5f
                ),
                center = Offset(size.width * 0.85f, size.height * 0.1f),
                radius = size.width * 0.5f
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x0D6E40FF), Color.Transparent),
                    center = Offset(size.width * 0.1f, size.height * 0.6f),
                    radius = size.width * 0.4f
                ),
                center = Offset(size.width * 0.1f, size.height * 0.6f),
                radius = size.width * 0.4f
            )
        }

        LazyColumn(
            state   = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ── Top spacing
            item { Spacer(modifier = Modifier.height(52.dp)) }

            // ── Header
            item {
                AlertsHeader()
            }

            item { Spacer(modifier = Modifier.height(4.dp)) }

            // ── Summary cards
            item {
                AlertsSummaryRow(
                    total    = totalAlerts,
                    critical = criticalAlerts,
                    resolved = resolvedAlerts
                )
            }

            item { Spacer(modifier = Modifier.height(4.dp)) }

            // ── Filter chips
            item {
                AlertsFilterRow(
                    selected = selectedFilter,
                    onSelect = { selectedFilter = it }
                )
            }

            item { Spacer(modifier = Modifier.height(4.dp)) }

            // ── Section label
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text  = "Liste des alertes",
                        style = TextStyle(
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = Color(0xFF9AAFC2)
                        )
                    )
                    Text(
                        text  = "${filteredAlerts.size} résultat(s)",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color    = Color(0xFF4A5C6A)
                        )
                    )
                }
            }

            // ── Empty state
            if (filteredAlerts.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector        = Icons.Outlined.NotificationsOff,
                                contentDescription = null,
                                tint               = Color(0xFF2A3D52),
                                modifier           = Modifier.size(56.dp)
                            )
                            Text(
                                text  = "Aucune alerte trouvée",
                                style = TextStyle(
                                    fontSize   = 16.sp,
                                    fontWeight = FontWeight.Medium,
                                    color      = Color(0xFF7A8BA0)
                                )
                            )
                        }
                    }
                }
            }

            // ── Alert items
            items(filteredAlerts, key = { it.id }) { alert ->
                AlertCard(alert = alert)
            }

            // ── Loading more indicator
            if (isLoadingMore) {
                item {
                    Box(
                        modifier         = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment     = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier   = Modifier.size(18.dp),
                                color      = Color(0xFF00C2FF),
                                strokeWidth = 2.dp
                            )
                            Text(
                                text  = "Chargement des alertes…",
                                style = TextStyle(
                                    fontSize = 13.sp,
                                    color    = Color(0xFF7A8BA0)
                                )
                            )
                        }
                    }
                }
            }

            // ── End of list
            if (!hasMorePages && !isLoadingMore && filteredAlerts.isNotEmpty()) {
                item {
                    Box(
                        modifier         = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text  = "— Fin des alertes —",
                            style = TextStyle(
                                fontSize = 11.sp,
                                color    = Color(0xFF2A3D52)
                            )
                        )
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(100.dp)) }
        }
    }
}

// ─────────────────────────────────────────
// HEADER
// ─────────────────────────────────────────

@Composable
private fun AlertsHeader() {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
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
                style = TextStyle(
                    fontSize = 13.sp,
                    color    = Color(0xFF7A8BA0)
                )
            )
        }

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0x1AFF4D6D))
                .border(1.dp, Color(0x22FF4D6D), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Outlined.NotificationsActive,
                contentDescription = "Alertes",
                tint               = Color(0xFFFF7A93),
                modifier           = Modifier.size(22.dp)
            )
        }
    }
}

// ─────────────────────────────────────────
// SUMMARY ROW
// ─────────────────────────────────────────

@Composable
private fun AlertsSummaryRow(
    total: Int,
    critical: Int,
    resolved: Int
) {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        AlertSummaryCard(
            label    = "Total",
            value    = total.toString(),
            icon     = Icons.Outlined.Notifications,
            color    = Color(0xFF00C2FF),
            modifier = Modifier.weight(1f)
        )
        AlertSummaryCard(
            label    = "Critiques",
            value    = critical.toString(),
            icon     = Icons.Outlined.Warning,
            color    = Color(0xFFFF4D6D),
            modifier = Modifier.weight(1f)
        )
        AlertSummaryCard(
            label    = "Résolues",
            value    = resolved.toString(),
            icon     = Icons.Outlined.CheckCircle,
            color    = Color(0xFF00D4AA),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun AlertSummaryCard(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue   = if (visible) 1f else 0f,
        animationSpec = tween(600, easing = EaseOutCubic),
        label         = "cardAlpha"
    )

    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0x1AFFFFFF), Color(0x0AFFFFFF))
                )
            )
            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(16.dp))
            .graphicsLayer { this.alpha = alpha }
            .padding(14.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = icon,
                    contentDescription = null,
                    tint               = color,
                    modifier           = Modifier.size(18.dp)
                )
            }
            Text(
                text  = value,
                style = TextStyle(
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White
                )
            )
            Text(
                text  = label,
                style = TextStyle(
                    fontSize = 11.sp,
                    color    = Color(0xFF7A8BA0)
                )
            )
        }
    }
}

// ─────────────────────────────────────────
// FILTER CHIPS
// ─────────────────────────────────────────

@Composable
private fun AlertsFilterRow(
    selected: AlertFilter,
    onSelect: (AlertFilter) -> Unit
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AlertFilter.values().forEach { filter ->
            val isSelected = selected == filter
            val color = when (filter) {
                AlertFilter.ALL      -> Color(0xFF00C2FF)
                AlertFilter.CRITICAL -> Color(0xFFFF4D6D)
                AlertFilter.WARNING  -> Color(0xFFFFA500)
                AlertFilter.INFO     -> Color(0xFF00D4AA)
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (isSelected) color.copy(alpha = 0.2f)
                        else Color(0x0DFFFFFF)
                    )
                    .border(
                        1.dp,
                        if (isSelected) color.copy(alpha = 0.5f)
                        else Color(0x1AFFFFFF),
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { onSelect(filter) }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text  = filter.label,
                    style = TextStyle(
                        fontSize   = 13.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold
                        else FontWeight.Normal,
                        color      = if (isSelected) color else Color(0xFF7A8BA0)
                    )
                )
            }
        }
    }
}

// ─────────────────────────────────────────
// ALERT CARD
// ─────────────────────────────────────────

@Composable
private fun AlertCard(alert: AlertModel) {
    val (accentColor, bgColor, typeLabel, typeIcon) = when (alert.type) {
        AlertType.CRITICAL -> Quad(
            Color(0xFFFF4D6D),
            Color(0x1AFF4D6D),
            "Critique",
            Icons.Outlined.Error
        )
        AlertType.WARNING  -> Quad(
            Color(0xFFFFA500),
            Color(0x1AFFA500),
            "Attention",
            Icons.Outlined.Warning
        )
        AlertType.INFO     -> Quad(
            Color(0xFF00C2FF),
            Color(0x1A00C2FF),
            "Info",
            Icons.Outlined.Info
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0x1AFFFFFF), Color(0x0AFFFFFF))
                )
            )
            .border(
                1.dp,
                if (alert.isResolved) Color(0x0FFFFFFF)
                else accentColor.copy(alpha = 0.2f),
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment     = Alignment.Top
        ) {
            // Colored icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (alert.isResolved) Color(0x0DFFFFFF) else bgColor
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = if (alert.isResolved)
                        Icons.Outlined.CheckCircle else typeIcon,
                    contentDescription = null,
                    tint               = if (alert.isResolved)
                        Color(0xFF00D4AA) else accentColor,
                    modifier           = Modifier.size(22.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Title + badge
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text     = alert.title,
                        style    = TextStyle(
                            fontSize   = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = if (alert.isResolved)
                                Color(0xFF7A8BA0) else Color.White
                        ),
                        maxLines  = 1,
                        overflow  = TextOverflow.Ellipsis,
                        modifier  = Modifier.weight(1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Type badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (alert.isResolved) Color(0x0D00D4AA)
                                else bgColor
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text  = if (alert.isResolved) "Résolu" else typeLabel,
                            style = TextStyle(
                                fontSize   = 10.sp,
                                fontWeight = FontWeight.Medium,
                                color      = if (alert.isResolved)
                                    Color(0xFF00D4AA) else accentColor
                            )
                        )
                    }
                }

                // Description
                Text(
                    text     = alert.description,
                    style    = TextStyle(
                        fontSize = 12.sp,
                        color    = Color(0xFF7A8BA0)
                    ),
                    maxLines  = 2,
                    overflow  = TextOverflow.Ellipsis
                )

                // Timestamp
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector        = Icons.Outlined.AccessTime,
                        contentDescription = null,
                        tint               = Color(0xFF3A4D5C),
                        modifier           = Modifier.size(11.dp)
                    )
                    Text(
                        text  = alert.date,
                        style = TextStyle(
                            fontSize = 11.sp,
                            color    = Color(0xFF3A4D5C)
                        )
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────
// HELPER — Destructuring for 4 values
// ─────────────────────────────────────────

private data class Quad<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)