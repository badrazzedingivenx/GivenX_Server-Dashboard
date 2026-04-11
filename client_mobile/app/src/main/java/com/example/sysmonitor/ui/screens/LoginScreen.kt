package com.example.sysmonitor.ui.screens
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.*
import com.example.sysmonitor.ui.screens.AuthTextField
import com.example.sysmonitor.ui.screens.GradientButton

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var globalError by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current

    val contentAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 700, easing = EaseOutCubic),
        label = "contentAlpha"
    )
    val contentOffset by animateFloatAsState(
        targetValue = 0f,
        animationSpec = tween(durationMillis = 700, easing = EaseOutCubic),
        label = "contentOffset"
    )

    fun validate(): Boolean {
        var valid = true
        emailError = null
        passwordError = null
        globalError = null

        if (email.isBlank()) {
            emailError = "L'adresse e-mail est obligatoire"
            valid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Adresse e-mail invalide"
            valid = false
        }

        if (password.isBlank()) {
            passwordError = "Le mot de passe est obligatoire"
            valid = false
        } else if (password.length < 6) {
            passwordError = "Le mot de passe doit contenir au moins 6 caractères"
            valid = false
        }

        return valid
    }

    fun handleLogin() {
        focusManager.clearFocus()
        if (!validate()) return

        isLoading = true
        globalError = null

        // Simulate network call
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            isLoading = false
            if (email == "admin@sysmonitor.com" && password == "password123") {
                onLoginSuccess()
            } else {
                globalError = "Identifiants incorrects. Veuillez réessayer."
            }
        }, 1800)
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
        // Decorative ambient orbs
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x2200C2FF), Color.Transparent),
                    center = Offset(size.width * 0.15f, size.height * 0.15f),
                    radius = size.width * 0.45f
                ),
                center = Offset(size.width * 0.15f, size.height * 0.15f),
                radius = size.width * 0.45f
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x1A6E40FF), Color.Transparent),
                    center = Offset(size.width * 0.85f, size.height * 0.75f),
                    radius = size.width * 0.50f
                ),
                center = Offset(size.width * 0.85f, size.height * 0.75f),
                radius = size.width * 0.50f
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x1500D4AA), Color.Transparent),
                    center = Offset(size.width * 0.5f, size.height * 0.9f),
                    radius = size.width * 0.35f
                ),
                center = Offset(size.width * 0.5f, size.height * 0.9f),
                radius = size.width * 0.35f
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .graphicsLayer {
                    alpha = contentAlpha
                    translationY = contentOffset
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Logo / Brand mark
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
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
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "SysMonitor",
                style = TextStyle(
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = (-0.5).sp
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Tableau de bord de surveillance système",
                style = TextStyle(
                    fontSize = 13.sp,
                    color = Color(0xFF7A8BA0),
                    letterSpacing = 0.2.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Glass Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0x1AFFFFFF),
                                Color(0x0DFFFFFF)
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                        )
                    )
                    .border(
                        width = 1.dp,
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0x33FFFFFF),
                                Color(0x0DFFFFFF)
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(28.dp)
            ) {
                Column {
                    Text(
                        text = "Connexion",
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Accédez à votre espace de surveillance",
                        style = TextStyle(
                            fontSize = 13.sp,
                            color = Color(0xFF7A8BA0)
                        )
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Email Field
                    AuthTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = null
                            globalError = null
                        },
                        label = "Adresse e-mail",
                        placeholder = "vous@exemple.com",
                        leadingIcon = Icons.Default.Email,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        isError = emailError != null,
                        errorMessage = emailError
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Field
                    AuthTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = null
                            globalError = null
                        },
                        label = "Mot de passe",
                        placeholder = "••••••••",
                        leadingIcon = Icons.Default.Lock,
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { handleLogin() }
                        ),
                        isError = passwordError != null,
                        errorMessage = passwordError,
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible)
                                        Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (passwordVisible)
                                        "Masquer" else "Afficher",
                                    tint = Color(0xFF7A8BA0)
                                )
                            }
                        }
                    )

                    // Global error
                    AnimatedVisibility(
                        visible = globalError != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        globalError?.let {
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0x1AFF4D6D))
                                    .border(
                                        1.dp,
                                        Color(0x33FF4D6D),
                                        RoundedCornerShape(12.dp)
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

                    Spacer(modifier = Modifier.height(28.dp))

                    // Login Button
                    GradientButton(
                        text = "Se connecter",
                        isLoading = isLoading,
                        onClick = { handleLogin() }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Divider
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF1E2D40)
                )
                Text(
                    text = "  ou  ",
                    style = TextStyle(fontSize = 12.sp, color = Color(0xFF4A5C6A))
                )
                Divider(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFF1E2D40)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Register link
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pas encore de compte ? ",
                    style = TextStyle(
                        fontSize = 14.sp,
                        color = Color(0xFF7A8BA0)
                    )
                )
                Text(
                    text = "Créer un compte",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF00C2FF)
                    ),
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}