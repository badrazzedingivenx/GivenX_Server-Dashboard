package com.example.sysmonitor.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ─────────────────────────────────────────
// COLOR SCHEME
// ─────────────────────────────────────────

private val DarkColorScheme = darkColorScheme(
    primary      = Color(0xFF00C2FF),
    onPrimary    = Color(0xFF0A0E1A),
    secondary    = Color(0xFF6E40FF),
    onSecondary  = Color.White,
    background   = Color(0xFF0A0E1A),
    onBackground = Color(0xFFE1EBF5),
    surface      = Color(0xFF0D1B2A),
    onSurface    = Color(0xFFCDD9E5),
    error        = Color(0xFFFF4D6D),
    onError      = Color.White
)

// ─────────────────────────────────────────
// THEME
// ─────────────────────────────────────────

@Composable
fun SysMonitorTheme(
    content: @Composable () -> Unit
) {
    // ✅ FIX — removed typography parameter entirely
    // MaterialTheme typography must be androidx.compose.material3.Typography
    // If you have a custom Type.kt that returns kotlin.text.Typography → it crashes
    MaterialTheme(
        colorScheme = DarkColorScheme,
        // typography = Typography  ← DO NOT pass this if your Type.kt is wrong
        content     = content
    )
}