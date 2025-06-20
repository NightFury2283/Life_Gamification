package com.example.life_gamification.presentation.Status

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.life_gamification.R
import com.example.life_gamification.domain.Configs.LevelConfig
import com.example.life_gamification.domain.Configs.StatusConfig.REQUIRED_STATS
import com.example.life_gamification.domain.Configs.isMaxLevel
import com.example.life_gamification.presentation.common.ConfirmDialog
import com.example.life_gamification.presentation.common.formatToDateString
import com.example.life_gamification.presentation.common.formatToString
import com.example.life_gamification.presentation.common.rememberDatePickerDialog
import java.util.Date


@Composable
fun StatusScreen(
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

    //всё для задач
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var newTaskName by remember { mutableStateOf("") }
    var newTaskXp by remember { mutableStateOf("1") }
    var newTaskCoins by remember { mutableStateOf("0") }
    var selectedDate by remember { mutableStateOf<Date?>(null) }
    val tasks by viewModel.tasks.collectAsState()

    val datePickerDialog = rememberDatePickerDialog(
        onDateSelected = { date ->
            selectedDate = date
        }
    )

// Для уровня и прогресс бара
    val currentLevel = viewModel.level.value
    val currentExp = viewModel.experience.value

    val (expForCurrentLevel, expForNextLevel) = remember(currentLevel) {
        val current = LevelConfig.REQUIREMENTS.getOrElse(currentLevel) { 0 }
        val next = if (currentLevel < LevelConfig.MAX_LEVEL) {
            LevelConfig.REQUIREMENTS.getOrElse(currentLevel + 1) { 0 }
        } else {
            0
        }
        current to next
    }

    val nextLevelExp = if (currentLevel >= LevelConfig.MAX_LEVEL) {
        100
    } else {
        maxOf(1, expForNextLevel - expForCurrentLevel)
    }

    val progress = if (isMaxLevel(currentLevel)) {
        1f
    } else {
        val currentExpClamped = maxOf(0, currentExp - expForCurrentLevel)
        currentExpClamped.toFloat() / nextLevelExp.toFloat()
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                "Уровень: $currentLevel",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )

                                if (currentLevel >= LevelConfig.MAX_LEVEL) {
                                    Text("MAX", color = Color.Yellow)
                                }

                        }

                        //прогресс-бар уровня
                        LinearProgressIndicator(
                            progress = progress,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .padding(vertical = 4.dp),
                            color = Color(0xFF4CAF50),
                            trackColor = Color(0xFF2E7D32)
                        )

                        Text(
                            text = if (currentLevel >= LevelConfig.MAX_LEVEL) {
                                "Максимальный уровень достигнут!"
                            } else {
                                "Опыт: $currentExp/$nextLevelExp"
                            },
                            color = Color.White,
                            fontSize = 12.sp,
                            modifier = Modifier.align(Alignment.End))

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
                                val undeletableStats = REQUIRED_STATS
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
                    title = "ЗАДАЧИ",
                    expanded = expandedTasks.value,
                    onToggle = { expandedTasks.value = !expandedTasks.value }
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        tasks.forEach { task ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Checkbox(
                                    checked = task.isCompleted,
                                    onCheckedChange = { isChecked ->
                                        if (isChecked) viewModel.completeTask(task)
                                    },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = Color.White,
                                        uncheckedColor = Color.White,
                                        checkmarkColor = Color.Black
                                    )
                                )

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(task.name, color = Color.White)
                                    Text(
                                        "До: ${task.dueDate.formatToDateString()}",
                                        color = Color.LightGray,
                                        fontSize = 12.sp
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text("+${task.xpReward} XP", color = Color.Green)
                                    Text("+${task.coinsReward} монет", color = Color.Yellow)
                                }

                                IconButton(onClick = {
                                    deletionTarget = DeletionTarget.Task(
                                        name = task.name,
                                        onConfirm = { viewModel.deleteTask(task) }
                                    )
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Удалить", tint = Color.Red)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { showAddTaskDialog = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Добавить задачу")
                        }
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
        if (showAddTaskDialog) {
            AlertDialog(
                onDismissRequest = {
                    showAddTaskDialog = false
                    newTaskName = ""
                    newTaskXp = "1"
                    newTaskCoins = "0"
                    selectedDate = null
                },
                title = { Text("Добавить задачу") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = newTaskName,
                            onValueChange = { newTaskName = it },
                            label = { Text("Название") }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row {
                            OutlinedTextField(
                                value = newTaskXp,
                                onValueChange = { if (it.all { c -> c.isDigit() }) newTaskXp = it },
                                label = { Text("XP") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            OutlinedTextField(
                                value = newTaskCoins,
                                onValueChange = { if (it.all { c -> c.isDigit() }) newTaskCoins = it },
                                label = { Text("Монеты") },
                                modifier = Modifier.weight(1f),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Виджет выбора даты
                        Button(
                            onClick = { datePickerDialog.show() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(selectedDate?.formatToString() ?: "Выберите дату")
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (newTaskName.isNotBlank() && selectedDate != null) {
                                viewModel.addTask(
                                    name = newTaskName,
                                    xp = newTaskXp.toIntOrNull() ?: 1,
                                    coins = newTaskCoins.toIntOrNull() ?: 0,
                                    dueDate = selectedDate!!.time
                                )
                                showAddTaskDialog = false
                                newTaskName = ""
                                newTaskXp = "1"
                                newTaskCoins = "0"
                                selectedDate = null
                            }
                        },
                        enabled = newTaskName.isNotBlank() && selectedDate != null // Добавлена проверка
                    ) {
                        Text("Добавить")
                    }
                },
                dismissButton = {
                    OutlinedButton(onClick = {
                        showAddTaskDialog = false
                        newTaskName = ""
                        newTaskXp = "1"
                        newTaskCoins = "0"
                        selectedDate = null
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
                is DeletionTarget.Task -> "Вы точно хотите удалить задачу \"${target.name}\"?" // Исправлено
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