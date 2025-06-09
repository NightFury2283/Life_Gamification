package com.example.life_gamification.domain.usecase

import com.example.life_gamification.data.local.entity.UserStatsEntity
import com.example.life_gamification.domain.repository.UserStatsRepository

class UserStatsUseCase(private val repository: UserStatsRepository) {
    suspend fun getUserStats(userId: String): UserStatsEntity? = repository.getStats(userId)
    suspend fun updateUserStats(stats: UserStatsEntity) = repository.updateStats(stats)
}
