package com.example.shelflife

import android.os.Bundle
import android.graphics.Color
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.shelflife.sensor.ShakeDetector
import com.example.shelflife.ui.FoodViewModel
import com.example.shelflife.ui.ShelfLifeApp
import com.example.shelflife.ui.theme.ShelfLifeTheme
import kotlinx.coroutines.flow.MutableSharedFlow

class MainActivity : ComponentActivity() {

    private val viewModel: FoodViewModel by viewModels()

    // Gets one event per shake, passed down to the app below.
    private val shakes = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Demo items on first launch, so the lists aren't empty.
        viewModel.insertSampleItemsIfEmpty()

        // Starts on resume and stops on pause, so the sensor isn't
        // draining the battery while the app is in the background.
        val shakeDetector = ShakeDetector(this, onShake = { shakes.tryEmit(Unit) })
        // No accelerometer means no shake shortcut, the rest of the app works the same.
        if (shakeDetector.isAvailable) lifecycle.addObserver(shakeDetector)

        // The top bar is always navy, so keep the status bar icons white.
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        setContent {
            ShelfLifeTheme {
                ShelfLifeApp(viewModel, shakes = shakes)
            }
        }
    }
}
