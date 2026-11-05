package com.example.shelflife.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun HomeScreen(viewModel: FoodViewModel, modifier: Modifier = Modifier) {
    val items by viewModel.homeItems.collectAsStateWithLifecycle()

    if (items.isEmpty()) {
        EmptyMessage("Nothing in your kitchen yet", modifier)
        return
    }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(items, key = { it.id }) { item ->
            FoodItemRow(item)
            HorizontalDivider()
        }
    }
}

@Composable
fun EmptyMessage(text: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text)
    }
}
