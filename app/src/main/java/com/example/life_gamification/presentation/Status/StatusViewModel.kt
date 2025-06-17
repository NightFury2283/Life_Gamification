package com.example.life_gamification.presentation.Status

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
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val _user = MutableStateFlow<UserEntity?>(null)
    val user: StateFlow<UserEntity?> = _user





    //состояние - сколько осталось очков распределения
    private val _statPoints = mutableStateOf(0)
    val statPoints: State<Int> get() = _statPoints

    init {
        viewModelScope.launch {
            _user.value = userRepository.getUserById(userId)?.let { user ->
                val now = System.currentTimeMillis()
                val needsReset = (user.expMultiplierExpiry < now && user.expMultiplier != 1.0) ||
                        (user.coinsMultiplierExpiry < now && user.coinsMultiplier != 1.0)

                // сброс баффов если прошло 24 часа
                if (needsReset) {
                    user.copy(
                        expMultiplier = 1.0,
                        expMultiplierExpiry = 0L,
                        coinsMultiplier = 1.0,
                        coinsMultiplierExpiry = 0L
                    ).also { updatedUser ->
                        userRepository.updateUser(updatedUser)
                    }
                } else {
                    user
                }
            }
            if (!isToday(_user.value?.lastLoginTime ?: 0L)) {
                _statPoints.value = 0 // сбросить накопленные очки
            }

            val dailies = useCases.getCustomDailyList(userId)
            _completedDailiesToday.clear()
            dailies.forEach { daily ->
                if (isToday(daily.lastCompletedDate)) {
                    _completedDailiesToday.add(daily.id)
                }
            }

            val currentStats = useCases.getCustomStatList(userId)
            val existingNames = currentStats.map { it.name }
            val required = listOf("Здоровье", "Сила", "Интеллект")
            val missing = required.filterNot { it in existingNames }

            if (missing.isNotEmpty()) {
                missing.forEach { useCases.addCustomStat(userId, it, 0) }
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
            _user.value ?: return@launch


            if (isCompleted) {
                _completedDailiesToday.add(daily.id)
                checkAllDailiesCompleted()
                addExperience(daily.addXp)

                //обновляем дату выполнения
                val updatedDaily = daily.copy(lastCompletedDate = System.currentTimeMillis())
                useCases.updateDaily(updatedDaily)
            } else {
                _completedDailiesToday.remove(daily.id)
                addExperience(-daily.addXp)

                val updatedDaily = daily.copy(lastCompletedDate = 0L)
                useCases.updateDaily(updatedDaily)
            }
        }
    }


    //добавление монет и опыта с применением множителей. (может пригодится для задач пользователя)
    fun addExperience(baseXp: Int) {
        viewModelScope.launch {
            val multiplier = getCurrentExpMultiplier()
            val xpToAdd = (baseXp * multiplier).toInt()
            _user.value?.let { user ->
                val updatedUser = user.copy(experience = user.experience + xpToAdd)
                userRepository.updateUser(updatedUser)
                _user.value = updatedUser
            }
        }
    }
    //на будущее (для задач игрока)
    fun addCoins(baseCoins: Int) {
        viewModelScope.launch {
            val multiplier = getCurrentCoinsMultiplier()
            val coinsToAdd = (baseCoins * multiplier).toInt()
            _user.value?.let { user ->
                val updatedUser = user.copy(money = user.money + coinsToAdd)
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

    //проверка: все ли ежедневки выполнены
    private fun checkAllDailiesCompleted() {
        val allCompleted = daily.value.all { isDailyCompletedToday(it) }

        val today = System.currentTimeMillis()
        val lastBonusDate = _user.value?.lastStatBonusDate ?: 0L

        if (allCompleted && !isToday(lastBonusDate)) {
            _statPoints.value = 3

            // обновляем дату последней выдачи
            val updatedUser = _user.value?.copy(lastStatBonusDate = today)
            if (updatedUser != null) {
                viewModelScope.launch {
                    userRepository.updateUser(updatedUser)
                    _user.value = updatedUser
                }
            }
        }
    }



    //распределение очков(увеличение хар-ки на 1)
    fun increaseStat(stat: UserStatEntity) {
        if (_statPoints.value > 0) {
            viewModelScope.launch {
                val updatedStat = stat.copy(value = stat.value + 1)
                useCases.updateCustomStat(updatedStat)
                _statPoints.value -= 1
            }
        }
    }

    // узнаём какой у нас сейчас множитель монет и опыта
    private suspend fun getCurrentExpMultiplier(): Double {
        val user = _user.value ?: return 1.0
        return if (user.expMultiplierExpiry > System.currentTimeMillis()) {
            user.expMultiplier
        } else {
            1.0
        }
    }

    private suspend fun getCurrentCoinsMultiplier(): Double {
        val user = _user.value ?: return 1.0
        return if (user.coinsMultiplierExpiry > System.currentTimeMillis()) {
            user.coinsMultiplier
        } else {
            1.0
        }
    }


    fun reloadUser() {
        viewModelScope.launch {
            _user.value = userRepository.getUserById(userId)
        }
    }

    //методы для проверки есть ли у пользователя множители сейчас. Для UI
    fun hasActiveExpMultiplier(): Boolean {
        val user = _user.value ?: return false
        return user.expMultiplierExpiry > System.currentTimeMillis()
    }

    fun hasActiveCoinsMultiplier(): Boolean {
        val user = _user.value ?: return false
        return user.coinsMultiplierExpiry > System.currentTimeMillis()
    }

}
