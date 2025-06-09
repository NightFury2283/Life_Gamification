package com.example.life_gamification.googleSign.data.auth



import com.example.life_gamification.googleSign.domain.auth.AuthRepository

class AuthRepositoryImpl(private val firebaseAuth: FirebaseAuthSource) : AuthRepository {
    override fun isUserAuthenticated() = firebaseAuth.isUserAuthenticated()

    override suspend fun loginWithGoogle(idToken: String) = firebaseAuth.googleSignIn(idToken)
}