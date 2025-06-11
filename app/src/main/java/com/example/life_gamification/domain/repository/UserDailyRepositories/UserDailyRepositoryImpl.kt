package com.example.life_gamification.domain.repository.UserDailyRepositories

import com.example.life_gamification.data.local.dao.UserDailyQuestsDao
import com.example.life_gamification.data.local.entity.UserDailyQuestsEntity

class UserDailyRepositoryImpl(private val dao: UserDailyQuestsDao) : UserDailyRepository {
    override suspend fun getDailyQuests(userId: String): List<UserDailyQuestsEntity> = dao.getDailyQuests(userId)
    override suspend fun addDailyQuest(daily: UserDailyQuestsEntity) = dao.addDailyQuest(daily)
    override suspend fun deleteDailyQuest(daily: UserDailyQuestsEntity) = dao.deleteDailyQuest(daily)
    override suspend fun updateDailyQuest(daily: UserDailyQuestsEntity) = dao.updateDailyQuest(daily)
}