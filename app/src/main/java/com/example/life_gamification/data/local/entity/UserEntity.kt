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
    var money: Int = 300,
    val lastStatBonusDate: Long = 0L,
    val lastLoginTime: Long = System.currentTimeMillis(),

    // поля для бафов из магазина
    val expMultiplier: Double = 1.0,
    val expMultiplierExpiry: Long = 0L,
    val coinsMultiplier: Double = 1.0,
    val coinsMultiplierExpiry: Long = 0L
)
