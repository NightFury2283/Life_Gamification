package com.example.life_gamification.domain.usecase


import com.example.life_gamification.data.local.entity.UserEntity
import com.example.life_gamification.data.local.entity.UserInventoryItemEntity
import com.example.life_gamification.data.local.dao.UserDao
import com.example.life_gamification.data.local.dao.UserStatDao

class ItemEffectHandler(
    private val userDao: UserDao,
    private val statDao: UserStatDao
) {

    suspend fun applyItemEffect(userId: String, item: UserInventoryItemEntity) {
        if (item.type == "stat") {
            applyStatEffect(userId, item.effectValue)
        }
    }

    private suspend fun applyStatEffect(userId: String, effect: String?) {
        if (effect == null) return

        val stats = statDao.getStats(userId).toMutableList()

        val mappings = mapOf(
            "_intellect" to "Интеллект",
            "_health" to "Здоровье",
            "_strength" to "Сила"
        )

        for ((suffix, statName) in mappings) {
            if (effect.startsWith("+") && effect.endsWith(suffix)) {
                val value = effect.removePrefix("+").removeSuffix(suffix).toIntOrNull() ?: return
                val stat = stats.find { it.name == statName } ?: return
                stat.value += value
                statDao.updateStat(stat)
                return
            }
        }
    }


}
