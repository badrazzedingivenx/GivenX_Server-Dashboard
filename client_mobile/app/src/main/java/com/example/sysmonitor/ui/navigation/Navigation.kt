package com.example.sysmonitor

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
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
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// ─────────────────────────────────────────
// ROUTE CONSTANTS
// ─────────────────────────────────────────

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
}

// ─────────────────────────────────────────
// NAV GRAPH
// ─────────────────────────────────────────

@Composable
fun SysMonitorNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.LOGIN
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        enterTransition = {
            fadeIn(animationSpec = tween(350)) +
                    slideInHorizontally(
                        initialOffsetX = { it / 5 },
                        animationSpec = tween(350, easing = EaseOutCubic)
                    )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(250)) +
                    slideOutHorizontally(
                        targetOffsetX = { -it / 5 },
                        animationSpec = tween(250, easing = EaseInCubic)
                    )
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(350)) +
                    slideInHorizontally(
                        initialOffsetX = { -it / 5 },
                        animationSpec = tween(350, easing = EaseOutCubic)
                    )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(250)) +
                    slideOutHorizontally(
                        targetOffsetX = { it / 5 },
                        animationSpec = tween(250, easing = EaseInCubic)
                    )
        }
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                }
            )
        }

        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.DASHBOARD) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.DASHBOARD) {
            DashboardPlaceholderScreen()
        }
    }
}

// ─────────────────────────────────────────
// SHARED UI COMPONENTS
// ─────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    isError: Boolean = false,
    errorMessage: String? = null,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = TextStyle(
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = if (isError) Color(0xFFFF7A93) else Color(0xFF9AAFC2),
                letterSpacing = 0.4.sp
            ),
            modifier = Modifier.padding(bottom = 6.dp)
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp)),
            placeholder = {
                Text(
                    text = placeholder,
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color(0xFF3D5166)
                    )
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = if (isError) Color(0xFFFF7A93) else Color(0xFF4A6580),
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            isError = isError,
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color(0xFFCDD9E5),
                focusedContainerColor = Color(0x1A1E3A5F),
                unfocusedContainerColor = Color(0x0D1E3A5F),
                errorContainerColor = Color(0x1AFF4D6D),
                focusedBorderColor = Color(0xFF00C2FF),
                unfocusedBorderColor = Color(0x331E3A5F),
                errorBorderColor = Color(0xFFFF4D6D),
                cursorColor = Color(0xFF00C2FF),
                focusedLeadingIconColor = Color(0xFF00C2FF),
                unfocusedLeadingIconColor = Color(0xFF4A6580),
                errorLeadingIconColor = Color(0xFFFF7A93)
            ),
            textStyle = TextStyle(
                fontSize = 14.sp,
                color = Color.White
            )
        )

        AnimatedVisibility(
            visible = isError && errorMessage != null,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            errorMessage?.let {
                Text(
                    text = "⚠ $it",
                    style = TextStyle(
                        fontSize = 11.sp,
                        color = Color(0xFFFF7A93)
                    ),
                    modifier = Modifier.padding(top = 5.dp, start = 4.dp)
                )
            }
        }
    }
}

@Composable
fun GradientButton(
    text: String,
    isLoading: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    gradientColors: List<Color> = listOf(Color(0xFF00C2FF), Color(0xFF6E40FF))
) {
    val interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
    val scale by animateFloatAsState(
        targetValue = if (isLoading) 0.97f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
        label = "buttonScale"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .height(52.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(
                brush = if (!isLoading) {
                    Brush.linearGradient(colors = gradientColors)
                } else {
                    Brush.linearGradient(
                        colors = listOf(Color(0xFF1E3A5F), Color(0xFF1E3A5F))
                    )
                }
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = !isLoading
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = Color(0xFF00C2FF),
                    strokeWidth = 2.dp
                )
                Text(
                    text = "Traitement en cours…",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF7A8BA0)
                    )
                )
            }
        } else {
            Text(
                text = text,
                style = TextStyle(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    letterSpacing = 0.3.sp
                )
            )
        }
    }
}

// ─────────────────────────────────────────
// DASHBOARD PLACEHOLDER
// ─────────────────────────────────────────

@Composable
fun DashboardPlaceholderScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0A0E1A), Color(0xFF0D1B2A), Color(0xFF0A1628))
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x2200C2FF), Color.Transparent),
                    center = Offset(size.width * 0.5f, size.height * 0.3f),
                    radius = size.width * 0.6f
                ),
                center = Offset(size.width * 0.5f, size.height * 0.3f),
                radius = size.width * 0.6f
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF00C2FF), Color(0xFF6E40FF))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "SM",
                    style = TextStyle(
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White
                    )
                )
            }

            Text(
                text = "Tableau de bord",
                style = TextStyle(
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            )

            Text(
                text = "Connexion réussie ! Bienvenue dans SysMonitor.",
                style = TextStyle(
                    fontSize = 14.sp,
                    color = Color(0xFF7A8BA0),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.padding(horizontal = 40.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0x1A00C2FF))
                    .border(1.dp, Color(0x3300C2FF), RoundedCornerShape(12.dp))
                    .padding(horizontal = 20.dp, vertical = 10.dp)
            ) {
                Text(
                    text = "● Système opérationnel",
                    style = TextStyle(
                        fontSize = 13.sp,
                        color = Color(0xFF00C2FF),
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}