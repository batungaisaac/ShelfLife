package com.example.shelflife

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.lifecycleScope
import com.example.shelflife.data.SettingsRepository
import com.example.shelflife.ui.EXPIRING_SOON_DAYS
import com.example.shelflife.ui.SettingsScreen
import com.example.shelflife.ui.theme.ShelfLifeTheme
import kotlinx.coroutines.launch

// Settings get their own activity rather than another route in the nav graph.
class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        val settings = SettingsRepository(applicationContext)
        setContent {
            val shakeEnabled by settings.shakeEnabled.collectAsState(initial = true)
            val warningDays by settings.warningDays.collectAsState(initial = EXPIRING_SOON_DAYS)
            ShelfLifeTheme {
                SettingsScreen(
                    shakeEnabled = shakeEnabled,
                    warningDays = warningDays,
                    onShakeChange = { lifecycleScope.launch { settings.setShakeEnabled(it) } },
                    onWarningDaysChange = { lifecycleScope.launch { settings.setWarningDays(it) } },
                    onBack = { finish() }
                )
            }
        }
    }
}
