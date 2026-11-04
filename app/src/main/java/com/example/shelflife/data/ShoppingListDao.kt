package com.example.shelflife.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ShoppingListDao {

    @Insert
    suspend fun insert(item: ShoppingListItem): Long

    @Update
    suspend fun update(item: ShoppingListItem)

    @Delete
    suspend fun delete(item: ShoppingListItem)

    // Unchecked items first
    @Query("SELECT * FROM shopping_list_items ORDER BY isChecked ASC, name ASC")
    fun getAll(): Flow<List<ShoppingListItem>>
}
