package com.example.life_gamification.data.auth

import com.example.life_gamification.domain.auth.AuthRepository
import com.example.life_gamification.data.auth.FirebaseAuthSource

class AuthRepositoryImpl(private val firebaseAuth: FirebaseAuthSource) : AuthRepository {
    override fun isUserAuthenticated() = firebaseAuth.isUserAuthenticated()

    override suspend fun loginWithGoogle(idToken: String) = firebaseAuth.googleSignIn(idToken)
}