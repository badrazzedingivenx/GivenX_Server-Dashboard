package com.example.sysmonitor.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ─────────────────────────────────────────
// MODEL
// ─────────────────────────────────────────

data class UserItem(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val status: UserStatus,
    val lastLogin: String
)

enum class UserStatus { ACTIVE, BLOCKED }

// ─────────────────────────────────────────
// MOCK DATA
// ─────────────────────────────────────────

private val initialUsers = listOf(
    UserItem("1", "Saad Larouchi",    "saad@example.com",    "Admin",      UserStatus.ACTIVE,   "Il y a 2 min"),
    UserItem("2", "Marwa Benali",     "marwa@example.com",   "Utilisateur",UserStatus.ACTIVE,   "Il y a 1h"),
    UserItem("3", "Youssef El Alami", "youssef@example.com", "Utilisateur",UserStatus.ACTIVE,   "Il y a 3h"),
    UserItem("4", "Fatima Zahra",     "fatima@example.com",  "Utilisateur",UserStatus.BLOCKED,  "Il y a 2j"),
    UserItem("5", "Admin Système",    "admin@sysmonitor.com","Admin",      UserStatus.ACTIVE,   "Il y a 5 min"),
    UserItem("6", "Test User",        "test@example.com",    "Utilisateur",UserStatus.BLOCKED,  "Il y a 7j"),
    UserItem("7", "Karim Mansouri",   "karim@example.com",   "Utilisateur",UserStatus.ACTIVE,   "Aujourd'hui"),
    UserItem("8", "Leila Haddad",     "leila@example.com",   "Utilisateur",UserStatus.ACTIVE,   "Hier"),
)

// ─────────────────────────────────────────
// SCREEN
// ─────────────────────────────────────────

