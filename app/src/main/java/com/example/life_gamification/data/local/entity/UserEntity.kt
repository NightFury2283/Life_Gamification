package com.example.life_gamification.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String?,
    var level: Int = 1,
    var experience: Int = 0,
    var money: Int = 100,
    var health: Int = 0,
    var intellect: Int = 0,
    var strength: Int = 0,
    val lastStatBonusDate: Long = 0L,
    val lastLoginTime: Long = System.currentTimeMillis()
)
