package com.example.life_gamification.presentation.status

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.life_gamification.data.local.db.AppDatabase
import com.example.life_gamification.data.repository.UserRepository
import com.example.life_gamification.data.repository.UserRepositoryImpl
import com.example.life_gamification.domain.repository.UserDailyRepositories.UserDailyRepositoryImpl
import com.example.life_gamification.domain.repository.UserStatsRepositories.UserStatRepository
import com.example.life_gamification.domain.repository.UserStatsRepositories.UserStatRepositoryImpl
import com.example.life_gamification.domain.usecase.DailyUseCase.AddCustomDailyUseCase
import com.example.life_gamification.domain.usecase.DailyUseCase.DeleteCustomDailyUseCase
import com.example.life_gamification.domain.usecase.DailyUseCase.GetCustomDailyUseCase
import com.example.life_gamification.domain.usecase.StatsUseCase.AddCustomStatUseCase
import com.example.life_gamification.domain.usecase.StatsUseCase.DeleteCustomStatUseCase
import com.example.life_gamification.domain.usecase.StatsUseCase.GetCustomStatUseCase
import com.example.life_gamification.domain.usecase.StatusUseCases

class StatusViewModelFactory(
    private val context: Context,
    private val userId: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatusViewModel::class.java)) {
            val db = AppDatabase.getDatabase(context)

            val userDao = db.userDao()
            val userRepo = UserRepositoryImpl(userDao)



            val statDao = db.userStatDao()
            val statRepo = UserStatRepositoryImpl(statDao)


            val dailyDao = db.userDailyDao()
            val dailyRepo = UserDailyRepositoryImpl(dailyDao)

            val useCases = StatusUseCases(
                getCustomStat = GetCustomStatUseCase(statDao),
                addCustomStat = AddCustomStatUseCase(statRepo),
                deleteCustomStat = DeleteCustomStatUseCase(statRepo),
                getCustomDaily = GetCustomDailyUseCase(dailyDao),
                addCustomDaily = AddCustomDailyUseCase(dailyRepo),
                deleteCustomDaily = DeleteCustomDailyUseCase(dailyRepo)
            )

            return StatusViewModel(
                userId = userId,
                useCases = useCases,
                userRepository = userRepo
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}