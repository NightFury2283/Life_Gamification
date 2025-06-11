package com.example.life_gamification.presentation.status

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
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


    //обработка выполнения сегодня ежедневок
    private val _completedDailiesToday = mutableStateListOf<Int>()
    val completedDailiesToday: List<Int> get() = _completedDailiesToday

    private val _user = mutableStateOf<UserEntity?>(null)
    val user: State<UserEntity?> = _user
    //Зачем: Это состояние будет отслеживаться в UI
    // — оно обновится, когда пользователь загрузится.




    init {
        viewModelScope.launch {
            _user.value = userRepository.getUserById(userId)

            val dailies = useCases.getCustomDailyList(userId)
            _completedDailiesToday.clear()
            dailies.forEach { daily ->
                if (isToday(daily.lastCompletedDate)) {
                    _completedDailiesToday.add(daily.id)
                }
            }
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

    //функции выполнения ежедневок (обработка)
    fun isDailyCompletedToday(daily: UserDailyQuestsEntity): Boolean {
        return _completedDailiesToday.contains(daily.id)
    }

    fun setDailyCompletedToday(daily: UserDailyQuestsEntity, isCompleted: Boolean) {
        viewModelScope.launch {
            val currentUser = _user.value ?: return@launch

            if (isCompleted && !_completedDailiesToday.contains(daily.id)) {
                _completedDailiesToday.add(daily.id)

                // Добавляем опыт
                val updatedUser = currentUser.copy(experience = currentUser.experience + daily.addXp)
                userRepository.updateUser(updatedUser)
                _user.value = updatedUser

                // Обновляем дату выполнения
                val updatedDaily = daily.copy(lastCompletedDate = System.currentTimeMillis())
                useCases.updateDaily(updatedDaily)

            } else if (!isCompleted && _completedDailiesToday.contains(daily.id)) {
                _completedDailiesToday.remove(daily.id)

                // Убираем опыт
                val updatedUser = currentUser.copy(experience = (currentUser.experience - daily.addXp).coerceAtLeast(0))
                userRepository.updateUser(updatedUser)
                _user.value = updatedUser

                // Сбрасываем дату выполнения
                val updatedDaily = daily.copy(lastCompletedDate = 0L)
                useCases.updateDaily(updatedDaily)
            }
        }
    }



    fun addExperience(amount: Int) {
        viewModelScope.launch {
            _user.value?.let { user ->
                val updatedUser = user.copy(experience = (user.experience + amount).coerceAtLeast(0))
                userRepository.updateUser(updatedUser)
                _user.value = updatedUser
            }
        }
    }

    //метод для работы с датой (для повторного появления выполненных ежедневок)
    private fun isToday(dateMillis: Long): Boolean {
        val cal1 = java.util.Calendar.getInstance()
        val cal2 = java.util.Calendar.getInstance().apply { timeInMillis = dateMillis }
        return cal1.get(java.util.Calendar.YEAR) == cal2.get(java.util.Calendar.YEAR) &&
                cal1.get(java.util.Calendar.DAY_OF_YEAR) == cal2.get(java.util.Calendar.DAY_OF_YEAR)
    }


}
