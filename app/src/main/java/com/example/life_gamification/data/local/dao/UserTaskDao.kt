package com.example.life_gamification.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.life_gamification.data.local.entity.UserTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserTaskDao {
    @Insert
    suspend fun insertTask(task: UserTaskEntity)

    @Update
    suspend fun updateTask(task: UserTaskEntity)

    @Delete
    suspend fun deleteTask(task: UserTaskEntity)

    @Query("SELECT * FROM user_tasks WHERE userId = :userId")
    fun getTasks(userId: String): Flow<List<UserTaskEntity>>

    @Query("SELECT * FROM user_tasks WHERE userId = :userId AND dueDate <= :endOfDay AND isCompleted = 0")
    suspend fun getTasksByDate(userId: String, endOfDay: Long): List<UserTaskEntity>
}