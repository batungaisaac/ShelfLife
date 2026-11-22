package com.example.shelflife

import android.annotation.SuppressLint
import android.graphics.Color
import android.location.LocationManager
import android.os.Bundle
import android.os.CancellationSignal
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.core.location.LocationManagerCompat
import androidx.lifecycle.lifecycleScope
import com.example.shelflife.data.SettingsRepository
import com.example.shelflife.ui.EXPIRING_SOON_DAYS
import com.example.shelflife.ui.SettingsScreen
import com.example.shelflife.ui.theme.ShelfLifeTheme
import kotlinx.coroutines.launch

// Settings get their own activity rather than another route in the nav graph.
class SettingsActivity : ComponentActivity() {

    private lateinit var settings: SettingsRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        settings = SettingsRepository(applicationContext)
        setContent {
            val shakeEnabled by settings.shakeEnabled.collectAsState(initial = true)
            val warningDays by settings.warningDays.collectAsState(initial = EXPIRING_SOON_DAYS)
            ShelfLifeTheme {
                SettingsScreen(
                    shakeEnabled = shakeEnabled,
                    warningDays = warningDays,
                    onShakeChange = { lifecycleScope.launch { settings.setShakeEnabled(it) } },
                    onWarningDaysChange = { lifecycleScope.launch { settings.setWarningDays(it) } },
                    onSaveLocation = ::saveCurrentLocation,
                    onBack = { finish() }
                )
            }
        }
    }

    // Only called once the location permission has been granted.
    @SuppressLint("MissingPermission")
    private fun saveCurrentLocation(onDone: (Boolean) -> Unit) {
        val manager = getSystemService(LocationManager::class.java)
        val provider = if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            LocationManager.NETWORK_PROVIDER
        } else {
            LocationManager.GPS_PROVIDER
        }
        LocationManagerCompat.getCurrentLocation(manager, provider, CancellationSignal(), ContextCompat.getMainExecutor(this)) { location ->
            if (location != null) {
                lifecycleScope.launch { settings.setSupermarket(location.latitude, location.longitude) }
            }
            onDone(location != null)
        }
    }
}
