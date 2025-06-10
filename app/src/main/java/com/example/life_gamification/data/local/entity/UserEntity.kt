package com.example.life_gamification.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String,
    val name: String,
    val email: String?,
    val level: Int = 1,
    val experience: Int = 0,
    val money: Int = 0,
    val healthXp: Int = 0,
    val intellectXp: Int = 0,
    val productivityXp: Int = 0,
    val lastLoginTime: Long = System.currentTimeMillis()
)
