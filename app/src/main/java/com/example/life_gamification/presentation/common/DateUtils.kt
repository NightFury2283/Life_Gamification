package com.example.life_gamification.presentation.common


import android.app.DatePickerDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun rememberDatePickerDialog(
    onDateSelected: (Date) -> Unit,
    onDismiss: () -> Unit = {}
): DatePickerDialog {
    val context = LocalContext.current
    return remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                val calendar = Calendar.getInstance().apply {
                    set(year, month, day)
                }
                onDateSelected(calendar.time)
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        ).apply {
            setOnDismissListener { onDismiss() }
        }
    }
}

fun Date.formatToString(): String {
    return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(this)
}

fun Long.formatToDateString(): String {
    return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(this)
}