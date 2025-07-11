package com.example.life_gamification.domain.Configs

import com.example.life_gamification.domain.Configs.LevelConfig.MAX_LEVEL

object LevelConfig {
    val REQUIREMENTS = mapOf(
        1 to 0,
        2 to 10,
        3 to 15,
        4 to 30,
        5 to 50
    )

    const val MAX_LEVEL = 5
}

fun isMaxLevel(currentLevel: Int): Boolean {
    return currentLevel >= MAX_LEVEL
}