@Composable
fun UsersScreen() {
    val users          = remember { mutableStateListOf(*initialUsers.toTypedArray()) }
    var userToDelete   by remember { mutableStateOf<UserItem?>(null) }
    var userToBlock    by remember { mutableStateOf<UserItem?>(null) }
    var isRefreshing   by remember { mutableStateOf(false) }

    val activeCount  = users.count { it.status == UserStatus.ACTIVE }
    val blockedCount = users.count { it.status == UserStatus.BLOCKED }

    // ── Delete dialog
    userToDelete?.let { user ->
        ConfirmDialog(
            title       = "Supprimer l'utilisateur",
            message     = "Voulez-vous vraiment supprimer « ${user.name} » ? Cette action est irréversible.",
            confirmText = "Supprimer",
            confirmColor = Color(0xFFFF4D6D),
            onConfirm   = {
                users.remove(user)
                userToDelete = null
            },
            onDismiss   = { userToDelete = null }
        )
    }

    // ── Block dialog
    userToBlock?.let { user ->
        val isBlocked = user.status == UserStatus.BLOCKED
        ConfirmDialog(
            title        = if (isBlocked) "Débloquer l'utilisateur" else "Bloquer l'utilisateur",
            message      = if (isBlocked)
                "Voulez-vous débloquer « ${user.name} » ?"
            else
                "Voulez-vous bloquer « ${user.name} » ? Il ne pourra plus se connecter.",
            confirmText  = if (isBlocked) "Débloquer" else "Bloquer",
            confirmColor = if (isBlocked) Color(0xFF00D4AA) else Color(0xFFFFA500),
            onConfirm    = {
                val index = users.indexOfFirst { it.id == user.id }
                if (index != -1) {
                    users[index] = users[index].copy(
                        status = if (isBlocked) UserStatus.ACTIVE else UserStatus.BLOCKED
                    )
                }
                userToBlock = null
            },
            onDismiss    = { userToBlock = null }
        )
    }

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
                    listOf(Color(0x1500C2FF), Color.Transparent),
                    Offset(size.width * 0.8f, size.height * 0.1f), size.width * 0.45f
                ),
                center = Offset(size.width * 0.8f, size.height * 0.1f),
                radius = size.width * 0.45f
            )
        }

        LazyColumn(
            modifier            = Modifier.fillMaxSize().padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding      = PaddingValues(bottom = 40.dp)
        ) {
            item { Spacer(modifier = Modifier.height(16.dp)) }

            // Header
            item {
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text  = "Utilisateurs",
                            style = TextStyle(
                                fontSize   = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color      = Color.White
                            )
                        )
                        Text(
                            text  = "${users.size} utilisateur(s)",
                            style = TextStyle(fontSize = 13.sp, color = Color(0xFF7A8BA0))
                        )
                    }
                    // Refresh button
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x1A00C2FF))
                            .border(1.dp, Color(0x2200C2FF), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { isRefreshing = !isRefreshing }) {
                            Icon(
                                imageVector        = Icons.Default.Refresh,
                                contentDescription = "Rafraîchir",
                                tint               = Color(0xFF00C2FF),
                                modifier           = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // Summary badges
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatusBadgeLarge(
                        label  = "Actifs",
                        count  = activeCount,
                        color  = Color(0xFF00D4AA),
                        modifier = Modifier.weight(1f)
                    )
                    StatusBadgeLarge(
                        label  = "Bloqués",
                        count  = blockedCount,
                        color  = Color(0xFFFFA500),
                        modifier = Modifier.weight(1f)
                    )
                    StatusBadgeLarge(
                        label  = "Total",
                        count  = users.size,
                        color  = Color(0xFF00C2FF),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Section label
            item {
                Text(
                    text  = "Liste des utilisateurs",
                    style = TextStyle(
                        fontSize   = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = Color(0xFF9AAFC2)
                    )
                )
            }

            // Empty state
            if (users.isEmpty()) {
                item {
                    Box(
                        modifier         = Modifier.fillMaxWidth().padding(top = 60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                imageVector        = Icons.Outlined.People,
                                contentDescription = null,
                                tint               = Color(0xFF2A3D52),
                                modifier           = Modifier.size(56.dp)
                            )
                            Text(
                                text  = "Aucun utilisateur",
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

            // User cards
            items(users, key = { it.id }) { user ->
                UserCard(
                    user      = user,
                    onBlock   = { userToBlock  = user },
                    onDelete  = { userToDelete = user }
                )
            }
        }
    }
}

// ─────────────────────────────────────────
// USER CARD
// ─────────────────────────────────────────

@Composable
private fun UserCard(
    user: UserItem,
    onBlock: () -> Unit,
    onDelete: () -> Unit
) {
    val isBlocked    = user.status == UserStatus.BLOCKED
    val statusColor  = if (isBlocked) Color(0xFFFFA500) else Color(0xFF00D4AA)
    val statusLabel  = if (isBlocked) "Bloqué" else "Actif"
    val avatarColor  = when (user.role) {
        "Admin"        -> Color(0xFFFFA500)
        else           -> Color(0xFF00C2FF)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                brush = Brush.linearGradient(listOf(Color(0x1AFFFFFF), Color(0x0AFFFFFF)))
            )
            .border(
                1.dp,
                if (isBlocked) Color(0x22FFA500) else Color(0x0FFFFFFF),
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

            // Top row — avatar + info + status
            Row(
                modifier              = Modifier.fillMaxWidth(),
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(avatarColor.copy(alpha = 0.15f))
                        .border(1.dp, avatarColor.copy(alpha = 0.3f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text  = user.name.first().uppercase(),
                        style = TextStyle(
                            fontSize   = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color      = avatarColor
                        )
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text  = user.name,
                        style = TextStyle(
                            fontSize   = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = Color.White
                        )
                    )
                    Text(
                        text  = user.email,
                        style = TextStyle(fontSize = 12.sp, color = Color(0xFF7A8BA0))
                    )
                }

                // Status badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(statusColor.copy(alpha = 0.15f))
                        .border(1.dp, statusColor.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text  = statusLabel,
                        style = TextStyle(
                            fontSize   = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color      = statusColor
                        )
                    )
                }
            }

            // Role + last login
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text  = "Rôle : ${user.role}",
                    style = TextStyle(fontSize = 12.sp, color = Color(0xFF4A5C6A))
                )
                Text(
                    text  = user.lastLogin,
                    style = TextStyle(fontSize = 12.sp, color = Color(0xFF3A4D5C))
                )
            }

            // Action buttons
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Block / Unblock button
                Button(
                    onClick        = onBlock,
                    modifier       = Modifier.weight(1f).height(38.dp),
                    shape          = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    colors         = ButtonDefaults.buttonColors(
                        containerColor = if (isBlocked)
                            Color(0xFF00D4AA).copy(alpha = 0.15f)
                        else
                            Color(0xFFFFA500).copy(alpha = 0.15f)
                    )
                ) {
                    Icon(
                        imageVector        = Icons.Outlined.Block,
                        contentDescription = null,
                        tint               = if (isBlocked) Color(0xFF00D4AA) else Color(0xFFFFA500),
                        modifier           = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text  = if (isBlocked) "Débloquer" else "Bloquer",
                        style = TextStyle(
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color      = if (isBlocked) Color(0xFF00D4AA) else Color(0xFFFFA500)
                        )
                    )
                }

                // Delete button
                Button(
                    onClick        = onDelete,
                    modifier       = Modifier.weight(1f).height(38.dp),
                    shape          = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    colors         = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF4D6D).copy(alpha = 0.15f)
                    )
                ) {
                    Icon(
                        imageVector        = Icons.Outlined.Delete,
                        contentDescription = null,
                        tint               = Color(0xFFFF7A93),
                        modifier           = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text  = "Supprimer",
                        style = TextStyle(
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color      = Color(0xFFFF7A93)
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
private fun StatusBadgeLarge(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(color.copy(alpha = 0.1f))
            .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text  = count.toString(),
                style = TextStyle(
                    fontSize   = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color      = color
                )
            )
            Text(
                text  = label,
                style = TextStyle(fontSize = 11.sp, color = Color(0xFF7A8BA0))
            )
        }
    }
}

@Composable
private fun ConfirmDialog(
    title: String,
    message: String,
    confirmText: String,
    confirmColor: Color,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor   = Color(0xFF0D1B2A),
        shape            = RoundedCornerShape(20.dp),
        title = {
            Text(
                text  = title,
                style = TextStyle(
                    fontSize   = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color      = Color.White
                )
            )
        },
        text = {
            Text(
                text  = message,
                style = TextStyle(
                    fontSize = 13.sp,
                    color    = Color(0xFF7A8BA0)
                )
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape   = RoundedCornerShape(10.dp),
                colors  = ButtonDefaults.buttonColors(containerColor = confirmColor)
            ) {
                Text(
                    text  = confirmText,
                    style = TextStyle(color = Color.White, fontWeight = FontWeight.SemiBold)
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text  = "Annuler",
                    style = TextStyle(color = Color(0xFF7A8BA0))
                )
            }
        }
    )
}