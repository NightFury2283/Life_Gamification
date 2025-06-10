package com.example.life_gamification.presentation.status

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.life_gamification.domain.usecase.AddCustomStatUseCase
import com.example.life_gamification.domain.usecase.DeleteCustomStatUseCase
import com.example.life_gamification.data.local.entity.UserStatEntity
import com.example.life_gamification.domain.usecase.GetCustomStatUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StatusViewModel(
    private val userId: String,
    private val getCustomStatUseCase: GetCustomStatUseCase,
    private val addCustomStatUseCase: AddCustomStatUseCase,
    private val deleteCustomStatUseCase: DeleteCustomStatUseCase
) : ViewModel() {

    val stats: StateFlow<List<UserStatEntity>> =
        getCustomStatUseCase(userId)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addStat(name: String, value: Int = 0) {
        viewModelScope.launch {
            addCustomStatUseCase(userId, name, value)
        }
    }


    fun deleteStat(stat: UserStatEntity) {
        viewModelScope.launch {
            deleteCustomStatUseCase(stat)
        }
    }
}
