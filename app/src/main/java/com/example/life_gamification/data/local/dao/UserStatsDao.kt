package com.example.life_gamification.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.life_gamification.data.local.entity.UserStatsEntity

@Dao
interface UserStatsDao {
    @Query("SELECT * FROM user_stats WHERE userId = :userId")
    suspend fun getStats(userId: String): UserStatsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateStats(stats: UserStatsEntity)
}
