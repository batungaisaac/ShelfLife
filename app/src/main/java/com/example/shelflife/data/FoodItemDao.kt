package com.example.shelflife.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import java.util.Date

@Dao
interface FoodItemDao {

    @Insert
    suspend fun insert(item: FoodItem): Long

    @Update
    suspend fun update(item: FoodItem)

    @Delete
    suspend fun delete(item: FoodItem)

    @Query("SELECT * FROM food_items WHERE id = :id")
    suspend fun getById(id: Long): FoodItem?

    @Query("SELECT * FROM food_items ORDER BY expiryDate ASC")
    fun getAll(): Flow<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE status = 'ACTIVE' ORDER BY expiryDate ASC")
    fun getAllActive(): Flow<List<FoodItem>>

    // Active items expiring between now and N days from now (already expired items included).
    // expiryDate is stored in milliseconds, strftime('%s') gives seconds.
    @Query(
        """
        SELECT * FROM food_items
        WHERE status = 'ACTIVE'
          AND expiryDate <= (strftime('%s', 'now') + :days * 86400) * 1000
        ORDER BY expiryDate ASC
        """
    )
    fun getExpiringWithin(days: Int): Flow<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE status = :status ORDER BY dateResolved DESC")
    fun getByStatus(status: ItemStatus): Flow<List<FoodItem>>

    // Items marked with this status on or after the given date.
    @Query("SELECT COUNT(*) FROM food_items WHERE status = :status AND dateResolved >= :since")
    fun countResolvedSince(status: ItemStatus, since: Date): Flow<Int>

    @Query("SELECT * FROM food_items WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchByName(query: String): Flow<List<FoodItem>>
}
