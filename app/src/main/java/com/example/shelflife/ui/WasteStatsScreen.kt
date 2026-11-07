package com.example.shelflife.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Locale

private val UsedColor = Color(0xFF2E7D32)
private val BinnedColor = Color(0xFFC62828)

@Composable
fun WasteStatsScreen(viewModel: WasteStatsViewModel, modifier: Modifier = Modifier) {
    val stats by viewModel.thisMonth.collectAsStateWithLifecycle()
    val month = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(viewModel.monthStart)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(month, style = MaterialTheme.typography.titleLarge)

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            CountCard("Used", stats.used, UsedColor, Modifier.weight(1f))
            CountCard("Binned", stats.binned, BinnedColor, Modifier.weight(1f))
        }

        if (stats.total == 0) {
            Text(
                "Nothing used or binned yet this month.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            return@Column
        }

        // One bar split into used (green) and binned (red).
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(24.dp)
                .clip(RoundedCornerShape(12.dp))
        ) {
            if (stats.used > 0) {
                Box(Modifier.weight(stats.used.toFloat()).fillMaxHeight().background(UsedColor))
            }
            if (stats.binned > 0) {
                Box(Modifier.weight(stats.binned.toFloat()).fillMaxHeight().background(BinnedColor))
            }
        }

        val wastedPercent = stats.binned * 100 / stats.total
        Text(
            "$wastedPercent% of what left your kitchen this month was binned.",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun CountCard(label: String, count: Int, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(label, style = MaterialTheme.typography.labelLarge)
            Text(count.toString(), style = MaterialTheme.typography.displayMedium, color = color)
        }
    }
}
