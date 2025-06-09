package com.example.life_gamification.domain.usecase

import com.example.life_gamification.domain.repository.UserStatsRepository

class AddStatUseCase(private val repo: UserStatsRepository) {
    suspend operator fun invoke(userId: String, stat: StatType, amount: Int) {
        val stats = repo.getStats(userId) ?: return
        val updated = when (stat) {
            StatType.HEALTH -> stats.copy(health = stats.health + amount)
            StatType.INTELLECT -> stats.copy(intellect = stats.intellect + amount)
            StatType.PRODUCTIVITY -> stats.copy(productivity = stats.productivity + amount)
        }
        repo.updateStats(updated)
    }
}

enum class StatType {
    HEALTH, INTELLECT, PRODUCTIVITY
}
