package com.example.sysmonitor.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sysmonitor.data.model.LogAction
import com.example.sysmonitor.data.model.LogModel
import com.example.sysmonitor.ui.viewmodel.LogsViewModel

@Composable
fun LogsScreen(
    viewModel: LogsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x15AB7EFF), Color.Transparent),
                    center = Offset(size.width * 0.85f, size.height * 0.15f),
                    radius = size.width * 0.5f
                ),
                center = Offset(size.width * 0.85f, size.height * 0.15f),
                radius = size.width * 0.5f
            )
        }

        LazyColumn(
            modifier            = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            if (!uiState.isLoading) {
                item {
                    LogsStatsRow(
                        total   = uiState.totalLogs,
                        logins  = uiState.totalLogins,
                        actions = uiState.totalActions,
                        errors  = uiState.totalErrors
                    )
                }
            }

            item {
                LogsSearchBar(
                    query    = uiState.searchQuery,
                    onChange = { viewModel.onSearchQueryChange(it) }
                )
            }

            item {
                LogsFilterChips(
                    selected = uiState.selectedAction,
                    onSelect = { viewModel.onActionFilterChange(it) }
                )
            }

            item {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text  = "Journal d'activité",
                        style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF9AAFC2))
                    )
                    Text(
                        text  = "${uiState.filteredLogs.size} résultat(s)",
                        style = TextStyle(fontSize = 11.sp, color = Color(0xFF4A5C6A))
                    )
                }
            }

            if (uiState.isLoading) { items(5) { LogCardShimmer() } }

            if (!uiState.isLoading && uiState.errorMessage != null) {
                item { LogsErrorState(message = uiState.errorMessage!!, onRetry = { viewModel.loadLogs() }) }
            }

            if (!uiState.isLoading && uiState.filteredLogs.isEmpty() && uiState.errorMessage == null) {
                item { LogsEmptyState() }
            }

            items(uiState.filteredLogs, key = { it.id }) { log -> LogCard(log = log) }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
private fun LogsStatsRow(total: Int, logins: Int, actions: Int, errors: Int) {
    Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
        LogStatCard("Total",   total.toString(),   Icons.Outlined.List,         Color(0xFF00C2FF), Modifier.weight(1f))
        LogStatCard("Logins",  logins.toString(),  Icons.Outlined.Login,        Color(0xFF00D4AA), Modifier.weight(1f))
        LogStatCard("Actions", actions.toString(), Icons.Outlined.FlashOn,      Color(0xFFFFA500), Modifier.weight(1f))
        LogStatCard("Erreurs", errors.toString(),  Icons.Outlined.ErrorOutline, Color(0xFFFF4D6D), Modifier.weight(1f))
    }
}

@Composable
private fun LogStatCard(label: String, value: String, icon: ImageVector, color: Color, modifier: Modifier = Modifier) {
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(if (visible) 1f else 0f, tween(500, easing = EaseOutCubic), label = "statAlpha")
    LaunchedEffect(Unit) { visible = true }
    Box(modifier.clip(RoundedCornerShape(14.dp))
        .background(Brush.linearGradient(listOf(Color(0x1AFFFFFF), Color(0x0AFFFFFF))))
        .border(1.dp, Color(0x12FFFFFF), RoundedCornerShape(14.dp))
        .graphicsLayer { this.alpha = alpha }.padding(12.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(icon, null, tint = color, modifier = Modifier.size(16.dp))
            Text(text = value, style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White))
            Text(text = label, style = TextStyle(fontSize = 10.sp, color = Color(0xFF7A8BA0)))
        }
    }
}

@Composable
private fun LogsSearchBar(query: String, onChange: (String) -> Unit) {
    OutlinedTextField(
        value = query, onValueChange = onChange,
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)),
        placeholder = { Text("Rechercher par utilisateur, action…", style = TextStyle(fontSize = 13.sp, color = Color(0xFF3D5166))) },
        leadingIcon = { Icon(Icons.Outlined.Search, null, tint = Color(0xFF4A6580), modifier = Modifier.size(20.dp)) },
        trailingIcon = {
            if (query.isNotBlank()) {
                IconButton(onClick = { onChange("") }) {
                    Icon(Icons.Default.Close, "Effacer", tint = Color(0xFF4A6580), modifier = Modifier.size(18.dp))
                }
            }
        },
        singleLine = true, shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White, unfocusedTextColor = Color(0xFFCDD9E5),
            focusedContainerColor = Color(0x1A1E3A5F), unfocusedContainerColor = Color(0x0D1E3A5F),
            focusedBorderColor = Color(0xFFAB7EFF), unfocusedBorderColor = Color(0x221E3A5F), cursorColor = Color(0xFFAB7EFF)
        ), textStyle = TextStyle(fontSize = 14.sp, color = Color.White)
    )
}

