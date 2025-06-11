package com.example.life_gamification.presentation.status

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.life_gamification.data.local.entity.UserEntity
import com.example.life_gamification.domain.usecase.StatsUseCase.AddCustomStatUseCase
import com.example.life_gamification.domain.usecase.StatsUseCase.DeleteCustomStatUseCase
import com.example.life_gamification.data.local.entity.UserStatEntity
import com.example.life_gamification.data.repository.UserRepository
import com.example.life_gamification.domain.usecase.StatsUseCase.GetCustomStatUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import androidx.compose.runtime.State



class StatusViewModel(
    private val userId: String,
    private val getCustomStatUseCase: GetCustomStatUseCase,
    private val addCustomStatUseCase: AddCustomStatUseCase,
    private val deleteCustomStatUseCase: DeleteCustomStatUseCase,
    private val userRepository: UserRepository
) : ViewModel() {

    val stats: StateFlow<List<UserStatEntity>> =
        getCustomStatUseCase(userId)
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
