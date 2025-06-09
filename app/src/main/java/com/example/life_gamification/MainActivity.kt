package com.example.life_gamification

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.life_gamification.presentation.AppNavHost
import com.example.life_gamification.ui.theme.Life_gamificationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Life_gamificationTheme {
                AppNavHost(applicationContext) // передаём context вручную
            }
        }
    }
}
