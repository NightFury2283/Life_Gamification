package com.example.life_gamification.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val userId: String,
    val level: Int = 1,
    val xp: Int = 0,
    val health: Int = 0,
    val intellect: Int = 0,
    val productivity: Int = 0
)
