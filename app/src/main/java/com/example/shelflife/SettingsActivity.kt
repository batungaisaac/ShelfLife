package com.example.shelflife

import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.shelflife.ui.SettingsScreen
import com.example.shelflife.ui.theme.ShelfLifeTheme

// Settings get their own activity rather than another route in the nav graph.
class SettingsActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT))
        setContent {
            ShelfLifeTheme {
                SettingsScreen(onBack = { finish() })
            }
        }
    }
}
