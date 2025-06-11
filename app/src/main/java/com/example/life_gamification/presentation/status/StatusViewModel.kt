package com.example.life_gamification.presentation.status

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.life_gamification.data.local.entity.UserDailyQuestsEntity
import com.example.life_gamification.data.local.entity.UserEntity
import com.example.life_gamification.data.local.entity.UserStatEntity
import com.example.life_gamification.data.repository.UserRepository
import com.example.life_gamification.domain.usecase.StatusUseCases
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class StatusViewModel(
    private val userId: String,
    private val useCases: StatusUseCases,
    private val userRepository: UserRepository
) : ViewModel() {
    //характеристики
    val stats: StateFlow<List<UserStatEntity>> =
        useCases.getCustomStat(userId)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    //ежедневки
    val daily: StateFlow<List<UserDailyQuestsEntity>> =
        useCases.getCustomDaily(userId)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _user = mutableStateOf<UserEntity?>(null)
    val user: State<UserEntity?> = _user
    //Зачем: Это состояние будет отслеживаться в UI
    // — оно обновится, когда пользователь загрузится.

    init {
        viewModelScope.launch {
            _user.value = userRepository.getUserById(userId)
        }
    }

    //методы характеристик
    fun addStat(name: String, value: Int = 0) {
        viewModelScope.launch {
            useCases.addCustomStat(userId, name, value)
        }
    }


    fun deleteStat(stat: UserStatEntity) {
        viewModelScope.launch {
            useCases.deleteCustomStat(stat)
        }
    }


    //методы ежедневок
    fun addDaily(name: String, addXp: Int = 1){
        viewModelScope.launch {
            useCases.addCustomDaily(userId, name, addXp)
        }
    }


    fun deleteDaily(daily: UserDailyQuestsEntity){
        viewModelScope.launch {
            useCases.deleteCustomDaily(daily)
        }
    }

}
