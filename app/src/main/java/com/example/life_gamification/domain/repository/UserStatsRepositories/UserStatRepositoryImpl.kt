package com.example.life_gamification.domain.repository.UserStatsRepositories

import com.example.life_gamification.data.local.dao.UserStatDao
import com.example.life_gamification.data.local.entity.UserStatEntity

class UserStatRepositoryImpl(private val dao: UserStatDao) : UserStatRepository {
    override suspend fun getStats(userId: String): List<UserStatEntity> = dao.getStats(userId)
    override suspend fun addStat(stat: UserStatEntity) = dao.addStat(stat)
    override suspend fun deleteStat(stat: UserStatEntity) = dao.deleteStat(stat)
    override suspend fun updateStat(stat: UserStatEntity) = dao.updateStat(stat)
}