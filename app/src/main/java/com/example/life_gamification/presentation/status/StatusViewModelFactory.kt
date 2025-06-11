package com.example.life_gamification.presentation.status

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.life_gamification.data.local.db.AppDatabase
import com.example.life_gamification.data.repository.UserRepository
import com.example.life_gamification.data.repository.UserRepositoryImpl
import com.example.life_gamification.domain.repository.UserStatsRepositories.UserStatRepository
import com.example.life_gamification.domain.repository.UserStatsRepositories.UserStatRepositoryImpl
import com.example.life_gamification.domain.usecase.StatsUseCase.AddCustomStatUseCase
import com.example.life_gamification.domain.usecase.StatsUseCase.DeleteCustomStatUseCase
import com.example.life_gamification.domain.usecase.StatsUseCase.GetCustomStatUseCase

class StatusViewModelFactory(
    private val context: Context,
    private val userId: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StatusViewModel::class.java)) {
            val db = AppDatabase.getDatabase(context)
            val dao = db.userStatDao()
            val repo: UserStatRepository = UserStatRepositoryImpl(dao)
            val userDao = db.userDao()
            val userRepository: UserRepository = UserRepositoryImpl(userDao)

            return StatusViewModel(
                userId = userId,
                getCustomStatUseCase = GetCustomStatUseCase(dao),
                addCustomStatUseCase = AddCustomStatUseCase(repo),
                deleteCustomStatUseCase = DeleteCustomStatUseCase(repo),
                userRepository = userRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}