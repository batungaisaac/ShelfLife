package com.example.shelflife.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun ExpiringSoonScreen(viewModel: FoodViewModel, modifier: Modifier = Modifier) {
    val items by viewModel.expiringSoonItems.collectAsStateWithLifecycle()

    if (items.isEmpty()) {
        EmptyMessage("Nothing expiring in the next $EXPIRING_SOON_DAYS days", modifier)
        return
    }

    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(items, key = { it.id }) { item ->
            FoodItemRow(item)
            HorizontalDivider()
        }
    }
}
