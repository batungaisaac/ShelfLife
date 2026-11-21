package com.example.shelflife.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

private val permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)

// Returns a function that asks for location, explaining why first. onResult(true) if granted.
@Composable
fun rememberLocationPermission(onResult: (Boolean) -> Unit): () -> Unit {
    val context = LocalContext.current
    var showRationale by remember { mutableStateOf(false) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        onResult(it.values.any { granted -> granted })
    }
    if (showRationale) {
        AlertDialog(
            onDismissRequest = { showRationale = false },
            title = { Text("Use your location?") },
            text = { Text("ShelfLife uses your location once to save where your supermarket is. It isn't tracked or shared.") },
            confirmButton = {
                TextButton(onClick = { showRationale = false; launcher.launch(permissions) }) { Text("Continue") }
            },
            dismissButton = {
                TextButton(onClick = { showRationale = false; onResult(false) }) { Text("Not now") }
            }
        )
    }
    return {
        val granted = permissions.any {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        if (granted) onResult(true) else showRationale = true
    }
}
