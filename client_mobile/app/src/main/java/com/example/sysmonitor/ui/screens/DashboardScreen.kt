package com.example.sysmonitor.ui.screens

import androidx.compose.animation.core.EaseInOutSine
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.AccountTree
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.FolderOpen
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sysmonitor.data.model.MetricsModel
import com.example.sysmonitor.ui.viewmodel.DashboardViewModel

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: DashboardViewModel = viewModel()) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pullRefreshState  = rememberPullToRefreshState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(
                message     = it,
                actionLabel = "Réessayer",
                duration    = SnackbarDuration.Long
            )
            viewModel.clearError()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        SnackbarHost(
            hostState = snackbarHostState,
            modifier  = Modifier.align(Alignment.BottomCenter)
        ) { data ->
            Snackbar(
                snackbarData   = data,
                containerColor = Color(0xFF1E2D40),
                contentColor   = Color(0xFFCDD9E5),
                actionColor    = Color(0xFF00C2FF),
                shape          = RoundedCornerShape(12.dp)
            )
        }

        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh    = { viewModel.refresh() },
            state        = pullRefreshState,
            modifier     = Modifier.fillMaxSize()
        ) {
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
                        brush  = Brush.radialGradient(
                            listOf(Color(0x1500C2FF), Color.Transparent),
                            Offset(size.width * 0.1f, size.height * 0.1f), size.width * 0.5f),
                        center = Offset(size.width * 0.1f, size.height * 0.1f),
                        radius = size.width * 0.5f
                    )
                    drawCircle(
                        brush  = Brush.radialGradient(
                            listOf(Color(0x126E40FF), Color.Transparent),
                            Offset(size.width * 0.9f, size.height * 0.6f), size.width * 0.45f),
                        center = Offset(size.width * 0.9f, size.height * 0.6f),
                        radius = size.width * 0.45f
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(Modifier.height(16.dp))
                    DashboardHeader()
                    Spacer(Modifier.height(28.dp))
                    SystemStatusBadge(
                        isLoading = uiState.isLoading,
                        hasError  = uiState.metrics == null && !uiState.isLoading
                    )
                    Spacer(Modifier.height(24.dp))

                    // ✅ named style= parameter
                    Text(
                        text  = "Métriques système",
                        style = TextStyle(
                            fontSize      = 16.sp,
                            fontWeight    = FontWeight.SemiBold,
                            color         = Color(0xFF9AAFC2),
                            letterSpacing = 0.5.sp
                        )
                    )
                    Spacer(Modifier.height(14.dp))

                    when {
                        uiState.isLoading -> MetricsShimmerGrid()
                        uiState.metrics != null -> {
                            // ✅ local val avoids smart cast error on delegated property
                            val metrics = uiState.metrics!!
                            MetricsGrid(metrics = metrics)
                        }
                        else -> ErrorCard(onRetry = { viewModel.loadMetrics() })
                    }

                    Spacer(Modifier.height(28.dp))

                    if (!uiState.isLoading && uiState.metrics != null) {
                        Text(
                            text  = "Activité récente",
                            style = TextStyle(
                                fontSize      = 16.sp,
                                fontWeight    = FontWeight.SemiBold,
                                color         = Color(0xFF9AAFC2),
                                letterSpacing = 0.5.sp
                            )
                        )
                        Spacer(Modifier.height(14.dp))
                        ActivityPlaceholderCard()
                    }

                    Spacer(Modifier.height(40.dp))
                }
            }
        }
    }
}

// ─────────────────────────────────────────
// HEADER
// ─────────────────────────────────────────

