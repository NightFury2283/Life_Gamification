package com.example.life_gamification.domain.usecase

import com.example.life_gamification.domain.usecase.DailyUseCase.AddCustomDailyUseCase
import com.example.life_gamification.domain.usecase.DailyUseCase.DeleteCustomDailyUseCase
import com.example.life_gamification.domain.usecase.DailyUseCase.GetCustomDailyUseCase
import com.example.life_gamification.domain.usecase.StatsUseCase.AddCustomStatUseCase
import com.example.life_gamification.domain.usecase.StatsUseCase.DeleteCustomStatUseCase
import com.example.life_gamification.domain.usecase.StatsUseCase.GetCustomStatUseCase

data class StatusUseCases(
    val getCustomStat: GetCustomStatUseCase,
    val addCustomStat: AddCustomStatUseCase,
    val deleteCustomStat: DeleteCustomStatUseCase,
    val getCustomDaily: GetCustomDailyUseCase,
    val addCustomDaily: AddCustomDailyUseCase,
    val deleteCustomDaily: DeleteCustomDailyUseCase
)
