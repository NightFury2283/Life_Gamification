package com.example.life_gamification.googleSign.domain.auth

interface AuthRepository {
    fun isUserAuthenticated(): Boolean
    suspend fun loginWithGoogle(idToken: String): Result<Unit>
}