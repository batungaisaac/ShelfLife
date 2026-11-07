package com.example.shelflife.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.shelflife.data.AppDatabase
import com.example.shelflife.data.FoodRepository
import com.example.shelflife.data.ItemStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar
import java.util.Date

data class MonthStats(val used: Int = 0, val binned: Int = 0) {
    val total: Int get() = used + binned
}

class WasteStatsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FoodRepository(
        AppDatabase.getDatabase(application).foodItemDao()
    )

    // Midnight on the 1st of the current month.
    val monthStart: Date = Calendar.getInstance().apply {
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
    }.time

    val thisMonth: StateFlow<MonthStats> = combine(
        repository.countResolvedSince(ItemStatus.USED, monthStart),
        repository.countResolvedSince(ItemStatus.BINNED, monthStart)
    ) { used, binned -> MonthStats(used, binned) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), MonthStats())
}
