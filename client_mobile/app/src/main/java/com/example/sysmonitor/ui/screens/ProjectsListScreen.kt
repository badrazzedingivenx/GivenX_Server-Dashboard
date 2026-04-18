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
import com.example.sysmonitor.data.model.ProjectModel
import com.example.sysmonitor.ui.viewmodel.ProjectViewModel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsListScreen(
    onNavigateToCreate: () -> Unit,
    viewModel: ProjectViewModel = viewModel()
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
        // Ambient orbs
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x1500D4AA), Color.Transparent),
                    center = Offset(size.width * 0.8f, size.height * 0.1f),
                    radius = size.width * 0.45f
                ),
                center = Offset(size.width * 0.8f, size.height * 0.1f),
                radius = size.width * 0.45f
            )
        }

        PullToRefreshBox(
            isRefreshing = false,
            onRefresh    = { viewModel.loadProjects() },
            state        = pullRefreshState,
            modifier     = Modifier.fillMaxSize()
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
                                text = "Mes Projets",
                                style = TextStyle(
                                    fontSize   = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color      = Color.White
                                )
                            )
                            Text(
                                text = "${uiState.projects.size} projet(s)",
                                style = TextStyle(
                                    fontSize = 13.sp,
                                    color    = Color(0xFF7A8BA0)
                                )
                            )
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(4.dp)) }

                // Loading shimmer
                if (uiState.isLoading) {
                    items(3) { ProjectCardShimmer() }
                }

                // Error
                if (!uiState.isLoading && uiState.errorMessage != null) {
                    item {
                        ProjectErrorCard(
                            message = uiState.errorMessage!!,
                            onRetry = { viewModel.loadProjects() }
                        )
                    }
                }

                // Empty state
                if (!uiState.isLoading &&
                    uiState.projects.isEmpty() &&
                    uiState.errorMessage == null
                ) {
                    item { ProjectEmptyState(onAdd = onNavigateToCreate) }
                }

                // Project list
                items(uiState.projects) { project ->
                    ProjectCard(project = project)
                }

                item { Spacer(modifier = Modifier.height(100.dp)) }
            }
        }

        // FAB
        FloatingActionButton(
            onClick           = onNavigateToCreate,
            modifier          = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 24.dp, bottom = 88.dp),
            containerColor    = Color(0xFF00C2FF),
            contentColor      = Color.White,
            shape             = CircleShape
        ) {
            Icon(
                imageVector        = Icons.Default.Add,
                contentDescription = "Créer un projet",
                modifier           = Modifier.size(26.dp)
            )
        }
    }
}

// ─────────────────────────────────────────
// PROJECT CARD
// ─────────────────────────────────────────

@Composable
fun ProjectCard(project: ProjectModel) {
    val isActive = project.status.lowercase() == "active"

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
            .padding(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {

            // Top row — name + status
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier              = Modifier.weight(1f)
                ) {
                    // Icon
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x1A00D4AA)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector        = Icons.Outlined.FolderOpen,
                            contentDescription = null,
                            tint               = Color(0xFF00D4AA),
                            modifier           = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Text(
                            text  = project.name,
                            style = TextStyle(
                                fontSize   = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = Color.White
                            )
                        )
                        if (!project.description.isNullOrBlank()) {
                            Text(
                                text  = project.description,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color    = Color(0xFF7A8BA0)
                                ),
                                maxLines = 1
                            )
                        }
                    }
                }

                // Status badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (isActive) Color(0x1A00D4AA)
                            else Color(0x1A7A8BA0)
                        )
                        .border(
                            1.dp,
                            if (isActive) Color(0x3300D4AA)
                            else Color(0x337A8BA0),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text  = if (isActive) "Actif" else "Inactif",
                        style = TextStyle(
                            fontSize   = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color      = if (isActive) Color(0xFF00D4AA)
                            else Color(0xFF7A8BA0)
                        )
                    )
                }
            }

            // Server info
            project.server?.let { server ->
                Divider(color = Color(0x0FFFFFFF))
                Row(
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector        = Icons.Outlined.Dns,
                        contentDescription = null,
                        tint               = Color(0xFF4A5C6A),
                        modifier           = Modifier.size(13.dp)
                    )
                    Text(
                        text  = server.name,
                        style = TextStyle(
                            fontSize = 11.sp,
                            color    = Color(0xFF4A5C6A)
                        )
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────
// SUPPORTING COMPONENTS
// ─────────────────────────────────────────

@Composable
fun ProjectCardShimmer() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue  = 0f,
        targetValue   = 1000f,
        animationSpec = infiniteRepeatable(
            animation  = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerTranslate"
    )
    val shimmerBrush = Brush.linearGradient(
        colors = listOf(Color(0xFF1A2A3A), Color(0xFF243445), Color(0xFF1A2A3A)),
        start  = Offset(translateAnim - 300f, 0f),
        end    = Offset(translateAnim, 0f)
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(shimmerBrush)
    )
}

@Composable
fun ProjectErrorCard(message: String, onRetry: () -> Unit) {
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
                imageVector        = Icons.Outlined.CloudOff,
                contentDescription = null,
                tint               = Color(0xFFFF7A93),
                modifier           = Modifier.size(36.dp)
            )
            Text(
                text      = message,
                style     = TextStyle(fontSize = 13.sp, color = Color(0xFFFF7A93)),
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRetry,
                shape   = RoundedCornerShape(10.dp),
                colors  = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFF4D6D)
                )
            ) {
                Text(text = "Réessayer", color = Color.White)
            }
        }
    }
}

@Composable
fun ProjectEmptyState(onAdd: () -> Unit) {
    Box(
        modifier         = Modifier
            .fillMaxWidth()
            .padding(top = 60.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector        = Icons.Outlined.FolderOpen,
                contentDescription = null,
                tint               = Color(0xFF2A3D52),
                modifier           = Modifier.size(64.dp)
            )
            Text(
                text  = "Aucun projet trouvé",
                style = TextStyle(
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = Color(0xFF7A8BA0)
                )
            )
            Text(
                text      = "Créez votre premier projet\npour commencer",
                style     = TextStyle(fontSize = 13.sp, color = Color(0xFF4A5C6A)),
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onAdd,
                shape   = RoundedCornerShape(12.dp),
                colors  = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF00C2FF)
                )
            ) {
                Icon(
                    imageVector        = Icons.Default.Add,
                    contentDescription = null,
                    modifier           = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Créer un projet")
            }
        }
    }
}