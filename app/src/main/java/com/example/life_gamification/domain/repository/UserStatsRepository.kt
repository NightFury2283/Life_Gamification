package com.example.life_gamification.domain.repository

import com.example.life_gamification.data.local.entity.UserStatsEntity

interface UserStatsRepository {
    suspend fun getStats(userId: String): UserStatsEntity?
    suspend fun updateStats(stats: UserStatsEntity)
}
