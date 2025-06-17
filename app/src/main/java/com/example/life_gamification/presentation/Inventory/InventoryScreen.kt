package com.example.life_gamification.presentation.Inventory


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.life_gamification.data.local.db.AppDatabase
import com.example.life_gamification.domain.repository.UserInventoryRepositories.UserInventoryRepositoryImpl
import com.example.life_gamification.domain.usecase.ItemEffectHandler



@Composable
fun InventoryScreen(
    userId: String,
    viewModel: InventoryViewModel = viewModel(
        factory = run {
            val context = LocalContext.current
            val db = AppDatabase.getDatabase(context)

            InventoryViewModelFactory(
                repository = UserInventoryRepositoryImpl(
                    dao = db.inventoryDao()
                ),
                userDao = db.userDao(),
                effectHandler = ItemEffectHandler(
                    userDao = db.userDao(),
                    statDao = db.userStatDao()
                )
            )
        }
    )
) {
    val items by viewModel.userItems.collectAsState()

    // Загружаем инвентарь при первом запуске
    LaunchedEffect(userId) {
        viewModel.loadInventory(userId)
    }
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Text(
            text = "Inventory",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            items(items.filter { !it.isConsumed }) { item ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(item.name, fontWeight = FontWeight.Bold)
                        Text(item.description)
                        Text("Тип: ${item.type}")
                        Text("Эффект: ${item.effectValue}")
                        Button(
                            onClick = {
                                viewModel.consumeItem(item)
                            }
                        ) {
                            Text("Использовать")
                        }
                    }
                }
            }

            if (items.all { it.isConsumed }) {
                item {
                    Text(
                        text = "Инвентарь пуст 😢",
                        modifier = Modifier.padding(top = 24.dp),
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
