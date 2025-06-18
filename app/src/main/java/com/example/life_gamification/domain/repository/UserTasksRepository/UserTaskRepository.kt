package com.example.life_gamification.domain.repository.UserTasksRepository

import com.example.life_gamification.data.local.dao.UserTaskDao
import com.example.life_gamification.data.local.entity.UserTaskEntity
import kotlinx.coroutines.flow.Flow


interface UserTaskRepository {
    suspend fun addTask(task: UserTaskEntity)
    suspend fun updateTask(task: UserTaskEntity)
    suspend fun deleteTask(task: UserTaskEntity)
    fun getTasks(userId: String): Flow<List<UserTaskEntity>>
    suspend fun getTodayTasks(userId: String, endOfDay: Long): List<UserTaskEntity>
}