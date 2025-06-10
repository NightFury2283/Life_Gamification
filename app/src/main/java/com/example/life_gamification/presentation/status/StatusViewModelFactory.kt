package com.example.life_gamification.presentation.status

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.life_gamification.data.local.db.AppDatabase
import com.example.life_gamification.domain.repository.UserStatRepository
import com.example.life_gamification.domain.repository.UserStatRepositoryImpl
import com.example.life_gamification.domain.usecase.AddCustomStatUseCase
import com.example.life_gamification.domain.usecase.DeleteCustomStatUseCase
import com.example.life_gamification.domain.usecase.GetCustomStatUseCase

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

            return StatusViewModel(
                userId = userId,
                getCustomStatUseCase = GetCustomStatUseCase(dao),
                addCustomStatUseCase = AddCustomStatUseCase(repo),
                deleteCustomStatUseCase = DeleteCustomStatUseCase(repo)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}