package com.example.life_gamification.presentation.Tasks


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.life_gamification.presentation.common.TaskItem
import com.example.life_gamification.presentation.common.formatToString
import com.example.life_gamification.presentation.common.rememberDatePickerDialog

@Composable
fun TasksScreen(
    userId: String,
    viewModel: TasksViewModel = viewModel(
        factory = TasksViewModelFactory(
            context = LocalContext.current,
            userId = userId
        )
    )
) {
    val selectedDate by viewModel.selectedDate.collectAsState()
    val tasks by viewModel.tasks.collectAsState()
    val datePickerDialog = rememberDatePickerDialog(
        onDateSelected = { date ->
            viewModel.setDate(date)
        }
    )
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Text(
                text = "Tasks",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Button(
                onClick = { datePickerDialog.show() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(selectedDate.formatToString())
            }

            if (tasks.isEmpty()) {
                Text(
                    text = "Нет задач на выбранную дату",
                    color = Color.Gray,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(tasks) { task ->
                        Card(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            TaskItem(
                                task = task,
                                onCompleteChange = { isChecked ->
                                    if (isChecked) viewModel.completeTask(task)
                                },
                                onDelete = {
                                    viewModel.deleteTask(task)
                                },
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}