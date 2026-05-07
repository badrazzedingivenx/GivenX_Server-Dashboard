package com.example.sysmonitor.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sysmonitor.data.model.CreateServerRequest
import com.example.sysmonitor.ui.viewmodel.ServerViewModel

@Composable
fun ServerCreateScreen(
    onNavigateBack: () -> Unit,
    onCreatedSuccess: () -> Unit,
    viewModel: ServerViewModel = viewModel()
) {
    val uiState by viewModel.createState.collectAsStateWithLifecycle()

    var name        by remember { mutableStateOf("") }
    var ipAddress   by remember { mutableStateOf("") }
    var os          by remember { mutableStateOf("") }
    var location    by remember { mutableStateOf("") }
    var nameError   by remember { mutableStateOf<String?>(null) }
    var ipError     by remember { mutableStateOf<String?>(null) }
    var osError     by remember { mutableStateOf<String?>(null) }

    // Navigate back on success
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            viewModel.resetCreateState()
            onCreatedSuccess()
        }
    }

    fun validate(): Boolean {
        var valid = true
        nameError = null; ipError = null; osError = null

        if (name.isBlank()) {
            nameError = "Le nom est obligatoire"
            valid = false
        }
        if (ipAddress.isBlank()) {
            ipError = "L'adresse IP est obligatoire"
            valid = false
        } else if (!Regex(
                """^\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}$"""
            ).matches(ipAddress)
        ) {
            ipError = "Format IP invalide (ex: 192.168.1.1)"
            valid = false
        }
        if (os.isBlank()) {
            osError = "Le système d'exploitation est obligatoire"
            valid = false
        }
        return valid
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(modifier = Modifier.height(52.dp))

            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
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
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Retour",
                        tint = Color(0xFF7A8BA0),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = "Nouveau Serveur",
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Text(
                        text = "Renseignez les informations du serveur",
                        style = TextStyle(
                            fontSize = 12.sp,
                            color = Color(0xFF7A8BA0)
                        )
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

                    // Server name
                    AuthTextField(
                        value = name,
                        onValueChange = { name = it; nameError = null },
                        label = "Nom du serveur *",
                        placeholder = "ex: Serveur Principal",
                        leadingIcon = Icons.Outlined.Dns,
                        isError = nameError != null,
                        errorMessage = nameError
                    )

                    // IP Address
                    AuthTextField(
                        value = ipAddress,
                        onValueChange = { ipAddress = it; ipError = null },
                        label = "Adresse IP *",
                        placeholder = "ex: 192.168.1.1",
                        leadingIcon = Icons.Outlined.Router,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal
                        ),
                        isError = ipError != null,
                        errorMessage = ipError
                    )

                    // OS
                    AuthTextField(
                        value = os,
                        onValueChange = { os = it; osError = null },
                        label = "Système d'exploitation *",
                        placeholder = "ex: Ubuntu 22.04",
                        leadingIcon = Icons.Outlined.Terminal,
                        isError = osError != null,
                        errorMessage = osError
                    )

                    // Location
                    AuthTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = "Localisation",
                        placeholder = "ex: Paris, FR",
                        leadingIcon = Icons.Outlined.LocationOn
                    )

                    // Global error
                    AnimatedVisibility(visible = uiState.errorMessage != null) {
                        uiState.errorMessage?.let {
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
                                    text = it,
                                    style = TextStyle(
                                        fontSize = 13.sp,
                                        color = Color(0xFFFF7A93)
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Submit button
                    GradientButton(
                        text = "Créer le serveur",
                        isLoading = uiState.isLoading,
                        onClick = {
                            if (validate()) {
                                viewModel.createServer(
                                    name      = name,
                                    ipAddress = ipAddress,
                                    os        = os,
                                    location  = location
                                )
                            }
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}