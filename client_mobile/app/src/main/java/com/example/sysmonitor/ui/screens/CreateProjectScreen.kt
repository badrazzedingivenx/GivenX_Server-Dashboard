package com.example.sysmonitor.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sysmonitor.data.model.ServerDropdownItem
import com.example.sysmonitor.ui.viewmodel.ProjectViewModel

@Composable
fun CreateProjectScreen(
    onNavigateBack: () -> Unit,
    onCreatedSuccess: () -> Unit,
    viewModel: ProjectViewModel = viewModel()
) {
    val uiState by viewModel.createState.collectAsStateWithLifecycle()

    var name           by remember { mutableStateOf("") }
    var description    by remember { mutableStateOf("") }
    var selectedServer by remember { mutableStateOf<ServerDropdownItem?>(null) }
    var nameError      by remember { mutableStateOf<String?>(null) }
    var dropdownExpanded by remember { mutableStateOf(false) }

    // Load servers on enter
    LaunchedEffect(Unit) {
        viewModel.loadServers()
    }

    // Navigate on success
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.resetCreateState()
            onCreatedSuccess()
        }
    }

    fun validate(): Boolean {
        nameError = null
        if (name.isBlank()) {
            nameError = "Le nom du projet est obligatoire"
            return false
        }
        return true
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
        // Ambient orb
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x1500D4AA), Color.Transparent),
                    center = Offset(size.width * 0.9f, size.height * 0.2f),
                    radius = size.width * 0.5f
                ),
                center = Offset(size.width * 0.9f, size.height * 0.2f),
                radius = size.width * 0.5f
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(52.dp))

            // Header
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0x1A1E3A5F))
                        .clickable { onNavigateBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector        = Icons.Default.ArrowBack,
                        contentDescription = "Retour",
                        tint               = Color(0xFF7A8BA0),
                        modifier           = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text  = "Nouveau Projet",
                        style = TextStyle(
                            fontSize   = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color      = Color.White
                        )
                    )
                    Text(
                        text  = "Renseignez les informations du projet",
                        style = TextStyle(fontSize = 12.sp, color = Color(0xFF7A8BA0))
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Form card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0x1AFFFFFF), Color(0x0AFFFFFF))
                        )
                    )
                    .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(20.dp))
                    .padding(24.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    // Project name
                    AuthTextField(
                        value         = name,
                        onValueChange = { name = it; nameError = null },
                        label         = "Nom du projet *",
                        placeholder   = "ex: Application Web",
                        leadingIcon   = Icons.Outlined.FolderOpen,
                        isError       = nameError != null,
                        errorMessage  = nameError
                    )

                    // Description
                    AuthTextField(
                        value         = description,
                        onValueChange = { description = it },
                        label         = "Description (optionnelle)",
                        placeholder   = "Décrivez votre projet...",
                        leadingIcon   = Icons.Outlined.Description
                    )

                    // Server Dropdown
                    Column {
                        Text(
                            text  = "Serveur associé",
                            style = TextStyle(
                                fontSize   = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color      = Color(0xFF9AAFC2),
                                letterSpacing = 0.4.sp
                            ),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )

                        Box {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(Color(0x0D1E3A5F))
                                    .border(
                                        1.dp,
                                        Color(0x331E3A5F),
                                        RoundedCornerShape(14.dp)
                                    )
                                    .clickable { dropdownExpanded = true }
                                    .padding(
                                        horizontal = 16.dp,
                                        vertical   = 14.dp
                                    )
                            ) {
                                Row(
                                    modifier              = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment     = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment     = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        if (uiState.serversLoading) {
                                            CircularProgressIndicator(
                                                modifier   = Modifier.size(16.dp),
                                                color      = Color(0xFF00C2FF),
                                                strokeWidth = 2.dp
                                            )
                                        } else {
                                            Icon(
                                                imageVector        = Icons.Outlined.Dns,
                                                contentDescription = null,
                                                tint               = Color(0xFF4A6580),
                                                modifier           = Modifier.size(20.dp)
                                            )
                                        }
                                        Text(
                                            text  = selectedServer?.name
                                                ?: "Sélectionner un serveur",
                                            style = TextStyle(
                                                fontSize = 14.sp,
                                                color    = if (selectedServer != null)
                                                    Color(0xFFCDD9E5)
                                                else
                                                    Color(0xFF3D5166)
                                            )
                                        )
                                    }
                                    Icon(
                                        imageVector        = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint               = Color(0xFF4A6580)
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded        = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false },
                                modifier        = Modifier.background(Color(0xFF0D1B2A))
                            ) {
                                if (uiState.servers.isEmpty()) {
                                    DropdownMenuItem(
                                        text    = {
                                            Text(
                                                text  = "Aucun serveur disponible",
                                                style = TextStyle(
                                                    fontSize = 13.sp,
                                                    color    = Color(0xFF7A8BA0)
                                                )
                                            )
                                        },
                                        onClick = { dropdownExpanded = false }
                                    )
                                } else {
                                    uiState.servers.forEach { server ->
                                        DropdownMenuItem(
                                            text    = {
                                                Text(
                                                    text  = server.name,
                                                    style = TextStyle(
                                                        fontSize = 14.sp,
                                                        color    = Color(0xFFCDD9E5)
                                                    )
                                                )
                                            },
                                            onClick = {
                                                selectedServer   = server
                                                dropdownExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Error message
                    AnimatedVisibility(visible = uiState.errorMessage != null) {
                        uiState.errorMessage?.let { error ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0x1AFF4D6D))
                                    .border(
                                        1.dp,
                                        Color(0x33FF4D6D),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text  = error,
                                    style = TextStyle(
                                        fontSize = 13.sp,
                                        color    = Color(0xFFFF7A93)
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Submit button
                    GradientButton(
                        text      = "Créer le projet",
                        isLoading = uiState.isLoading,
                        onClick   = {
                            if (validate()) {
                                viewModel.createProject(
                                    name        = name,
                                    description = description,
                                    serverId    = selectedServer?.id
                                )
                            }
                        },
                        gradientColors = listOf(
                            Color(0xFF00D4AA),
                            Color(0xFF00C2FF)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}