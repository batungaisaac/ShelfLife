package com.example.shelflife.data

import kotlinx.coroutines.flow.Flow

class ShoppingListRepository(private val shoppingListDao: ShoppingListDao) {

    val all: Flow<List<ShoppingListItem>> = shoppingListDao.getAll()

    suspend fun insert(item: ShoppingListItem): Long = shoppingListDao.insert(item)

    suspend fun update(item: ShoppingListItem) = shoppingListDao.update(item)

    suspend fun deleteChecked() = shoppingListDao.deleteChecked()
}
