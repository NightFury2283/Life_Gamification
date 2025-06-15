package com.example.life_gamification.presentation.Inventory


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.life_gamification.domain.repository.UserInventoryRepositories.UserInventoryRepository

class InventoryViewModelFactory(
    private val repository: UserInventoryRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return InventoryViewModel(repository) as T
    }
}
