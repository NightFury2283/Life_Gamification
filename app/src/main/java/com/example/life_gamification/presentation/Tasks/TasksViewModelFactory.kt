package com.example.life_gamification.presentation.Tasks

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.life_gamification.data.local.db.AppDatabase
import com.example.life_gamification.data.repository.UserRepositoryImpl
import com.example.life_gamification.domain.repository.UserTasksRepository.UserTaskRepositoryImpl

class TasksViewModelFactory(
    private val context: Context,
    private val userId: String
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TasksViewModel::class.java)) {
            val db = AppDatabase.getDatabase(context)
            return TasksViewModel(
                userId = userId,
                taskDao = db.userTaskDao(),
                userTaskRepository = UserTaskRepositoryImpl(db.userTaskDao()),
                userRepository = UserRepositoryImpl(db.userDao())
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}