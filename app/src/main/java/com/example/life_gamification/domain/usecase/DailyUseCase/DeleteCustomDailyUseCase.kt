package com.example.life_gamification.domain.usecase.DailyUseCase

import com.example.life_gamification.data.local.entity.UserDailyQuestsEntity
import com.example.life_gamification.domain.repository.UserDailyRepositories.UserDailyRepository

class DeleteCustomDailyUseCase(private val repository: UserDailyRepository) {
    suspend operator fun invoke(daily: UserDailyQuestsEntity) {
        repository.deleteDailyQuest(daily)
    }
}