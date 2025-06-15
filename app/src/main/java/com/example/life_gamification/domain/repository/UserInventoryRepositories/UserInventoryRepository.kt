package com.example.life_gamification.domain.repository.UserInventoryRepositories

import com.example.life_gamification.data.local.entity.UserInventoryItemEntity

interface UserInventoryRepository {
    suspend fun getUserItems(userId: String): List<UserInventoryItemEntity>
    suspend fun addItem(item: UserInventoryItemEntity)
    suspend fun consumeItem(item: UserInventoryItemEntity)
    suspend fun getUserCoins(userId: String) : Int
}
