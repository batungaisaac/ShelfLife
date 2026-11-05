package com.example.shelflife.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.shelflife.data.AppDatabase
import com.example.shelflife.data.FoodItem
import com.example.shelflife.data.FoodRepository
import com.example.shelflife.data.ItemStatus
import com.example.shelflife.data.StorageLocation
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.TimeUnit

// Items expiring within this many days show up on the Expiring Soon screen.
const val EXPIRING_SOON_DAYS = 3

class FoodViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FoodRepository(
        AppDatabase.getDatabase(application).foodItemDao()
    )

    val homeItems: StateFlow<List<FoodItem>> = repository.all
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val expiringSoonItems: StateFlow<List<FoodItem>> = repository.all
        .map { items ->
            val cutoff = System.currentTimeMillis() + TimeUnit.DAYS.toMillis(EXPIRING_SOON_DAYS.toLong())
            items.filter { it.expiryDate.time <= cutoff }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun insert(item: FoodItem) = viewModelScope.launch {
        repository.insert(item)
    }

    // Marks an item as used or binned. The row is kept (not deleted) so it can feed
    // waste/usage stats later; dateResolved records when it left the kitchen.
    fun markItem(item: FoodItem, status: ItemStatus) = viewModelScope.launch {
        repository.update(item.copy(status = status, dateResolved = Date()))
    }

    // Temporary: seeds a few items so the lists have something to show
    // until the Add Item screen exists.
    fun insertSampleItemsIfEmpty() = viewModelScope.launch {
        if (repository.all.first().isNotEmpty()) return@launch
        val now = System.currentTimeMillis()
        fun inDays(days: Int) = Date(now + TimeUnit.DAYS.toMillis(days.toLong()))
        listOf(
            FoodItem(name = "Milk", quantity = 1.0, location = StorageLocation.FRIDGE, expiryDate = inDays(2), price = 120.0),
            FoodItem(name = "Spinach", quantity = 1.0, location = StorageLocation.FRIDGE, expiryDate = inDays(-1), price = 50.0),
            FoodItem(name = "Eggs", quantity = 12.0, location = StorageLocation.FRIDGE, expiryDate = inDays(10), price = 420.0),
            FoodItem(name = "Frozen peas", quantity = 1.0, location = StorageLocation.FREEZER, expiryDate = inDays(90), price = 250.0),
            FoodItem(name = "Rice", quantity = 2.0, location = StorageLocation.CUPBOARD, expiryDate = inDays(180), price = 300.0)
        ).forEach { repository.insert(it) }
    }
}
