package com.example.life_gamification.presentation.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.life_gamification.domain.usecase.AddStatUseCase
import com.example.life_gamification.domain.usecase.StatType
import kotlinx.coroutines.launch

class StatusViewModel(private val addStatUseCase: AddStatUseCase) : ViewModel() {
    fun addHealth(userId: String, amount: Int) {
        viewModelScope.launch {
            addStatUseCase(userId, StatType.HEALTH, amount)
        }
    }
}
