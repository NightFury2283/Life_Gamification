package com.example.life_gamification.domain.usecase.StatsUseCase


import com.example.life_gamification.data.local.entity.UserStatEntity
import com.example.life_gamification.domain.repository.UserStatsRepositories.UserStatRepository

class UpdateCustomStatUseCase(private val repository: UserStatRepository) {
    suspend operator fun invoke(stat: UserStatEntity) {
        repository.updateStat(stat)
    }
}