@Composable
private fun DashboardHeader() {
    Row(
        modifier              = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment     = Alignment.CenterVertically
    ) {
        Column {
            // ✅ style= named, all params named inside TextStyle
            Text(
                text  = "Tableau de bord",
                style = TextStyle(
                    fontSize      = 26.sp,
                    fontWeight    = FontWeight.Bold,
                    color         = Color.White,
                    letterSpacing = (-0.5).sp
                )
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text  = "Vue d'ensemble du système",
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
                .background(Color(0x1A1E3A5F))
                .border(1.dp, Color(0x221E3A5F), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector        = Icons.Outlined.Notifications,
                contentDescription = null,
                tint               = Color(0xFF7A8BA0),
                modifier           = Modifier.size(22.dp)
            )
        }
    }
}

// ─────────────────────────────────────────
// STATUS BADGE
// ─────────────────────────────────────────

@Composable
private fun SystemStatusBadge(isLoading: Boolean, hasError: Boolean) {
    val (color, text) = when {
        isLoading -> Color(0xFF7A8BA0) to "Chargement en cours…"
        hasError  -> Color(0xFFFF4D6D) to "Système non disponible"
        else      -> Color(0xFF00D4AA) to "Système opérationnel"
    }
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.4f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(tween(900, easing = EaseInOutSine), RepeatMode.Reverse),
        label         = "pulseAlpha"
    )
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.1f))
            .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = if (!hasError) pulseAlpha else 1f))
        )
        Text(
            text  = text,
            style = TextStyle(
                fontSize   = 12.sp,
                fontWeight = FontWeight.Medium,
                color      = color
            )
        )
    }
}

// ─────────────────────────────────────────
// METRICS GRID
// ─────────────────────────────────────────

data class MetricCardData(
    val title: String,
    val value: String,
    val subtitle: String,
    val icon: ImageVector,
    val accentColor: Color,
    val progress: Float?
)

@Composable
private fun MetricsGrid(metrics: MetricsModel) {
    val cards = listOf(
        MetricCardData(
            title       = "Processeur",
            value       = "${metrics.cpuUsage.toInt()}%",
            subtitle    = "Utilisation CPU",
            icon        = Icons.Outlined.Memory,
            accentColor = Color(0xFFFF6B6B),
            progress    = metrics.cpuUsage / 100f
        ),
        MetricCardData(
            title       = "Mémoire RAM",
            value       = "${metrics.ramUsage.toInt()}%",
            subtitle    = if (metrics.ramTotalGb > 0f)
                "${"%.1f".format(metrics.ramUsedGb)} / ${"%.0f".format(metrics.ramTotalGb)} Go"
            else "Utilisation RAM",
            icon        = Icons.Outlined.Storage,
            accentColor = Color(0xFF4D9FFF),
            progress    = metrics.ramUsage / 100f
        ),
        MetricCardData(
            title       = "Stockage",
            value       = "${metrics.diskUsage.toInt()}%",
            subtitle    = if (metrics.diskTotalGb > 0f)
                "${"%.0f".format(metrics.diskUsedGb)} / ${"%.0f".format(metrics.diskTotalGb)} Go"
            else "Utilisation Disque",
            icon        = Icons.Outlined.FolderOpen,
            accentColor = Color(0xFF00D4AA),
            progress    = metrics.diskUsage / 100f
        ),
        MetricCardData(
            title       = "Processus",
            value       = "${metrics.runningProcesses}",
            subtitle    = "En cours d'exécution",
            icon        = Icons.Outlined.AccountTree,
            accentColor = Color(0xFFAB7EFF),
            progress    = null
        )
    )

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        cards.chunked(2).forEach { row ->
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                row.forEach { card -> MetricCard(data = card, modifier = Modifier.weight(1f)) }
                if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

// ─────────────────────────────────────────
// METRIC CARD
// ─────────────────────────────────────────

@Composable
private fun MetricCard(data: MetricCardData, modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(false) }
    val animatedProgress by animateFloatAsState(
        targetValue   = if (visible) (data.progress ?: 0f) else 0f,
        animationSpec = tween(900, easing = EaseOutCubic),
        label         = "progress"
    )
    val contentAlpha by animateFloatAsState(
        targetValue   = if (visible) 1f else 0f,
        animationSpec = tween(600, easing = EaseOutCubic),
        label         = "alpha"
    )
    LaunchedEffect(Unit) { visible = true }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(listOf(Color(0x1AFFFFFF), Color(0x0AFFFFFF)))
            )
            .border(
                1.dp,
                Brush.linearGradient(listOf(Color(0x28FFFFFF), Color(0x08FFFFFF))),
                RoundedCornerShape(20.dp)
            )
            .graphicsLayer { alpha = contentAlpha }
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(data.accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = data.icon,
                        contentDescription = data.title,
                        tint               = data.accentColor,
                        modifier           = Modifier.size(22.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(data.accentColor.copy(alpha = 0.6f))
                )
            }

            Spacer(Modifier.height(16.dp))

            // ✅ ALL Text() calls below use style = TextStyle(...) with named params
            Text(
                text  = data.value,
                style = TextStyle(
                    fontSize      = 28.sp,
                    fontWeight    = FontWeight.Bold,
                    color         = Color.White,
                    letterSpacing = (-1).sp
                )
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text  = data.title,
                style = TextStyle(
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = Color(0xFFCDD9E5)
                )
            )
            Text(
                text  = data.subtitle,
                style = TextStyle(
                    fontSize = 11.sp,
                    color    = Color(0xFF7A8BA0)
                )
            )

            if (data.progress != null) {
                Spacer(Modifier.height(14.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color(0xFF1E2D40))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress.coerceIn(0f, 1f))
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(data.accentColor.copy(0.7f), data.accentColor)
                                )
                            )
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────
// SHIMMER
// ─────────────────────────────────────────

