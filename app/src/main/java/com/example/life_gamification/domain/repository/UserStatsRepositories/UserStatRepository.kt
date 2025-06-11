package com.example.life_gamification.domain.repository.UserStatsRepositories

import com.example.life_gamification.data.local.entity.UserStatEntity

interface UserStatRepository {
    suspend fun getStats(userId: String): List<UserStatEntity>
    suspend fun addStat(stat: UserStatEntity)
    suspend fun deleteStat(stat: UserStatEntity)
    suspend fun updateStat(stat: UserStatEntity)
}