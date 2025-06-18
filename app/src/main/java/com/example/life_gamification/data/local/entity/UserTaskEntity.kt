package com.example.life_gamification.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_tasks")
data class UserTaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val name: String,
    val xpReward: Int,
    val coinsReward: Int,
    val dueDate: Long,
    val isCompleted: Boolean = false,
    val completionDate: Long? = null
)