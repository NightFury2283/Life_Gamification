package com.example.life_gamification.data.local.entity



import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats",
        indices = [Index(value = ["userId", "name"], unique = true)])
data class UserStatEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,
    val name: String,
    var value: Int
)