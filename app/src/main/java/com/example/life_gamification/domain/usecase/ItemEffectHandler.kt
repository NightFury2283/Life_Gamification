package com.example.life_gamification.domain.usecase


import com.example.life_gamification.data.local.entity.UserEntity
import com.example.life_gamification.data.local.entity.UserInventoryItemEntity
import com.example.life_gamification.data.local.dao.UserDao

class ItemEffectHandler(
    private val userDao: UserDao
) {

    suspend fun applyItemEffect(userId: String, item: UserInventoryItemEntity) {
        val user = userDao.getUserById(userId) ?: return

        when (item.type) {
            "stat" -> applyStatEffect(user, item.effectValue)
            // В будущем: "buff", "chest" и т.д.
        }

        userDao.update(user)
    }

    private fun applyStatEffect(user: UserEntity, effect: String?) {
        if (effect == null) return

        when {
            effect.startsWith("+") && effect.endsWith("_intellect") -> {
                val value = effect.removePrefix("+").removeSuffix("_intellect").toIntOrNull() ?: return
                user.intellect += value
            }

            effect.startsWith("+") && effect.endsWith("_health") -> {
                val value = effect.removePrefix("+").removeSuffix("_health").toIntOrNull() ?: return
                user.health += value
            }

            effect.startsWith("+") && effect.endsWith("_strength") -> {
                val value = effect.removePrefix("+").removeSuffix("_strength").toIntOrNull() ?: return
                user.strength += value
            }

            // Добавь ещё другие характеристики, если надо
        }
    }
}
