package com.example.life_gamification.domain.usecase.DailyUseCase


import com.example.life_gamification.data.local.entity.UserDailyQuestsEntity
import com.example.life_gamification.domain.repository.UserDailyRepositories.UserDailyRepository

class GetCustomDailyListUseCase(private val repository: UserDailyRepository) {
    suspend operator fun invoke(userId: String): List<UserDailyQuestsEntity> {
        return repository.getDailyQuests(userId)
    }
}
