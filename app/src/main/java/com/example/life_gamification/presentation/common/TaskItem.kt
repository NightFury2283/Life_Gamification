package com.example.life_gamification.presentation.common


import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.example.life_gamification.data.local.entity.UserTaskEntity

@Composable
fun TaskItem(
    task: UserTaskEntity,
    onCompleteChange: (Boolean) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = onCompleteChange,
            colors = CheckboxDefaults.colors(
                checkedColor = Color.White,
                uncheckedColor = Color.White,
                checkmarkColor = Color.Black
            )
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(task.name, color = Color.White)
            if (task.dueDate != 0L) {
                Text(
                    "До: ${task.dueDate.formatToDateString()}",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text("+${task.xpReward} XP", color = Color.Green)
            Text("+${task.coinsReward} монет", color = Color.Yellow)
        }

        IconButton(onClick = onDelete) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "Удалить",
                tint = Color.Red
            )
        }
    }
}