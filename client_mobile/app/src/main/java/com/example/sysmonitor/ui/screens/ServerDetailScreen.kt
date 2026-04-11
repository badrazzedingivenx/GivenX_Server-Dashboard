package com.example.sysmonitor.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sysmonitor.data.model.ServerModel
import com.example.sysmonitor.ui.viewmodel.ServerViewModel

@Composable
fun ServerDetailScreen(
    serverId: String,
    onNavigateBack: () -> Unit,
    viewModel: ServerViewModel = viewModel()
) {
    val uiState by viewModel.detailState.collectAsStateWithLifecycle()
    var showRestartDialog by remember { mutableStateOf(false) }
    var showStopDialog    by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(serverId) {
        viewModel.loadServerDetail(serverId)
    }

    LaunchedEffect(uiState.actionSuccess) {
        uiState.actionSuccess?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearDetailMessages()
        }
    }

    // Restart confirmation dialog
    if (showRestartDialog) {
        ServerActionDialog(
            title = "Redémarrer le serveur",
            message = "Êtes-vous sûr de vouloir redémarrer ce serveur ? " +
                    "Les connexions actives seront interrompues.",
            confirmText = "Redémarrer",
            confirmColor = Color(0xFFFFA500),
            onConfirm = {
                showRestartDialog = false
                viewModel.restartServer(serverId)
            },
            onDismiss = { showRestartDialog = false }
        )
    }

    // Stop confirmation dialog
    if (showStopDialog) {
        ServerActionDialog(
            title = "Arrêter le serveur",
            message = "Êtes-vous sûr de vouloir arrêter ce serveur ? " +
                    "Il sera inaccessible jusqu'au prochain démarrage.",
            confirmText = "Arrêter",
            confirmColor = Color(0xFFFF4D6D),
            onConfirm = {
                showStopDialog = false
                viewModel.stopServer(serverId)
            },
            onDismiss = { showStopDialog = false }
        )
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF1E2D40),
                    contentColor = Color(0xFFCDD9E5),
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        containerColor = Color.Transparent
    ) { paddingValues ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
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
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF00C2FF)
                    )
                }
                uiState.errorMessage != null -> {
                    ServerErrorCard(
                        message = uiState.errorMessage!!,
                        onRetry = { viewModel.loadServerDetail(serverId) }
                    )
                }
                uiState.server != null -> {
                    ServerDetailContent(
                        server = uiState.server!!,
                        actionLoading = uiState.actionLoading,
                        onBack = onNavigateBack,
                        onRestart = { showRestartDialog = true },
                        onStop = { showStopDialog = true }
                    )
                }
            }
        }
    }
}

@Composable
private fun ServerDetailContent(
    server: ServerModel,
    actionLoading: Boolean,
    onBack: () -> Unit,
    onRestart: () -> Unit,
    onStop: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(52.dp))

        // Back + title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x1A1E3A5F))
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Retour",
                    tint = Color(0xFF7A8BA0),
                    modifier = Modifier.size(20.dp)
                )
            }
            Column {
                Text(
                    text = server.name,
                    style = TextStyle(
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
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
            Spacer(modifier = Modifier.weight(1f))
            ServerStatusBadge(status = server.status)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Info card
        DetailCard {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                DetailRow(
                    icon = Icons.Outlined.Terminal,
                    label = "Système",
                    value = server.os
                )
                Divider(color = Color(0x0FFFFFFF))
                DetailRow(
                    icon = Icons.Outlined.LocationOn,
                    label = "Localisation",
                    value = server.location
                )
                Divider(color = Color(0x0FFFFFFF))
                DetailRow(
                    icon = Icons.Outlined.Schedule,
                    label = "Uptime",
                    value = "${server.uptimeHours} heures"
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Metrics
        Text(
            text = "Métriques en temps réel",
            style = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF9AAFC2)
            )
        )
        Spacer(modifier = Modifier.height(12.dp))

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
        }

        Spacer(modifier = Modifier.height(28.dp))

        // Actions
        Text(
            text = "Actions",
            style = TextStyle(
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF9AAFC2)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Restart button
            ActionButton(
                text = "Redémarrer",
                color = Color(0xFFFFA500),
                isLoading = actionLoading,
                onClick = onRestart,
                modifier = Modifier.weight(1f)
            )
            // Stop button
            ActionButton(
                text = "Arrêter",
                color = Color(0xFFFF4D6D),
                isLoading = actionLoading,
                onClick = onStop,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

// ─────────────────────────────────────────
// DETAIL COMPONENTS
// ─────────────────────────────────────────

@Composable
fun DetailCard(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0x1AFFFFFF), Color(0x0AFFFFFF))
                )
            )
            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(18.dp))
            .padding(20.dp)
    ) { content() }
}

@Composable
fun DetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFF4A6580),
            modifier = Modifier.size(18.dp)
        )
        Text(
            text = label,
            style = TextStyle(fontSize = 13.sp, color = Color(0xFF7A8BA0)),
            modifier = Modifier.weight(1f)
        )
        Text(
            text = value,
            style = TextStyle(
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFCDD9E5)
            )
        )
    }
}

@Composable
fun ActionButton(
    text: String,
    color: Color,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .clickable(enabled = !isLoading) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                color = color,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = color
                )
            )
        }
    }
}

@Composable
fun ServerActionDialog(
    title: String,
    message: String,
    confirmText: String,
    confirmColor: Color,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0D1B2A),
        shape = RoundedCornerShape(20.dp),
        title = {
            Text(
                text = title,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )
        },
        text = {
            Text(
                text = message,
                style = TextStyle(
                    fontSize = 13.sp,
                    color = Color(0xFF7A8BA0)
                )
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = confirmColor
                )
            ) {
                Text(text = confirmText, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Annuler",
                    color = Color(0xFF7A8BA0)
                )
            }
        }
    )
}