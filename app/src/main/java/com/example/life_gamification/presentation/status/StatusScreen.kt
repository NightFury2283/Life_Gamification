package com.example.life_gamification.presentation.status

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.life_gamification.R

@Composable
fun StatusScreen(navController: NavController) {
    val expandedStats = remember { mutableStateOf(true) }
    val expandedDailies = remember { mutableStateOf(true) }
    val expandedTasks = remember { mutableStateOf(true) }

    // TODO: Подключить ViewModel и Room для загрузки данных
    val level = 5
    val experience = 1200
    val coins = 999
    val stats = listOf("Сила" to 10, "Интеллект" to 7, "Выносливость" to 9)
    val dailies = listOf("Почистить зубы", "Сделать зарядку")
    val tasks = listOf("Сделать домашку", "Купить продукты")

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
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            item {
                Text(
                    text = "STATUS",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
//                    border = BorderStroke(2.dp, Color.White)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Уровень:", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("$level", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Опыт: $experience XP", color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Монеты: $coins", color = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color.Yellow
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ExpandableSection(
                    title = "ХАРАКТЕРИСТИКИ",
                    expanded = expandedStats.value,
                    onToggle = { expandedStats.value = !expandedStats.value }
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        stats.forEach { (name, value) ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("$name: $value", color = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                            Button(onClick = { /*TODO*/ }) {
                                Text("Добавить")
                            }
                            Button(onClick = { /*TODO*/ }) {
                                Text("Удалить")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                ExpandableSection(
                    title = "ЕЖЕДНЕВКИ",
                    expanded = expandedDailies.value,
                    onToggle = { expandedDailies.value = !expandedDailies.value }
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        dailies.forEach {
                            Text("- $it", color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { /*TODO*/ }) {
                            Text("Добавить ежедневку")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Cyan.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "ВНИМАНИЕ! - За невыполнение ежедневных заданий последует соответствующее наказание.",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                ExpandableSection(
                    title = "ЗАДАЧИ: СЕГОДНЯ",
                    expanded = expandedTasks.value,
                    onToggle = { expandedTasks.value = !expandedTasks.value }
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        tasks.forEach {
                            Text("- $it", color = Color.White)
                        }
                    }
                }
            }
        }

        BottomNavBar(navController = navController)
    }
}

@Composable
fun ExpandableSection(title: String, expanded: Boolean, onToggle: () -> Unit, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .padding(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggle() }
        ) {
//            Icon(
//                imageVector = if (expanded) Icons.Default.ExpandMore else Icons.Default.KeyboardArrowRight,
//                contentDescription = null,
//                tint = Color.White
//            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(title, fontWeight = FontWeight.Bold, color = Color.White)
        }
        if (expanded) content()
    }
}

@Composable
fun BottomNavBar(navController: NavController) {
    Row(
        modifier = Modifier
            //.align(Alignment.BottomCenter)
            .fillMaxWidth()
            .padding(8.dp)
            .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
//        val icons = listOf(Icons.Default.Person, Icons.Default.List, Icons.Default.ShoppingCart, Icons.Default.Inventory, Icons.Default.Settings)
//        icons.forEachIndexed { index, icon ->
//            Box(
//                modifier = Modifier
//                    .size(40.dp)
//                    .clip(RoundedCornerShape(8.dp))
//                    .background(Color.White.copy(alpha = if (index == 0) 0.3f else 0f))
//                    .clickable { /* TODO: навигация */ },
//                contentAlignment = Alignment.Center
//            ) {
//                Icon(icon, contentDescription = null, tint = Color.White)
//            }
//        }
    }
}
