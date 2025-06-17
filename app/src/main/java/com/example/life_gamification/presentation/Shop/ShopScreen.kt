package com.example.life_gamification.presentation.Shop



import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.life_gamification.data.local.db.AppDatabase
import com.example.life_gamification.data.local.entity.UserInventoryItemEntity
import com.example.life_gamification.domain.repository.UserInventoryRepositories.UserInventoryRepositoryImpl
import com.example.life_gamification.domain.usecase.ItemEffectHandler
import com.example.life_gamification.presentation.Inventory.InventoryViewModel
import com.example.life_gamification.presentation.Inventory.InventoryViewModelFactory
import com.example.life_gamification.presentation.Status.StatusViewModel
import com.example.life_gamification.presentation.common.ConfirmDialog

enum class Category(val label: String) {
    EFFECTS("\uD83C\uDF00 Эффекты"),
    GOODIES("\uD83C\uDF6D Вкусности"),
    CHESTS("\uD83E\uDDF0 Сундуки")
}

data class ShopItem(
    val id: String,
    val name: String,
    val description: String,
    val effectValue: String?,
    val price: Int,
    val category: Category,
    val type: String
)

@Composable
fun ShopScreen(
    userId: String,
    statusViewModel: StatusViewModel,
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

    val context = LocalContext.current

    // Загрузить монеты при запуске
    LaunchedEffect(Unit) {
        viewModel.loadUserCoins(userId)
    }

    val coins by viewModel.userCoins.collectAsState(initial = 0)

    val items = remember {
        listOf(
            ShopItem("coffee", "Кофе с молоком", "Энергия на весь день", "x2_exp_day", 35, Category.EFFECTS, "buff"),
            ShopItem("leprechaun_pot", "Горшочек лепрекона", "Удача сегодня со мной", "x2_coins_day", 35, Category.EFFECTS, "buff"),
            ShopItem("book", "Книга знаний", "+2 XP к интеллекту", "+2_intellect", 20, Category.GOODIES, "stat"),
            ShopItem("vitamin", "Витаминка", "+2 к здоровью", "+2_health", 20, Category.GOODIES, "stat"),
            ShopItem("bar", "Протеиновый батончик", "+2 к силе", "+2_strength", 20, Category.GOODIES, "stat"),
            ShopItem("chest_common", "Обычный сундук", "1 случайный предмет", "chest_common", 25, Category.CHESTS, "chest"),
            ShopItem("chest_epic", "Эпический сундук", "2 предмета + шанс на бустер", "chest_epic", 50, Category.CHESTS, "chest"),
            ShopItem("chest_legendary", "Легендарный сундук", "4 предмета + перманентный буфер", "chest_legendary", 100, Category.CHESTS, "chest"),
        )
    }

    var selectedCategory by remember { mutableStateOf(Category.EFFECTS) }
    var selectedItemToBuy by remember { mutableStateOf<ShopItem?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 64.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

            Text(
                text = "Монеты: $coins",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Category.values().forEach { category ->
                    Text(
                        text = category.label,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { selectedCategory = category }
                            .background(if (selectedCategory == category) Color(0xFF4813B2) else Color.LightGray)
                            .padding(8.dp),
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(modifier = Modifier.weight(1f)) {
                items(items.filter { it.category == selectedCategory }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(item.name, fontWeight = FontWeight.Bold)
                            Text(item.description)
                            Text("Эффект: ${item.effectValue}")
                            Text("Цена: ${item.price} монет", fontWeight = FontWeight.Bold)
                            Button(onClick = {
                                if (coins >= item.price) {
                                    selectedItemToBuy = item
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Недостаточно монет для покупки",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }) {
                                Text("Купить")
                            }
                        }
                    }
                }
            }

            selectedItemToBuy?.let { item ->
                ConfirmDialog(
                    title = "Подтвердите покупку",
                    text = "Вы точно хотите купить \"${item.name}\" за ${item.price} монет?",
                    onConfirm = {
                        viewModel.addItem(
                            UserInventoryItemEntity(
                                userId = userId,
                                name = item.name,
                                description = item.description,
                                type = item.type,
                                effectValue = item.effectValue,
                                isConsumed = false
                            )
                        )
                        viewModel.spendCoins(userId, item.price, statusViewModel)
                        statusViewModel.reloadUser()

                        selectedItemToBuy = null
                    },
                    onDismiss = {
                        selectedItemToBuy = null
                    }
                )
            }
        }
    }
}

