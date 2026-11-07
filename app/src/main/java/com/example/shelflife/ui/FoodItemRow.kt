package com.example.shelflife.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.shelflife.data.FoodItem
import com.example.shelflife.data.ItemStatus
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

@Composable
fun FoodItemRow(
    item: FoodItem,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val clickModifier = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    Surface(
        modifier = modifier.fillMaxWidth().then(clickModifier),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, style = MaterialTheme.typography.titleMedium)
                Text(
                    "${formatQuantity(item.quantityRemaining)} · ${item.location.label()}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            val days = daysUntil(item.expiryDate)
            Text(
                when (item.status) {
                    ItemStatus.ACTIVE -> expiryLabel(days)
                    ItemStatus.USED -> "Used"
                    ItemStatus.BINNED -> "Binned"
                },
                style = MaterialTheme.typography.labelLarge,
                color = when {
                    item.status != ItemStatus.ACTIVE -> MaterialTheme.colorScheme.onSurfaceVariant
                    days < 0 -> MaterialTheme.colorScheme.error
                    days <= EXPIRING_SOON_DAYS -> Color(0xFFE65100)
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

// Whole calendar days between today and the expiry date (negative if already expired).
fun daysUntil(date: Date): Long {
    fun startOfDay(millis: Long) = Calendar.getInstance().apply {
        timeInMillis = millis
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.timeInMillis
    val diff = startOfDay(date.time) - startOfDay(System.currentTimeMillis())
    return TimeUnit.MILLISECONDS.toDays(diff)
}

private fun expiryLabel(days: Long): String = when {
    days < -1 -> "Expired ${-days} days ago"
    days == -1L -> "Expired yesterday"
    days == 0L -> "Expires today"
    days == 1L -> "Expires tomorrow"
    else -> "$days days left"
}

fun formatQuantity(q: Double): String =
    if (q % 1.0 == 0.0) q.toLong().toString() else q.toString()

fun com.example.shelflife.data.StorageLocation.label(): String =
    name.lowercase().replaceFirstChar { it.uppercase() }
