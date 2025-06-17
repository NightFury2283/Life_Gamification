package com.example.life_gamification.domain.usecase.StatsUseCase



import com.example.life_gamification.data.local.dao.UserStatDao
import com.example.life_gamification.data.local.entity.UserStatEntity

class GetCustomStatListUseCase(
    private val dao: UserStatDao
) {
    suspend operator fun invoke(userId: String): List<UserStatEntity> {
        return dao.getStats(userId)
    }
}
