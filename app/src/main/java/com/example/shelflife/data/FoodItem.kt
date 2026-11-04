package com.example.shelflife.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

enum class StorageLocation { FRIDGE, FREEZER, CUPBOARD }

enum class ItemStatus { ACTIVE, USED, BINNED }

@Entity(tableName = "food_items")
data class FoodItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val quantity: Double,
    val quantityRemaining: Double = quantity,
    val location: StorageLocation,
    val expiryDate: Date,
    val status: ItemStatus = ItemStatus.ACTIVE,
    val dateResolved: Date? = null,
    val notes: String? = null,
    val photoPath: String? = null,
    val price: Double? = null
)
