package com.example.shelflife.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shelflife.data.FoodItem
import com.example.shelflife.data.ItemStatus
import com.example.shelflife.data.StorageLocation

@Composable
fun HomeScreen(
    viewModel: FoodViewModel,
    onMark: (FoodItem, ItemStatus) -> Unit,
    onItemClick: (FoodItem) -> Unit,
    modifier: Modifier = Modifier
) {
    val items by viewModel.homeItems.collectAsStateWithLifecycle()
    val location by viewModel.locationFilter.collectAsStateWithLifecycle()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()

    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = viewModel::setSearchQuery,
            placeholder = { Text("Search by name") },
            leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            trailingIcon = {
                if (query.isNotEmpty()) {
                    IconButton(onClick = { viewModel.setSearchQuery("") }) {
                        Icon(Icons.Filled.Clear, contentDescription = "Clear search")
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LocationFilterRow(selected = location, onSelect = viewModel::setLocationFilter)

        if (items.isEmpty()) {
            val place = location
            EmptyMessage(
                when {
                    query.isNotBlank() -> "No items match \"${query.trim()}\""
                    place == null -> "Nothing in your kitchen yet"
                    else -> "Nothing in the ${place.label().lowercase()}"
                }
            )
            return@Column
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items, key = { it.id }) { item ->
                SwipeableFoodItemRow(item, onMark = onMark, onClick = { onItemClick(item) })
                HorizontalDivider()
            }
        }
    }
}

// "All" plus one chip per storage location.
@Composable
private fun LocationFilterRow(
    selected: StorageLocation?,
    onSelect: (StorageLocation?) -> Unit
) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(selected = selected == null, onClick = { onSelect(null) }, label = { Text("All") })
        StorageLocation.entries.forEach { option ->
            FilterChip(
                selected = selected == option,
                onClick = { onSelect(option) },
                label = { Text(option.label()) }
            )
        }
    }
}

@Composable
fun EmptyMessage(text: String, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text)
    }
}
