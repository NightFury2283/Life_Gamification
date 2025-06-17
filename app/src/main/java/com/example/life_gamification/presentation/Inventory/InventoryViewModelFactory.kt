package com.example.life_gamification.presentation.Inventory


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.life_gamification.data.local.dao.UserDao
import com.example.life_gamification.domain.repository.UserInventoryRepositories.UserInventoryRepository
import com.example.life_gamification.domain.usecase.ItemEffectHandler

class InventoryViewModelFactory(
    private val repository: UserInventoryRepository,
    private val userDao: UserDao,
    private val effectHandler: ItemEffectHandler
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return InventoryViewModel(repository, userDao, effectHandler) as T
    }
}
