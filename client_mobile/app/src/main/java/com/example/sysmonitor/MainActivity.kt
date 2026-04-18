package com.example.sysmonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.sysmonitor.ui.navigation.SysMonitorNavGraph
import com.example.sysmonitor.ui.theme.SysMonitorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Step 1 — Let content draw behind system bars
        enableEdgeToEdge()

        // Step 2 — Make status bar and nav bar fully transparent
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor     = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        // Step 3 — Set light/dark icons on system bars
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars     = false  // white icons
        insetsController.isAppearanceLightNavigationBars = false  // white icons



        setContent {
            SysMonitorTheme {
                SysMonitorNavGraph()
            }
        }
    }
}