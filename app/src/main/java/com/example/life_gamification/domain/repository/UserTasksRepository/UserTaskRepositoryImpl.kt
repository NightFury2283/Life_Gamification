package com.example.life_gamification.domain.repository.UserTasksRepository

import com.example.life_gamification.data.local.dao.UserTaskDao
import com.example.life_gamification.data.local.entity.UserTaskEntity
import kotlinx.coroutines.flow.Flow


class UserTaskRepositoryImpl(private val dao: UserTaskDao) : UserTaskRepository {
    override suspend fun addTask(task: UserTaskEntity) = dao.insertTask(task)
    override suspend fun updateTask(task: UserTaskEntity) = dao.updateTask(task)
    override suspend fun deleteTask(task: UserTaskEntity) = dao.deleteTask(task)
    override fun getTasks(userId: String): Flow<List<UserTaskEntity>> = dao.getTasks(userId)
    override suspend fun getTodayTasks(userId: String, endOfDay: Long): List<UserTaskEntity> =
        dao.getTasksByDate(userId, endOfDay)
}