package com.example.shelflife.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.shelflife.data.FoodItem
import com.example.shelflife.data.ItemStatus

private val UsedColor = Color(0xFF2E7D32)
private val BinnedColor = Color(0xFFC62828)

// Swipe right (start to end) marks the item Used, swipe left marks it Binned.
@Composable
fun SwipeableFoodItemRow(
    item: FoodItem,
    onMark: (FoodItem, ItemStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState()

    // No reset needed: once marked, the item is no longer Active and leaves the list.
    LaunchedEffect(dismissState.currentValue) {
        when (dismissState.currentValue) {
            SwipeToDismissBoxValue.StartToEnd -> onMark(item, ItemStatus.USED)
            SwipeToDismissBoxValue.EndToStart -> onMark(item, ItemStatus.BINNED)
            SwipeToDismissBoxValue.Settled -> {}
        }
    }

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier,
        gesturesEnabled = item.status == ItemStatus.ACTIVE,
        backgroundContent = {
            when (dismissState.dismissDirection) {
                SwipeToDismissBoxValue.StartToEnd ->
                    SwipeBackground(UsedColor, Arrangement.Start) {
                        Icon(Icons.Filled.Check, contentDescription = null, tint = Color.White)
                        Text("  Used", color = Color.White)
                    }
                SwipeToDismissBoxValue.EndToStart ->
                    SwipeBackground(BinnedColor, Arrangement.End) {
                        Text("Binned  ", color = Color.White)
                        Icon(Icons.Filled.Delete, contentDescription = null, tint = Color.White)
                    }
                SwipeToDismissBoxValue.Settled -> {}
            }
        }
    ) {
        FoodItemRow(item)
    }
}

@Composable
private fun SwipeBackground(
    color: Color,
    arrangement: Arrangement.Horizontal,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(horizontal = 24.dp),
        horizontalArrangement = arrangement,
        verticalAlignment = Alignment.CenterVertically
    ) { content() }
}
