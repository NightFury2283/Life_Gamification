package com.example.life_gamification.presentation.Tasks

import androidx.lifecycle.viewModelScope
import com.example.life_gamification.data.local.dao.UserTaskDao
import com.example.life_gamification.data.repository.UserRepository
import com.example.life_gamification.domain.repository.UserTasksRepository.UserTaskRepository
import com.example.life_gamification.presentation.Base.BaseTaskViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Date


class TasksViewModel(
    userId: String,
    private val taskDao: UserTaskDao,
    userTaskRepository: UserTaskRepository,
    userRepository: UserRepository
) : BaseTaskViewModel(userId, userTaskRepository, userRepository) {

    private val _selectedDate = MutableStateFlow(Date())
    val selectedDate: StateFlow<Date> = _selectedDate

    init {
        loadUser()
        loadTasksForDate(selectedDate.value)
    }

    fun setDate(date: Date) {
        _selectedDate.value = date
        loadTasksForDate(date)
    }

    private fun loadUser() {
        viewModelScope.launch {
            _user.value = userRepository.getUserById(userId)
        }
    }

    private fun loadTasksForDate(date: Date) {
        viewModelScope.launch {
            _tasks.value = taskDao.getTasksByDate(userId, date.time)
        }
    }
}