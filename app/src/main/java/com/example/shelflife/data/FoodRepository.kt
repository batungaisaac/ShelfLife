package com.example.shelflife.data

import kotlinx.coroutines.flow.Flow

class FoodRepository(private val foodItemDao: FoodItemDao) {

    val allActive: Flow<List<FoodItem>> = foodItemDao.getAllActive()

    fun expiringWithin(days: Int): Flow<List<FoodItem>> = foodItemDao.getExpiringWithin(days)

    fun byStatus(status: ItemStatus): Flow<List<FoodItem>> = foodItemDao.getByStatus(status)

    fun search(query: String): Flow<List<FoodItem>> = foodItemDao.searchByName(query)

    suspend fun insert(item: FoodItem): Long = foodItemDao.insert(item)

    suspend fun update(item: FoodItem) = foodItemDao.update(item)

    suspend fun delete(item: FoodItem) = foodItemDao.delete(item)
}
