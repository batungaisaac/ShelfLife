package com.example.shelflife.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shelflife.data.Supermarket
import com.example.shelflife.ui.theme.Navy
import com.example.shelflife.ui.theme.White
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    shakeEnabled: Boolean,
    warningDays: Int,
    onShakeChange: (Boolean) -> Unit,
    onWarningDaysChange: (Int) -> Unit,
    supermarket: Supermarket?,
    onSaveLocation: (onDone: (Boolean) -> Unit) -> Unit,
    onClearSupermarket: () -> Unit,
    onBack: () -> Unit
) {
    var locationMessage by remember { mutableStateOf<String?>(null) }
    val askForLocation = rememberLocationPermission { granted ->
        if (granted) {
            onSaveLocation { saved ->
                locationMessage = if (saved) "Supermarket saved." else "Couldn't get your location. Check that location is on."
            }
        } else {
            locationMessage = "Without location access, ShelfLife can't save your supermarket."
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Navy,
                    titleContentColor = White,
                    navigationIconContentColor = White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Shake to open Expiring Soon", modifier = Modifier.weight(1f))
                Switch(checked = shakeEnabled, onCheckedChange = onShakeChange)
            }
            Text("Warn me $warningDays days before an item expires")
            Slider(
                value = warningDays.toFloat(),
                onValueChange = { onWarningDaysChange(it.roundToInt()) },
                valueRange = 1f..14f,
                steps = 12
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                val place = supermarket?.let { "%.4f, %.4f".format(it.latitude, it.longitude) } ?: "Not set"
                Text("Supermarket: $place", modifier = Modifier.weight(1f))
                if (supermarket != null) {
                    TextButton(onClick = { locationMessage = null; onClearSupermarket() }) { Text("Clear") }
                }
            }
            Button(onClick = askForLocation) { Text("Use my current location") }
            locationMessage?.let { Text(it) }
        }
    }
}
