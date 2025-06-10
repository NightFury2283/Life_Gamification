package com.example.life_gamification.data.repository

import com.example.life_gamification.data.local.entity.UserEntity

interface UserRepository {
    suspend fun getUserById(userId: String): UserEntity?
}