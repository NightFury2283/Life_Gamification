package com.example.life_gamification.domain.usecase.DailyUseCase

import com.example.life_gamification.data.local.dao.UserDailyQuestsDao
import com.example.life_gamification.data.local.entity.UserDailyQuestsEntity
import kotlinx.coroutines.flow.Flow

class GetCustomDailyUseCase(
    private val dao: UserDailyQuestsDao
) {
    operator fun invoke(userId: String): Flow<List<UserDailyQuestsEntity>> {
        return dao.getDailyQuestsFlow(userId)
    }
}