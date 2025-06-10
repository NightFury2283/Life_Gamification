package com.example.life_gamification.domain.usecase


import com.example.life_gamification.data.local.entity.UserStatEntity
import com.example.life_gamification.domain.repository.UserStatRepository

class DeleteCustomStatUseCase(private val repository: UserStatRepository) {
    suspend operator fun invoke(stat: UserStatEntity) {
        repository.deleteStat(stat)
    }
}