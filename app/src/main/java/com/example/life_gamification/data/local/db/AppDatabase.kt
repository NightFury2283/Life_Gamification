package com.example.life_gamification.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.life_gamification.data.local.dao.UserDailyQuestsDao
import com.example.life_gamification.data.local.dao.UserDao
import com.example.life_gamification.data.local.dao.UserInventoryDao
import com.example.life_gamification.data.local.dao.UserStatDao
import com.example.life_gamification.data.local.entity.UserDailyQuestsEntity
import com.example.life_gamification.data.local.entity.UserEntity
import com.example.life_gamification.data.local.entity.UserInventoryItemEntity
import com.example.life_gamification.data.local.entity.UserStatEntity


@Database(
    entities = [UserEntity::class, UserStatEntity::class, UserDailyQuestsEntity::class, UserInventoryItemEntity::class],
    version = 8
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun userStatDao(): UserStatDao
    abstract fun userDailyDao(): UserDailyQuestsDao
    abstract fun inventoryDao(): UserInventoryDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gamification.db"
                    //удалить потом то что снизу строка (очистка бд)
                ).fallbackToDestructiveMigration(true)
                    .build().also { INSTANCE = it }
            }
        }
    }
}
