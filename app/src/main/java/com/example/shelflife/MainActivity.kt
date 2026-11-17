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

class MainActivity : ComponentActivity() {

    private val viewModel: FoodViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Demo items on first launch, so the lists aren't empty.
        viewModel.insertSampleItemsIfEmpty()

        // Starts on resume and stops on pause, so the sensor isn't
        // draining the battery while the app is in the background.
        // A shake doesn't do anything yet.
        val shakeDetector = ShakeDetector(this, onShake = {})
        lifecycle.addObserver(shakeDetector)

        // The top bar is always navy, so keep the status bar icons white.
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        setContent {
            ShelfLifeTheme {
                ShelfLifeApp(viewModel)
            }
        }
    }
}
