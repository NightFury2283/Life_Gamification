package com.example.life_gamification.domain.repository

import com.example.life_gamification.data.local.dao.UserStatsDao
import com.example.life_gamification.data.local.entity.UserStatsEntity

class UserStatsRepositoryImpl(
    private val userStatsDao: UserStatsDao
) : UserStatsRepository {
    override suspend fun getStats(userId: String): UserStatsEntity? {
        return userStatsDao.getStats(userId)
    }

    override suspend fun updateStats(stats: UserStatsEntity) {
        userStatsDao.updateStats(stats)
    }
}
