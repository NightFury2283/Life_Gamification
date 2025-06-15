package com.example.life_gamification.domain.repository.UserInventoryRepositories

import com.example.life_gamification.data.local.dao.UserInventoryDao
import com.example.life_gamification.data.local.entity.UserInventoryItemEntity

class UserInventoryRepositoryImpl(private val dao: UserInventoryDao) : UserInventoryRepository {
    override suspend fun getUserItems(userId: String) = dao.getAllItemsForUser(userId)
    override suspend fun addItem(item: UserInventoryItemEntity) = dao.insertItem(item)
    override suspend fun consumeItem(item: UserInventoryItemEntity) {
        dao.updateItem(item.copy(isConsumed = true))
    }
    override suspend fun getUserCoins(userId: String): Int {
        return dao.getUserCoins(userId)
    }
}