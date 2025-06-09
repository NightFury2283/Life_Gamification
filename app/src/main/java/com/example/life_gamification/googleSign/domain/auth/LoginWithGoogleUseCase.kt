package com.example.life_gamification.googleSign.domain.auth


class LoginWithGoogleUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(idToken: String): Result<Unit> {
        return repository.loginWithGoogle(idToken)
    }
}