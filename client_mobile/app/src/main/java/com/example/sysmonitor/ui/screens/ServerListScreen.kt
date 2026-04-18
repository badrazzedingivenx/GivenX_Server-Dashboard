package com.example.sysmonitor.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sysmonitor.data.model.ServerModel
import com.example.sysmonitor.data.model.ServerStatus
import com.example.sysmonitor.ui.viewmodel.ServerViewModel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServerListScreen(
    onNavigateToDetail: (String) -> Unit,
    onNavigateToCreate: () -> Unit,
    viewModel: ServerViewModel = viewModel()
) {
    val uiState by viewModel.listState.collectAsStateWithLifecycle()
    val pullRefreshState = rememberPullToRefreshState()

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
        PullToRefreshBox(
            isRefreshing = uiState.isRefreshing,
            onRefresh = { viewModel.refreshServers() },
            state = pullRefreshState,
            modifier = Modifier.fillMaxSize()
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item { Spacer(modifier = Modifier.height(52.dp)) }

                // Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Mes Serveurs",
                                style = TextStyle(
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "${uiState.servers.size} serveur(s) configuré(s)",
                                style = TextStyle(
                                    fontSize = 13.sp,
                                    color = Color(0xFF7A8BA0)
                                )
                            )
                        }
                        // Add button
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            Color(0xFF00C2FF),
                                            Color(0xFF6E40FF)
                                        )
                                    )
                                )
                                .clickable { onNavigateToCreate() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Ajouter",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(8.dp)) }

                // Loading shimmer
                if (uiState.isLoading) {
                    items(3) { ServerCardShimmer() }
                }

                // Error state
                if (!uiState.isLoading && uiState.errorMessage != null) {
                    item {
                        ServerErrorCard(
                            message = uiState.errorMessage!!,
                            onRetry = { viewModel.loadServers() }
                        )
                    }
                }

                // Empty state
                if (!uiState.isLoading &&
                    uiState.servers.isEmpty() &&
                    uiState.errorMessage == null
                ) {
                    item { ServerEmptyState(onAdd = onNavigateToCreate) }
                }

                // Server list
                items(uiState.servers) { server ->
                    ServerCard(
                        server = server,
                        onClick = { onNavigateToDetail(server.id) }
                    )
                }

                item { Spacer(modifier = Modifier.height(40.dp)) }
            }
        }
    }
}

// ─────────────────────────────────────────
// SERVER CARD
// ─────────────────────────────────────────

@Composable
fun ServerCard(
    server: ServerModel,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0x1AFFFFFF), Color(0x0AFFFFFF))
                )
            )
            .border(
                1.dp,
                Color(0x1AFFFFFF),
                RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Server icon
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x1A00C2FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Dns,
                            contentDescription = null,
                            tint = Color(0xFF00C2FF),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Column {
                        Text(
                            text = server.name,
                            style = TextStyle(
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        )
                        Text(
                            text = server.ipAddress,
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = Color(0xFF7A8BA0)
                            )
                        )
                    }
                }
                ServerStatusBadge(status = server.status)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Metrics row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ServerMiniMetric(
                    label = "CPU",
                    value = "${server.cpuUsage.toInt()}%",
                    color = Color(0xFFFF6B6B),
                    modifier = Modifier.weight(1f)
                )
                ServerMiniMetric(
                    label = "RAM",
                    value = "${server.ramUsage.toInt()}%",
                    color = Color(0xFF4D9FFF),
                    modifier = Modifier.weight(1f)
                )
                ServerMiniMetric(
                    label = "Uptime",
                    value = "${server.uptimeHours}h",
                    color = Color(0xFF00D4AA),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocationOn,
                    contentDescription = null,
                    tint = Color(0xFF4A5C6A),
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = "${server.location} · ${server.os}",
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = Color(0xFF4A5C6A)
                    )
                )
            }
        }
    }
}

// ─────────────────────────────────────────
// SHARED SMALL COMPONENTS
// ─────────────────────────────────────────

@Composable
fun ServerStatusBadge(status: ServerStatus) {
    val (color, label) = when (status) {
        ServerStatus.ONLINE     -> Color(0xFF00D4AA) to "En ligne"
        ServerStatus.OFFLINE    -> Color(0xFFFF4D6D) to "Hors ligne"
        ServerStatus.RESTARTING -> Color(0xFFFFA500) to "Redémarrage"
        ServerStatus.STOPPED    -> Color(0xFF7A8BA0) to "Arrêté"
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                color = color
            )
        )
    }
}

@Composable
fun ServerMiniMetric(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.08f))
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            )
            Text(
                text = label,
                style = TextStyle(
                    fontSize = 10.sp,
                    color = Color(0xFF7A8BA0)
                )
            )
        }
    }
}

@Composable
fun ServerCardShimmer() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )
    val shimmerBrush = Brush.linearGradient(
        colors = listOf(
            Color(0xFF1A2A3A),
            Color(0xFF243445),
            Color(0xFF1A2A3A)
        ),
        start = Offset(translateAnim - 300f, 0f),
        end = Offset(translateAnim, 0f)
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(shimmerBrush)
    )
}

@Composable
fun ServerErrorCard(message: String, onRetry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0x1AFF4D6D))
            .border(1.dp, Color(0x33FF4D6D), RoundedCornerShape(18.dp))
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.CloudOff,
                contentDescription = null,
                tint = Color(0xFFFF7A93),
                modifier = Modifier.size(36.dp)
            )
            Text(
                text = message,
                style = TextStyle(
                    fontSize = 13.sp,
                    color = Color(0xFFFF7A93),
                    textAlign = TextAlign.Center
                )
            )
            Button(
                onClick = onRetry,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF4D6D)
                )
            ) {
                Text(text = "Réessayer", color = Color.White)
            }
        }
    }
}

@Composable
fun ServerEmptyState(onAdd: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Dns,
                contentDescription = null,
                tint = Color(0xFF2A3D52),
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = "Aucun serveur configuré",
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF7A8BA0)
                )
            )
            Text(
                text = "Ajoutez votre premier serveur\npour commencer la surveillance",
                style = TextStyle(
                    fontSize = 13.sp,
                    color = Color(0xFF4A5C6A),
                    textAlign = TextAlign.Center
                )
            )
            Button(
                onClick = onAdd,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00C2FF)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Ajouter un serveur")
            }
        }
    }
}