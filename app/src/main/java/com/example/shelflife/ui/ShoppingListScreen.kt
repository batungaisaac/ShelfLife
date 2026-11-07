package com.example.shelflife.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Checkbox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.shelflife.data.ShoppingListItem

@Composable
fun ShoppingListScreen(viewModel: ShoppingListViewModel, modifier: Modifier = Modifier) {
    val items by viewModel.items.collectAsStateWithLifecycle()
    var newItem by rememberSaveable { mutableStateOf("") }

    fun addItem() {
        if (newItem.isNotBlank()) {
            viewModel.add(newItem)
            newItem = ""
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = newItem,
                onValueChange = { newItem = it },
                placeholder = { Text("Add to shopping list") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { addItem() }),
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = ::addItem, enabled = newItem.isNotBlank()) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        }

        if (items.isEmpty()) {
            EmptyMessage("Your shopping list is empty")
            return@Column
        }

        if (items.any { it.isChecked }) {
            TextButton(
                onClick = { viewModel.clearTicked() },
                modifier = Modifier.align(Alignment.End).padding(end = 8.dp)
            ) { Text("Clear ticked") }
        }

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(items, key = { it.id }) { item ->
                ShoppingListRow(item, onCheckedChange = { viewModel.setChecked(item, it) })
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun ShoppingListRow(item: ShoppingListItem, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!item.isChecked) }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Checkbox(checked = item.isChecked, onCheckedChange = onCheckedChange)
        Text(
            item.name,
            style = MaterialTheme.typography.bodyLarge,
            textDecoration = if (item.isChecked) TextDecoration.LineThrough else null,
            color = if (item.isChecked) MaterialTheme.colorScheme.onSurfaceVariant
            else MaterialTheme.colorScheme.onSurface
        )
    }
}
