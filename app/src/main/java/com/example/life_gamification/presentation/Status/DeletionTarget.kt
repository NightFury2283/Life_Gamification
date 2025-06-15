package com.example.life_gamification.presentation.Status

sealed class DeletionTarget(val name: String, val onConfirm: () -> Unit) {
    class Stat(name: String, onConfirm: () -> Unit) : DeletionTarget(name, onConfirm)
    class Daily(name: String, onConfirm: () -> Unit) : DeletionTarget(name, onConfirm)
}

