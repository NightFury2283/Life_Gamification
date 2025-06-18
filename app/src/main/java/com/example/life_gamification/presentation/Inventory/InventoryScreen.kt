package com.example.life_gamification.presentation.Inventory


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.life_gamification.data.local.db.AppDatabase
import com.example.life_gamification.domain.repository.UserInventoryRepositories.UserInventoryRepositoryImpl
import com.example.life_gamification.domain.usecase.ItemEffectHandler
import kotlinx.coroutines.launch

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
                    statDao = db.userStatDao(),
                    inventoryDao = db.inventoryDao()
                )
            )
        }
    )
) {
    val items by viewModel.userItems.collectAsState()
    var showChestRewards by remember { mutableStateOf(false) }
    var chestRewards by remember { mutableStateOf<List<ItemEffectHandler.RewardItem>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    // Загружаем инвентарь при первом запуске
    LaunchedEffect(userId) {
        viewModel.loadInventory(userId)
    }

    // Диалог с наградами сундуков
    if (showChestRewards) {
        AlertDialog(
            onDismissRequest = { showChestRewards = false },
            title = { Text("Вы получили:") },
            text = {
                Column {
                    chestRewards.forEach { reward ->
                        Text("- ${reward.name}: ${reward.description}")
                    }
                }
            },
            confirmButton = {
                Button(onClick = { showChestRewards = false }) {
                    Text("OK")
                }
            }
        )
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF042C84),
                        Color(0xFF4813B2),
                        Color(0xFF218DDB)
                    )
                )
            )
            .statusBarsPadding()
    )
    {

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text(
                text = "Инвентарь",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(items.filter { !it.isConsumed }) { item ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(item.name, fontWeight = FontWeight.Bold)
                            Text(item.description)
                            Text("Тип: ${item.type}")
                            Text("Эффект: ${item.effectValue}")
                            Button(
                                onClick = {
                                    if (item.type == "chest") {
                                        coroutineScope.launch {
                                            chestRewards = viewModel.openChest(item)
                                            showChestRewards = true
                                        }
                                    } else {
                                        viewModel.consumeItem(item)
                                    }
                                }
                            ) {
                                Text(if (item.type == "chest") "Открыть" else "Использовать")
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
}