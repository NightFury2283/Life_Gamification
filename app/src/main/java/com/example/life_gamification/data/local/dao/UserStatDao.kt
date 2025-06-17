package com.example.life_gamification.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.life_gamification.data.local.entity.UserStatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserStatDao {

    @Query("SELECT * FROM user_stats WHERE userId = :userId")
    fun getStatsFlow(userId: String): Flow<List<UserStatEntity>>

    @Query("SELECT * FROM user_stats WHERE userId = :userId")
    suspend fun getStats(userId: String): List<UserStatEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addStat(stat: UserStatEntity)

    @Delete
    suspend fun deleteStat(stat: UserStatEntity)

    @Update
    suspend fun updateStat(stat: UserStatEntity)
}
