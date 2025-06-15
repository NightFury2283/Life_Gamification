package com.example.life_gamification.data.local.dao



import androidx.room.*
import com.example.life_gamification.data.local.entity.UserInventoryItemEntity

@Dao
interface UserInventoryDao {
    @Query("SELECT * FROM inventory_items WHERE userId = :userId")
    suspend fun getAllItemsForUser(userId: String): List<UserInventoryItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: UserInventoryItemEntity)

    @Delete
    suspend fun deleteItem(item: UserInventoryItemEntity)

    @Update
    suspend fun updateItem(item: UserInventoryItemEntity)

    @Query("DELETE FROM inventory_items WHERE isConsumed = 1")
    suspend fun clearConsumedItems()

    @Query("SELECT money FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserCoins(userId: String): Int
}
