package com.example.life_gamification.presentation.Base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.life_gamification.data.local.entity.UserEntity
import com.example.life_gamification.data.local.entity.UserTaskEntity
import com.example.life_gamification.data.repository.UserRepository
import com.example.life_gamification.domain.repository.UserTasksRepository.UserTaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


abstract class BaseTaskViewModel(
    protected val userId: String,
    protected val userTaskRepository: UserTaskRepository,
    protected val userRepository: UserRepository
) : ViewModel() {

    protected val _tasks = MutableStateFlow<List<UserTaskEntity>>(emptyList())
    val tasks: StateFlow<List<UserTaskEntity>> = _tasks

    protected val _user = MutableStateFlow<UserEntity?>(null)
    val user: StateFlow<UserEntity?> = _user

    fun loadTasks() {
        viewModelScope.launch {
            userTaskRepository.getTasks(userId).collect { tasks ->
                _tasks.value = tasks
            }
        }
    }

    fun addTask(name: String, xp: Int, coins: Int, dueDate: Long) {
        viewModelScope.launch {
            userTaskRepository.addTask(
                UserTaskEntity(
                    userId = userId,
                    name = name,
                    xpReward = xp,
                    coinsReward = coins,
                    dueDate = dueDate
                )
            )
            loadTasks()
        }
    }

    fun completeTask(task: UserTaskEntity) {
        viewModelScope.launch {
            val updatedTask = task.copy(
                isCompleted = true,
                completionDate = System.currentTimeMillis()
            )
            userTaskRepository.updateTask(updatedTask)

            val expMultiplier = getCurrentExpMultiplier()
            val coinsMultiplier = getCurrentCoinsMultiplier()

            val actualXp = (task.xpReward * expMultiplier).toInt()
            val actualCoins = (task.coinsReward * coinsMultiplier).toInt()

            _user.value?.let { user ->
                val updatedUser = user.copy(
                    experience = user.experience + actualXp,
                    money = user.money + actualCoins
                )
                userRepository.updateUser(updatedUser)
                _user.value = updatedUser
            }
            loadTasks()
        }
    }

    fun deleteTask(task: UserTaskEntity) {
        viewModelScope.launch {
            userTaskRepository.deleteTask(task)
            loadTasks()
        }
    }

    protected fun getCurrentExpMultiplier(): Double {
        val user = _user.value ?: return 1.0
        return if (user.expMultiplierExpiry > System.currentTimeMillis()) {
            user.expMultiplier
        } else {
            1.0
        }
    }

    protected fun getCurrentCoinsMultiplier(): Double {
        val user = _user.value ?: return 1.0
        return if (user.coinsMultiplierExpiry > System.currentTimeMillis()) {
            user.coinsMultiplier
        } else {
            1.0
        }
    }
}