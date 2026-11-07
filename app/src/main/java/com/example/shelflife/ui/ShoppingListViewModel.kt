package com.example.shelflife.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.shelflife.data.AppDatabase
import com.example.shelflife.data.ShoppingListItem
import com.example.shelflife.data.ShoppingListRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShoppingListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = ShoppingListRepository(
        AppDatabase.getDatabase(application).shoppingListDao()
    )

    // Unticked items first, then ticked ones (ordering comes from the DAO).
    val items: StateFlow<List<ShoppingListItem>> = repository.all
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun add(name: String) = viewModelScope.launch {
        val trimmed = name.trim()
        if (trimmed.isNotEmpty()) repository.insert(ShoppingListItem(name = trimmed))
    }

    fun setChecked(item: ShoppingListItem, checked: Boolean) = viewModelScope.launch {
        repository.update(item.copy(isChecked = checked))
    }

    fun clearTicked() = viewModelScope.launch {
        repository.deleteChecked()
    }
}
