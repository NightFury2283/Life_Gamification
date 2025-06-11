package com.example.life_gamification.data.repository

import com.example.life_gamification.data.local.dao.UserDao
import com.example.life_gamification.data.local.entity.UserEntity

class UserRepositoryImpl(private val userDao: UserDao) : UserRepository {
    override suspend fun getUserById(userId: String): UserEntity? {
        return userDao.getUserById(userId)
    }
    override suspend fun updateUser(user: UserEntity) {
        userDao.update(user)
    }
}
