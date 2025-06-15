package com.example.life_gamification.presentation.Inventory


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.life_gamification.data.local.entity.UserInventoryItemEntity
import com.example.life_gamification.domain.repository.UserInventoryRepositories.UserInventoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InventoryViewModel(
    private val repository: UserInventoryRepository
) : ViewModel() {

    private val _userItems = MutableStateFlow<List<UserInventoryItemEntity>>(emptyList())
    val userItems: StateFlow<List<UserInventoryItemEntity>> = _userItems

    private val _userCoins = MutableStateFlow(0)
    val userCoins: StateFlow<Int> = _userCoins

    fun loadUserCoins(userId: String) {
        viewModelScope.launch {
            _userCoins.value = repository.getUserCoins(userId)
        }
    }

    fun loadInventory(userId: String) {
        viewModelScope.launch {
            _userItems.value = repository.getUserItems(userId)
        }
    }

    fun addItem(item: UserInventoryItemEntity) {
        viewModelScope.launch {
            repository.addItem(item)
            loadInventory(item.userId)
        }
    }

    fun consumeItem(item: UserInventoryItemEntity) {
        viewModelScope.launch {
            repository.consumeItem(item)
            loadInventory(item.userId)
        }
    }
}
