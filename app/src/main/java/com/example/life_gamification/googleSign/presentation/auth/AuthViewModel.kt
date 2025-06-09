package com.example.life_gamification.googleSign.presentation.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.life_gamification.googleSign.domain.auth.LoginWithGoogleUseCase
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginWithGoogleUseCase: LoginWithGoogleUseCase
) : ViewModel() {

    private val _loginState = MutableLiveData<Result<Unit>>()
    val loginState: LiveData<Result<Unit>> get() = _loginState

    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            val result = loginWithGoogleUseCase(idToken)
            _loginState.value = result
        }
    }

    fun clearLoginState() {
        _loginState.value = null
    }
}
