package com.example.life_gamification.presentation.Status

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.life_gamification.presentation.common.ConfirmDialog

import androidx.compose.ui.res.painterResource
import com.example.life_gamification.R


@Composable
fun StatusScreen(
    navController: NavController,
    userId: String,
    viewModel: StatusViewModel = viewModel(
        factory = StatusViewModelFactory(
            context = LocalContext.current.applicationContext,
            userId = userId
        )
    )
) {
    val expandedStats = remember { mutableStateOf(true) }
    val expandedDailies = remember { mutableStateOf(true) }
    val expandedTasks = remember { mutableStateOf(true) }

    val customStats by viewModel.stats.collectAsState()
    val customDaily by viewModel.daily.collectAsState()

    //диалоговое окно характеристик
    var showAddStatDialog by remember { mutableStateOf(false) }
    var newStatName by remember { mutableStateOf("") }
    val user by viewModel.user.collectAsState()



    //диалоговое окно ежедневок
    var showAddDailyDialog by remember { mutableStateOf(false) }
    var newDailyName by remember { mutableStateOf("") }
    var newDailyXp by remember { mutableStateOf("1") }

    var deletionTarget by remember { mutableStateOf<DeletionTarget?>(null) }

    //кол-во улучшений хар-ик
    val statPoints by viewModel.statPoints
    LaunchedEffect(Unit) {
        viewModel.reloadUser() // Загружаем данные при первом открытии
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
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Уровень: ${user?.level ?: "—"}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold)

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Опыт: ${user?.experience ?: 0} XP",
                                color = Color.White,
                                modifier = Modifier.weight(1f))

                            if (viewModel.hasActiveExpMultiplier()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("x${user?.expMultiplier}",
                                        color = Color.Yellow,
                                        fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_exp),
                                        contentDescription = "Множитель опыта",
                                        tint = Color.Yellow,
                                        modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Монеты: ${user?.money ?: 0}",
                                color = Color.White,
                                modifier = Modifier.weight(1f))

                            if (viewModel.hasActiveCoinsMultiplier()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("x${user?.coinsMultiplier}",
                                        color = Color.Yellow,
                                        fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_coin),
                                        contentDescription = "Множитель монет",
                                        tint = Color.Yellow,
                                        modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                ExpandableSection(
                    title = "ХАРАКТЕРИСТИКИ",
                    expanded = expandedStats.value,
                    onToggle = { expandedStats.value = !expandedStats.value }
                ) {
                    if (statPoints > 0) {
                        Text(
                            text = "Очки для улучшения: $statPoints",
                            color = Color.Yellow,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    Column(modifier = Modifier.padding(8.dp)) {
                        customStats.forEach { stat ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("${stat.name}: ${stat.value}", color = Color.White)
                                Spacer(Modifier.weight(1f))

                                // Показывать "+" если есть доступные очки
                                if (statPoints > 0) {
                                    Button(
                                        onClick = { viewModel.increaseStat(stat) },
                                        modifier = Modifier.size(32.dp),
                                        contentPadding = PaddingValues(0.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                                    ) {
                                        Text("+", color = Color.White)
                                    }
                                }
                                val undeletableStats = listOf("Здоровье", "Сила", "Интеллект")
                                if (stat.name !in undeletableStats) {
                                    IconButton(onClick = {
                                        deletionTarget = DeletionTarget.Stat(
                                            name = stat.name,
                                            onConfirm = { viewModel.deleteStat(stat) }
                                        )
                                    }) {
                                        Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = Color.Red)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                            Button(onClick = { showAddStatDialog = true }) {
                                Text("Добавить")
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
                        customDaily.forEach { daily ->
                            val isChecked = viewModel.isDailyCompletedToday(daily)
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = viewModel.completedDailiesToday.contains(daily.id),
                                    onCheckedChange = { isChecked ->
                                        viewModel.setDailyCompletedToday(daily, isChecked)
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color.White,
                                        uncheckedColor = Color.White,
                                        checkmarkColor = Color.Black
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = daily.name,
                                    color = Color.White,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "+${daily.addXp} XP",
                                    color = Color.Green,
                                    fontWeight = FontWeight.Bold
                                )
                                IconButton(onClick = {
                                    deletionTarget = DeletionTarget.Daily(
                                        name = daily.name,
                                        onConfirm = { viewModel.deleteDaily(daily) }
                                    )
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = Color.Red)
                                }
                            }
                        }


                        Spacer(modifier = Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
                            Button(onClick = { showAddDailyDialog = true }) {
                                Text("Добавить")
                            }
                        }
                    }
                }



                Spacer(modifier = Modifier.height(16.dp))

                ExpandableSection(
                    title = "ЗАДАЧИ НА СЕГОДНЯ",
                    expanded = expandedTasks.value,
                    onToggle = { expandedTasks.value = !expandedTasks.value }
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("Сделать домашку", color = Color.White)
                        Text("Купить продукты", color = Color.White)
                    }
                }
            }
        }

        if (showAddStatDialog) {
            AlertDialog(
                onDismissRequest = { showAddStatDialog = false },
                title = { Text("Добавить характеристику") },
                text = {
                    OutlinedTextField(
                        value = newStatName,
                        onValueChange = { newStatName = it },
                        label = { Text("Название") }
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newStatName.isNotBlank()) {
                                viewModel.addStat(newStatName)
                                newStatName = ""
                                showAddStatDialog = false
                            }
                        }
                    ) {
                        Text("Добавить")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = {
                        showAddStatDialog = false
                        newStatName = ""
                    }) {
                        Text("Отмена")
                    }
                }
            )
        }
        if (showAddDailyDialog) {
            AlertDialog(
                onDismissRequest = {
                    showAddDailyDialog = false
                    newDailyName = ""
                    newDailyXp = "1"
                },
                title = { Text("Добавить ежедневку") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newDailyName,
                            onValueChange = { newDailyName = it },
                            label = { Text("Название") }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = newDailyXp,
                            onValueChange = { newText ->
                                if (newText.all { it.isDigit() }) {
                                    newDailyXp = newText
                                }
                            },
                            label = { Text("XP") },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                keyboardType = KeyboardType.Number
                            )

                        )
                    }
                },
                confirmButton = {
                    Button(onClick = {
                        val xp = newDailyXp.toIntOrNull() ?: 1
                        if (newDailyName.isNotBlank()) {
                            viewModel.addDaily(newDailyName, xp)
                            newDailyName = ""
                            newDailyXp = "1"
                            showAddDailyDialog = false
                        }
                    }) {
                        Text("Добавить")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = {
                        showAddDailyDialog = false
                        newDailyName = ""
                        newDailyXp = "1"
                    }) {
                        Text("Отмена")
                    }
                }
            )
        }
        deletionTarget?.let { target ->
            val title = "Подтвердите удаление"
            val message = when (target) {
                is DeletionTarget.Stat -> "Вы точно хотите удалить характеристику \"${target.name}\"?"
                is DeletionTarget.Daily -> "Вы точно хотите удалить ежедневку \"${target.name}\"?"
            }

            ConfirmDialog(
                title = title,
                text = message,
                onConfirm = {
                    target.onConfirm()
                    deletionTarget = null
                },
                onDismiss = {
                    deletionTarget = null
                }
            )
        }


    }
}

@Composable
fun ExpandableSection(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit
) {
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
            Text(title, fontWeight = FontWeight.Bold, color = Color.White)
        }
        if (expanded) content()
    }
}