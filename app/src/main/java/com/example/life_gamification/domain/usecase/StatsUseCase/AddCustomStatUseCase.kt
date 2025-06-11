package com.example.life_gamification.domain.usecase.StatsUseCase



import com.example.life_gamification.data.local.entity.UserStatEntity
import com.example.life_gamification.domain.repository.UserStatsRepositories.UserStatRepository

class AddCustomStatUseCase(private val repository: UserStatRepository) {
    suspend operator fun invoke(userId: String, name: String, value: Int) {
        val stat = UserStatEntity(userId = userId, name = name, value = value)
        repository.addStat(stat)
    }
}