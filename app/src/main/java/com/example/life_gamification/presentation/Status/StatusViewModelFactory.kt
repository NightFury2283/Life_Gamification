package com.example.life_gamification.presentation.Status

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.life_gamification.data.local.db.AppDatabase
import com.example.life_gamification.data.repository.UserRepositoryImpl
import com.example.life_gamification.domain.repository.UserDailyRepositories.UserDailyRepositoryImpl
import com.example.life_gamification.domain.repository.UserStatsRepositories.UserStatRepositoryImpl
import com.example.life_gamification.domain.repository.UserTasksRepository.UserTaskRepositoryImpl
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
            val dailyRepoImpl = UserDailyRepositoryImpl(dailyDao)

            val taskDao = db.userTaskDao()
            val userTaskRepository = UserTaskRepositoryImpl(taskDao)



            val useCases = StatusUseCases(
                getCustomStat = GetCustomStatUseCase(statDao),
                getCustomStatList = GetCustomStatListUseCase(statDao),
                addCustomStat = AddCustomStatUseCase(statRepo),
                deleteCustomStat = DeleteCustomStatUseCase(statRepo),
                updateCustomStat = UpdateCustomStatUseCase(statRepo),
                getCustomDaily = GetCustomDailyUseCase(dailyDao),
                addCustomDaily = AddCustomDailyUseCase(dailyRepoImpl),
                deleteCustomDaily = DeleteCustomDailyUseCase(dailyRepoImpl),
                getCustomDailyList = GetCustomDailyListUseCase(dailyRepoImpl),
                updateDaily = UpdateDailyQuestUseCase(dailyRepoImpl)
            )

            return StatusViewModel(
                userId = userId,
                useCases = useCases,
                userRepository = userRepo,
                userTaskRepository = userTaskRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}