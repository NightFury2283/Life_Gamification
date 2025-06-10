package com.example.life_gamification.domain.usecase



import com.example.life_gamification.data.local.dao.UserStatDao
import com.example.life_gamification.data.local.entity.UserStatEntity
import kotlinx.coroutines.flow.Flow

class GetCustomStatUseCase(
    private val dao: UserStatDao
) {
    operator fun invoke(userId: String): Flow<List<UserStatEntity>> {
        return dao.getStatsFlow(userId)
    }
}
