package com.example.shelflife.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_list_items")
data class ShoppingListItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val isChecked: Boolean = false
)
