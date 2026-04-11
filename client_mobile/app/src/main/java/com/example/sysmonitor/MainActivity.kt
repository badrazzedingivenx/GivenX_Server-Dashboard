package com.example.sysmonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.sysmonitor.ui.navigation.SysMonitorNavGraph

// ─────────────────────────────────────────
// THEME
// ─────────────────────────────────────────

private val SysMonitorDarkColorScheme = darkColorScheme(
    primary = Color(0xFF00C2FF),
    onPrimary = Color(0xFF0A0E1A),
    primaryContainer = Color(0xFF003A52),
    onPrimaryContainer = Color(0xFFB3E9FF),
    secondary = Color(0xFF6E40FF),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1A0A4D),
    onSecondaryContainer = Color(0xFFCCBBFF),
    tertiary = Color(0xFF00D4AA),
    onTertiary = Color(0xFF0A1628),
    background = Color(0xFF0A0E1A),
    onBackground = Color(0xFFE1EBF5),
    surface = Color(0xFF0D1B2A),
    onSurface = Color(0xFFCDD9E5),
    surfaceVariant = Color(0xFF1A2C3D),
    onSurfaceVariant = Color(0xFF7A8BA0),
    error = Color(0xFFFF4D6D),
    onError = Color.White,
    errorContainer = Color(0xFF4D001A),
    onErrorContainer = Color(0xFFFFB3C1),
    outline = Color(0xFF2A3D52),
    outlineVariant = Color(0xFF1A2C3D)
)

@Composable
fun SysMonitorTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current

    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as android.app.Activity).window
            window.statusBarColor = Color(0xFF0A0E1A).toArgb()
            window.navigationBarColor = Color(0xFF0A0E1A).toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = SysMonitorDarkColorScheme,
        content = content
    )
}

// ─────────────────────────────────────────
// MAIN ACTIVITY
// ─────────────────────────────────────────

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            SysMonitorTheme {
                SysMonitorNavGraph()
            }
        }
    }
}