@Composable
private fun MetricsShimmerGrid() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val ta by transition.animateFloat(
        initialValue  = 0f,
        targetValue   = 1000f,
        animationSpec = infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Restart),
        label         = "shimmerT"
    )
    val brush = Brush.linearGradient(
        colors = listOf(Color(0xFF1A2A3A), Color(0xFF243445), Color(0xFF1A2A3A)),
        start  = Offset(ta - 300f, 0f),
        end    = Offset(ta, 0f)
    )
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        repeat(2) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                repeat(2) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(160.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(brush)
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────
// ERROR CARD
// ─────────────────────────────────────────

@Composable
private fun ErrorCard(onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0x1AFF4D6D))
            .border(1.dp, Color(0x33FF4D6D), RoundedCornerShape(20.dp))
            .padding(28.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier         = Modifier.size(56.dp).clip(CircleShape).background(Color(0x1AFF4D6D)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector        = Icons.Outlined.CloudOff,
                    contentDescription = null,
                    tint               = Color(0xFFFF7A93),
                    modifier           = Modifier.size(28.dp)
                )
            }
            Text(
                text  = "Données non disponibles",
                style = TextStyle(
                    fontSize   = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = Color(0xFFFF7A93)
                )
            )
            Text(
                text      = "Impossible de récupérer les métriques.\nVérifiez la connexion à l'API.",
                style     = TextStyle(
                    fontSize = 13.sp,
                    color    = Color(0xFF7A8BA0)
                ),
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Button(
                onClick        = onRetry,
                shape          = RoundedCornerShape(12.dp),
                colors         = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D6D)),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 10.dp)
            ) {
                Icon(
                    imageVector        = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier           = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text  = "Réessayer",
                    style = TextStyle(
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = Color.White
                    )
                )
            }
        }
    }
}

// ─────────────────────────────────────────
// ACTIVITY CARD
// ─────────────────────────────────────────

@Composable
private fun ActivityPlaceholderCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(listOf(Color(0x1AFFFFFF), Color(0x0AFFFFFF))))
            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            repeat(3) { index ->
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                when (index) {
                                    0    -> Color(0x1A00C2FF)
                                    1    -> Color(0x1A00D4AA)
                                    else -> Color(0x1AAB7EFF)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (index) {
                                0    -> Icons.Outlined.CheckCircle
                                1    -> Icons.Outlined.Sync
                                else -> Icons.Outlined.Warning
                            },
                            contentDescription = null,
                            tint = when (index) {
                                0    -> Color(0xFF00C2FF)
                                1    -> Color(0xFF00D4AA)
                                else -> Color(0xFFAB7EFF)
                            },
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text  = when (index) {
                                0    -> "Sauvegarde complétée"
                                1    -> "Synchronisation en cours"
                                else -> "Alerte mémoire"
                            },
                            style = TextStyle(
                                fontSize   = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color      = Color(0xFFCDD9E5)
                            )
                        )
                        Text(
                            text  = when (index) {
                                0    -> "Il y a 2 minutes"
                                1    -> "En cours"
                                else -> "Il y a 15 minutes"
                            },
                            style = TextStyle(
                                fontSize = 11.sp,
                                color    = Color(0xFF4A5C6A)
                            )
                        )
                    }
                }
                if (index < 2) {
                    HorizontalDivider(color = Color(0x0FFFFFFF), thickness = 1.dp)
                }
            }
        }
    }
}