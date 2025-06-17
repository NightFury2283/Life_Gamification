package com.example.life_gamification.domain.usecase

import com.example.life_gamification.data.local.dao.UserDao
import com.example.life_gamification.data.local.dao.UserInventoryDao
import com.example.life_gamification.data.local.dao.UserStatDao
import com.example.life_gamification.data.local.entity.UserInventoryItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Random

class ItemEffectHandler(
    private val userDao: UserDao,
    private val statDao: UserStatDao,
    private val inventoryDao: UserInventoryDao
) {
    // константы
    companion object {
        const val ONE_DAY_MS = 24 * 60 * 60 * 1000L
    }

    data class RewardItem(
        val name: String,
        val description: String,
        val type: String,
        val effectValue: String?
    )

    suspend fun applyItemEffect(userId: String, item: UserInventoryItemEntity) {
        when {
            item.effectValue == "+2_health" -> updateStat(userId, "Здоровье", 2)
            item.effectValue == "+2_strength" -> updateStat(userId, "Сила", 2)
            item.effectValue == "+2_intellect" -> updateStat(userId, "Интеллект", 2)
            item.effectValue == "x2_exp_day" -> applyExpMultiplier(userId, 2.0, ONE_DAY_MS)
            item.effectValue == "x2_coins_day" -> applyCoinsMultiplier(userId, 2.0, ONE_DAY_MS)
            item.effectValue?.startsWith("chest_") == true -> {
                openChestAndGetRewards(userId, item.effectValue)
            }
            else -> return
        }
    }

    private suspend fun updateStat(userId: String, statName: String, valueChange: Int) {
        withContext(Dispatchers.IO) {
            val stats = statDao.getStats(userId)
            val targetStat = stats.find { it.name == statName }

            targetStat?.let {
                statDao.updateStat(it.copy(value = it.value + valueChange))
            }
        }
    }

    private suspend fun applyExpMultiplier(userId: String, multiplier: Double, durationMs: Long) {
        withContext(Dispatchers.IO) {
            userDao.getUserById(userId)?.let { user ->
                userDao.updateUser(
                    user.copy(
                        expMultiplier = multiplier,
                        expMultiplierExpiry = System.currentTimeMillis() + durationMs
                    )
                )
            }
        }
    }

    private suspend fun applyCoinsMultiplier(userId: String, multiplier: Double, durationMs: Long) {
        withContext(Dispatchers.IO) {
            userDao.getUserById(userId)?.let { user ->
                userDao.updateUser(
                    user.copy(
                        coinsMultiplier = multiplier,
                        coinsMultiplierExpiry = System.currentTimeMillis() + durationMs
                    )
                )
            }
        }
    }

    //вызов рандома предметов для разных типов сундуков
    private fun generateCommonChestRewards(): List<RewardItem> {
        // 1 случайный предмет + шанс на ещё один
        return listOfNotNull(
            getRandomItem(),
            if (Random().nextInt(100) < 20) getRandomItem() else null
        )
    }

    private fun generateEpicChestRewards(): List<RewardItem> {
        // 2 предмета + шанс на ещё один
        return listOfNotNull(
            getRandomItem(),
            getRandomItem(),
            if (Random().nextInt(100) < 30) getRandomItem() else null
        )
    }

    private fun generateLegendaryChestRewards(): List<RewardItem> {
        // 4 предмета + эффект
        return listOf(
            getRandomItem(),
            getRandomItem(),
            getRandomItem(),
            getRandomItem(),
            getRandomEffect()
        )
    }

    private fun getRandomItem(): RewardItem {
        val commonItems = listOf(
            RewardItem("Протеиновый батончик", "+2 к силе", "stat", "+2_strength"),
            RewardItem("Витаминка", "+2 к здоровью", "stat", "+2_health"),
            RewardItem("Книга знаний", "+2 XP к интеллекту", "stat", "+2_intellect"),

        )
        return commonItems.random()
    }

    private fun getRandomEffect(): RewardItem {
        val commonItems = listOf(
            RewardItem("Кофе с молоком", "Энергия на весь день", "effect", "x2_exp_day"),
            RewardItem("Горшочек лепрекона", "Удача сегодня со мной", "effect", "x2_coins_day"),
        )
        return commonItems.random()
    }

    private var lastGeneratedRewards: List<RewardItem> = emptyList()

    suspend fun openChestAndGetRewards(userId: String, chestType: String): List<RewardItem> {
        return withContext(Dispatchers.IO) {
            lastGeneratedRewards = when (chestType) {
                "chest_common" -> generateCommonChestRewards()
                "chest_epic" -> generateEpicChestRewards()
                "chest_legendary" -> generateLegendaryChestRewards()
                else -> emptyList()
            }

            lastGeneratedRewards.forEach { reward ->
                inventoryDao.insertItem(
                    UserInventoryItemEntity(
                        userId = userId,
                        name = reward.name,
                        description = reward.description,
                        type = reward.type,
                        effectValue = reward.effectValue
                    )
                )
            }
            lastGeneratedRewards
        }
    }
}