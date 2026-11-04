package com.example.shelflife.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.shelflife.data.AppDatabase
import com.example.shelflife.data.FoodItem
import com.example.shelflife.data.FoodRepository
import com.example.shelflife.data.StorageLocation
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.TimeUnit

class FoodViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FoodRepository(
        AppDatabase.getDatabase(application).foodItemDao()
    )

    val activeItems: StateFlow<List<FoodItem>> = repository.allActive
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun insert(item: FoodItem) = viewModelScope.launch {
        repository.insert(item)
    }

    // Temporary: used to test the database until the Add Item screen exists.
    fun insertDummyItemIfEmpty() = viewModelScope.launch {
        if (repository.allActive.first().isEmpty()) {
            repository.insert(
                FoodItem(
                    name = "Milk",
                    quantity = 1.0,
                    location = StorageLocation.FRIDGE,
                    expiryDate = Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(3)),
                    price = 120.0
                )
            )
        }
    }
}
