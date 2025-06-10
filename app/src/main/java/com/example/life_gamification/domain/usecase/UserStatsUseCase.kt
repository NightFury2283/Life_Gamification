package com.example.life_gamification.domain.usecase

import com.example.life_gamification.data.local.entity.UserStatEntity
import com.example.life_gamification.domain.repository.UserStatRepository

class UserStatsUseCase(private val repository: UserStatRepository) {
    suspend fun getUserStats(userId: String): List<UserStatEntity> = repository.getStats(userId)
    suspend fun updateUserStats(stats: UserStatEntity) = repository.updateStat(stats)
}
