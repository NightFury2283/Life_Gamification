package com.example.life_gamification.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await

class FirebaseAuthSource(private val auth: FirebaseAuth) {

    fun isUserAuthenticated() = auth.currentUser != null

    suspend fun googleSignIn(idToken: String): Result<Unit> {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = auth.signInWithCredential(credential).await()
        return if (result.user != null) Result.success(Unit)
        else Result.failure(Exception("Sign in failed"))
    }
}