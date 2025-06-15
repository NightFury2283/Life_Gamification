package com.example.life_gamification.data.local.entity


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory_items")
data class UserInventoryItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String,                    // Владелец предмета
    val name: String,                     // Название (например "Кофе с молоком")
    val description: String,              // Описание
    val type: String,                     // Категория: effect, stat, chest
    val effectValue: String?,             // Например: x2XP, +2INT и т.п.
    val isConsumed: Boolean = false       // Потрачен или нет
)
