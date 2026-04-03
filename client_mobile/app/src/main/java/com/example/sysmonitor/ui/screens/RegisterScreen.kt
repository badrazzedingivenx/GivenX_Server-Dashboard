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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    val focusManager = LocalFocusManager.current

    val contentAlpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 700, easing = EaseOutCubic),
        label = "alpha"
    )

    fun validate(): Boolean {
        var valid = true
        nameError = null
        emailError = null
        passwordError = null
        confirmPasswordError = null

        if (fullName.isBlank()) {
            nameError = "Le nom complet est obligatoire"
            valid = false
        } else if (fullName.trim().length < 2) {
            nameError = "Le nom doit contenir au moins 2 caractères"
            valid = false
        }

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

        if (confirmPassword.isBlank()) {
            confirmPasswordError = "Veuillez confirmer le mot de passe"
            valid = false
        } else if (confirmPassword != password) {
            confirmPasswordError = "Les mots de passe ne correspondent pas"
            valid = false
        }

        return valid
    }

    fun handleRegister() {
        focusManager.clearFocus()
        if (!validate()) return

        isLoading = true

        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
            isLoading = false
            successMessage = "Compte créé avec succès !"
            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                onRegisterSuccess()
            }, 1200)
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
        // Ambient orbs
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x1A6E40FF), Color.Transparent),
                    center = Offset(size.width * 0.9f, size.height * 0.1f),
                    radius = size.width * 0.50f
                ),
                center = Offset(size.width * 0.9f, size.height * 0.1f),
                radius = size.width * 0.50f
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x1500D4AA), Color.Transparent),
                    center = Offset(size.width * 0.1f, size.height * 0.65f),
                    radius = size.width * 0.40f
                ),
                center = Offset(size.width * 0.1f, size.height * 0.65f),
                radius = size.width * 0.40f
            )
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color(0x1500C2FF), Color.Transparent),
                    center = Offset(size.width * 0.5f, size.height * 0.95f),
                    radius = size.width * 0.35f
                ),
                center = Offset(size.width * 0.5f, size.height * 0.95f),
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
                },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // Logo
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFF00D4AA), Color(0xFF00C2FF))
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
                text = "Créez votre compte administrateur",
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
                        text = "Inscription",
                        style = TextStyle(
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Renseignez vos informations pour commencer",
                        style = TextStyle(
                            fontSize = 13.sp,
                            color = Color(0xFF7A8BA0)
                        )
                    )

                    Spacer(modifier = Modifier.height(28.dp))

                    // Full Name Field
                    AuthTextField(
                        value = fullName,
                        onValueChange = {
                            fullName = it
                            nameError = null
                        },
                        label = "Nom complet",
                        placeholder = "Saisir Votre Nom Complet",
                        leadingIcon = Icons.Default.Person,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next,
                            capitalization = KeyboardCapitalization.Words
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        ),
                        isError = nameError != null,
                        errorMessage = nameError
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Email Field
                    AuthTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = null
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
                        },
                        label = "Mot de passe",
                        placeholder = "Minimum 6 caractères",
                        leadingIcon = Icons.Default.Lock,
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Confirm Password Field
                    AuthTextField(
                        value = confirmPassword,
                        onValueChange = {
                            confirmPassword = it
                            confirmPasswordError = null
                        },
                        label = "Confirmer le mot de passe",
                        placeholder = "Répétez le mot de passe",
                        leadingIcon = Icons.Default.Lock,
                        visualTransformation = if (confirmPasswordVisible)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { handleRegister() }
                        ),
                        isError = confirmPasswordError != null,
                        errorMessage = confirmPasswordError,
                        trailingIcon = {
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    imageVector = if (confirmPasswordVisible)
                                        Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (confirmPasswordVisible)
                                        "Masquer" else "Afficher",
                                    tint = Color(0xFF7A8BA0)
                                )
                            }
                        }
                    )

                    // Success message
                    AnimatedVisibility(
                        visible = successMessage != null,
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically()
                    ) {
                        successMessage?.let {
                            Spacer(modifier = Modifier.height(16.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0x1A00D4AA))
                                    .border(
                                        1.dp,
                                        Color(0x3300D4AA),
                                        RoundedCornerShape(12.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = it,
                                    style = TextStyle(
                                        fontSize = 13.sp,
                                        color = Color(0xFF00D4AA),
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Register Button
                    GradientButton(
                        text = "Créer mon compte",
                        isLoading = isLoading,
                        onClick = { handleRegister() },
                        gradientColors = listOf(Color(0xFF00D4AA), Color(0xFF00C2FF))
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(modifier = Modifier.weight(1f), color = Color(0xFF1E2D40))
                Text(
                    text = "  ou  ",
                    style = TextStyle(fontSize = 12.sp, color = Color(0xFF4A5C6A))
                )
                Divider(modifier = Modifier.weight(1f), color = Color(0xFF1E2D40))
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Vous avez déjà un compte ? ",
                    style = TextStyle(fontSize = 14.sp, color = Color(0xFF7A8BA0))
                )
                Text(
                    text = "Se connecter",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF00C2FF)
                    ),
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}