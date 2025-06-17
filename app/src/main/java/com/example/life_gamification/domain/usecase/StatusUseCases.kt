package com.example.life_gamification.domain.usecase

import com.example.life_gamification.data.local.entity.UserDailyQuestsEntity
import com.example.life_gamification.domain.usecase.DailyUseCase.AddCustomDailyUseCase
import com.example.life_gamification.domain.usecase.DailyUseCase.DeleteCustomDailyUseCase
import com.example.life_gamification.domain.usecase.DailyUseCase.GetCustomDailyListUseCase
import com.example.life_gamification.domain.usecase.DailyUseCase.GetCustomDailyUseCase
import com.example.life_gamification.domain.usecase.DailyUseCase.UpdateDailyQuestUseCase
import com.example.life_gamification.domain.usecase.StatsUseCase.AddCustomStatUseCase
import com.example.life_gamification.domain.usecase.StatsUseCase.DeleteCustomStatUseCase
import com.example.life_gamification.domain.usecase.StatsUseCase.GetCustomStatListUseCase
import com.example.life_gamification.domain.usecase.StatsUseCase.GetCustomStatUseCase
import com.example.life_gamification.domain.usecase.StatsUseCase.UpdateCustomStatUseCase

data class StatusUseCases(
    val getCustomStat: GetCustomStatUseCase,
    val getCustomStatList: GetCustomStatListUseCase,
    val addCustomStat: AddCustomStatUseCase,
    val deleteCustomStat: DeleteCustomStatUseCase,
    val updateCustomStat: UpdateCustomStatUseCase,
    val getCustomDaily: GetCustomDailyUseCase,
    val addCustomDaily: AddCustomDailyUseCase,
    val deleteCustomDaily: DeleteCustomDailyUseCase,
    val getCustomDailyList: GetCustomDailyListUseCase,
    val updateDaily: UpdateDailyQuestUseCase
)



