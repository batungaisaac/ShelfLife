package com.example.shelflife.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.shelflife.data.AppDatabase
import com.example.shelflife.data.FoodItem
import com.example.shelflife.data.FoodRepository
import com.example.shelflife.data.ItemStatus
import com.example.shelflife.data.StorageLocation
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.TimeUnit

// Items expiring within this many days show up on the Expiring Soon screen.
const val EXPIRING_SOON_DAYS = 3

@OptIn(ExperimentalCoroutinesApi::class)
class FoodViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = FoodRepository(
        AppDatabase.getDatabase(application).foodItemDao()
    )

    // null means "All locations".
    private val _locationFilter = MutableStateFlow<StorageLocation?>(null)
    val locationFilter: StateFlow<StorageLocation?> = _locationFilter.asStateFlow()

    fun setLocationFilter(location: StorageLocation?) {
        _locationFilter.value = location
    }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Blank search shows everything; otherwise the DAO's name search, kept to Active items.
    private val activeMatchingSearch = _searchQuery.flatMapLatest { query ->
        if (query.isBlank()) repository.allActive
        else repository.search(query.trim()).map { items -> items.filter { it.status == ItemStatus.ACTIVE } }
    }

    // Used and binned items stay in the database but drop off these lists.
    val homeItems: StateFlow<List<FoodItem>> = activeMatchingSearch
        .combine(_locationFilter) { items, location ->
            if (location == null) items else items.filter { it.location == location }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val expiringSoonItems: StateFlow<List<FoodItem>> = repository.expiringWithin(EXPIRING_SOON_DAYS)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun insert(item: FoodItem) = viewModelScope.launch {
        repository.insert(item)
    }

    suspend fun getItem(id: Long): FoodItem? = repository.getById(id)

    // New items have id 0 (Room assigns one on insert); anything else is an edit.
    fun save(item: FoodItem) = viewModelScope.launch {
        if (item.id == 0L) repository.insert(item) else repository.update(item)
    }

    // Marks an item as used or binned. The row is kept (not deleted) so it can feed
    // waste/usage stats later; dateResolved records when it left the kitchen.
    fun markItem(
        item: FoodItem,
        status: ItemStatus,
        quantityRemaining: Double = item.quantityRemaining
    ) = viewModelScope.launch {
        repository.update(
            item.copy(status = status, dateResolved = Date(), quantityRemaining = quantityRemaining)
        )
    }

    // Lowers what's left of an item. Using the last of it goes through markItem instead.
    fun useSome(item: FoodItem, amount: Double) = viewModelScope.launch {
        repository.update(item.copy(quantityRemaining = item.quantityRemaining - amount))
    }

    // Restores an item exactly as it was before it was marked (Active, no dateResolved).
    fun undoMark(original: FoodItem) = viewModelScope.launch {
        repository.update(original)
    }

    // Seeds demo items on first launch so every expiry state shows up on Home:
    // expired, today, tomorrow, soon, and plenty of time left, across all three locations.
    fun insertSampleItemsIfEmpty() = viewModelScope.launch {
        if (repository.all.first().isNotEmpty()) return@launch
        val now = System.currentTimeMillis()
        fun inDays(days: Int) = Date(now + TimeUnit.DAYS.toMillis(days.toLong()))
        listOf(
            FoodItem(name = "Milk", quantity = 1.0, location = StorageLocation.FRIDGE, expiryDate = inDays(0), price = 120.0),
            FoodItem(name = "Yoghurt", quantity = 4.0, location = StorageLocation.FRIDGE, expiryDate = inDays(1), price = 200.0),
            FoodItem(name = "Spinach", quantity = 1.0, location = StorageLocation.FRIDGE, expiryDate = inDays(-2), price = 50.0),
            FoodItem(name = "Bread", quantity = 1.0, location = StorageLocation.CUPBOARD, expiryDate = inDays(3), price = 65.0),
            FoodItem(name = "Frozen peas", quantity = 1.0, location = StorageLocation.FREEZER, expiryDate = inDays(90), price = 250.0),
            FoodItem(name = "Rice", quantity = 2.0, location = StorageLocation.CUPBOARD, expiryDate = inDays(180), price = 300.0)
        ).forEach { repository.insert(it) }
    }
}
