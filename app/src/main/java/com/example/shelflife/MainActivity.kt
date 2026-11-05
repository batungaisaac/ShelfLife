package com.example.shelflife

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.shelflife.ui.FoodViewModel
import com.example.shelflife.ui.ShelfLifeApp
import com.example.shelflife.ui.theme.ShelfLifeTheme

class MainActivity : ComponentActivity() {

    private val viewModel: FoodViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Temporary until the Add Item screen exists.
        viewModel.insertSampleItemsIfEmpty()

        enableEdgeToEdge()
        setContent {
            ShelfLifeTheme {
                ShelfLifeApp(viewModel)
            }
        }
    }
}
