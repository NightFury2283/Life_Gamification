package com.example.life_gamification.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.life_gamification.data.local.entity.UserDailyQuestsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDailyQuestsDao {

    @Query("SELECT * FROM user_stats WHERE userId = :userId")
    fun getDailyQuestsFlow(userId: String): Flow<List<UserDailyQuestsEntity>>

    @Query("SELECT * FROM user_stats WHERE userId = :userId")
    suspend fun getDailyQuests(userId: String): List<UserDailyQuestsEntity> // <-- МОЖЕТ ОСТАТЬСЯ для других задач

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDailyQuest(daily: UserDailyQuestsEntity)

    @Delete
    suspend fun deleteDailyQuest(daily: UserDailyQuestsEntity)

    @Update
    suspend fun updateDailyQuest(daily: UserDailyQuestsEntity)
}