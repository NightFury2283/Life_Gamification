package com.example.life_gamification.googleSign.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun HomeScreen(navController: NavHostController = rememberNavController()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Вы вошли!",
            fontSize = 28.sp,
            fontWeight = MaterialTheme.typography.titleLarge.fontWeight
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Добро пожаловать в главное меню",
            fontSize = 18.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                // Можно добавить навигацию на другие экраны
            }
        ) {
            Text(text = "Начать")
        }
    }
}