package com.example.life_gamification.domain.auth

interface AuthRepository {
    fun isUserAuthenticated(): Boolean
    suspend fun loginWithGoogle(idToken: String): Result<Unit>
}