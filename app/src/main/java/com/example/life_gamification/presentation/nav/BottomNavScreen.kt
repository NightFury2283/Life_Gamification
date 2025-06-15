package com.example.life_gamification.presentation.nav

sealed class BottomNavScreen(val route: String) {
    object Status : BottomNavScreen("status")
    object Tasks : BottomNavScreen("tasks")
    object Shop : BottomNavScreen("shop")
    object Inventory : BottomNavScreen("inventory")
    object Settings : BottomNavScreen("settings")
}
