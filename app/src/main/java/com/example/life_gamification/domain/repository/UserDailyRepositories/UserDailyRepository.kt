package com.example.life_gamification.domain.repository.UserDailyRepositories

import com.example.life_gamification.data.local.entity.UserDailyQuestsEntity

interface UserDailyRepository {
    suspend fun getDailyQuests(userId: String): List<UserDailyQuestsEntity>
    suspend fun addDailyQuest(stat: UserDailyQuestsEntity)
    suspend fun deleteDailyQuest(stat: UserDailyQuestsEntity)
    suspend fun updateDailyQuest(stat: UserDailyQuestsEntity)
}