@Composable
private fun LogsFilterChips(selected: LogAction?, onSelect: (LogAction?) -> Unit) {
    val filters = listOf(null to "Tous", LogAction.LOGIN to "Login", LogAction.LOGOUT to "Logout",
        LogAction.CREATE to "Create", LogAction.DELETE to "Delete", LogAction.UPDATE to "Update", LogAction.ERROR to "Erreur")
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), Arrangement.spacedBy(6.dp)) {
        filters.forEach { (action, label) ->
            val isSelected = selected == action
            val color = logActionColor(action)
            Box(Modifier.clip(RoundedCornerShape(20.dp))
                .background(if (isSelected) color.copy(0.18f) else Color(0x0AFFFFFF))
                .border(1.dp, if (isSelected) color.copy(0.45f) else Color(0x12FFFFFF), RoundedCornerShape(20.dp))
                .clickable { onSelect(action) }.padding(horizontal = 13.dp, vertical = 7.dp)) {
                Text(text = label, style = TextStyle(fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (isSelected) color else Color(0xFF7A8BA0)))
            }
        }
    }
}

@Composable
fun LogCard(log: LogModel) {
    val color = logActionColor(log.action)
    val icon  = logActionIcon(log.action)
    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
        .background(Brush.linearGradient(listOf(Color(0x18FFFFFF), Color(0x0AFFFFFF))))
        .border(1.dp, if (log.isError) Color(0x30FF4D6D) else Color(0x0DFFFFFF), RoundedCornerShape(14.dp))
        .padding(14.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.Top) {
            Box(Modifier.size(38.dp).clip(CircleShape).background(color.copy(0.13f)), Alignment.Center) {
                Icon(icon, null, tint = color, modifier = Modifier.size(19.dp))
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text(text = log.user, style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White),
                        modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                    Spacer(Modifier.width(8.dp))
                    Box(Modifier.clip(RoundedCornerShape(6.dp)).background(color.copy(0.13f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)) {
                        Text(text = log.action.label, style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Medium, color = color))
                    }
                }
                Text(text = log.description, style = TextStyle(fontSize = 12.sp, color = Color(0xFF7A8BA0)), maxLines = 2, overflow = TextOverflow.Ellipsis)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(Icons.Outlined.AccessTime, null, tint = Color(0xFF3A4D5C), modifier = Modifier.size(11.dp))
                    Text(text = log.date, style = TextStyle(fontSize = 11.sp, color = Color(0xFF3A4D5C)))
                }
            }
        }
    }
}

@Composable
private fun LogCardShimmer() {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val ta by transition.animateFloat(0f, 1000f, infiniteRepeatable(tween(1200, easing = LinearEasing), RepeatMode.Restart), "t")
    val brush = Brush.linearGradient(listOf(Color(0xFF1A2A3A), Color(0xFF243445), Color(0xFF1A2A3A)), Offset(ta - 300f, 0f), Offset(ta, 0f))
    Box(Modifier.fillMaxWidth().height(75.dp).clip(RoundedCornerShape(14.dp)).background(brush))
}

@Composable
private fun LogsErrorState(message: String, onRetry: () -> Unit) {
    Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp))
        .background(Color(0x1AFF4D6D)).border(1.dp, Color(0x30FF4D6D), RoundedCornerShape(14.dp)).padding(24.dp),
        Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(Icons.Outlined.CloudOff, null, tint = Color(0xFFFF7A93), modifier = Modifier.size(32.dp))
            Text(text = message, style = TextStyle(fontSize = 13.sp, color = Color(0xFFFF7A93)), textAlign = TextAlign.Center)
            Button(onClick = onRetry, shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D6D))) {
                Text("Réessayer", color = Color.White)
            }
        }
    }
}

@Composable
private fun LogsEmptyState() {
    Box(Modifier.fillMaxWidth().padding(top = 48.dp), Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Icon(Icons.Outlined.SearchOff, null, tint = Color(0xFF2A3D52), modifier = Modifier.size(52.dp))
            Text(text = "Aucun log trouvé", style = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color(0xFF7A8BA0)))
            Text(text = "Modifiez vos filtres ou votre recherche", style = TextStyle(fontSize = 12.sp, color = Color(0xFF4A5C6A)), textAlign = TextAlign.Center)
        }
    }
}

fun logActionColor(action: LogAction?): Color = when (action) {
    LogAction.LOGIN  -> Color(0xFF00D4AA)
    LogAction.LOGOUT -> Color(0xFF4D9FFF)
    LogAction.CREATE -> Color(0xFF00C2FF)
    LogAction.DELETE -> Color(0xFFFF4D6D)
    LogAction.UPDATE -> Color(0xFFFFA500)
    LogAction.ERROR  -> Color(0xFFFF4D6D)
    null             -> Color(0xFF7A8BA0)
}

fun logActionIcon(action: LogAction): ImageVector = when (action) {
    LogAction.LOGIN  -> Icons.Outlined.Login
    LogAction.LOGOUT -> Icons.Outlined.Logout
    LogAction.CREATE -> Icons.Outlined.AddCircleOutline
    LogAction.DELETE -> Icons.Outlined.DeleteOutline
    LogAction.UPDATE -> Icons.Outlined.Edit
    LogAction.ERROR  -> Icons.Outlined.ErrorOutline
}