package com.example.life_gamification.domain.usecase.DailyUseCase

import com.example.life_gamification.data.local.entity.UserDailyQuestsEntity
import com.example.life_gamification.domain.repository.UserDailyRepositories.UserDailyRepository

class AddCustomDailyUseCase(private val repository: UserDailyRepository) {
    suspend operator fun invoke(userId: String, name: String, value: Int) {
        val daily = UserDailyQuestsEntity(userId = userId, name = name, addXp = value)
        repository.addDailyQuest(daily)
    }
}