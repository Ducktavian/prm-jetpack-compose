package com.example.prm.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.prm.R
import com.example.prm.util.formatDisplayTime
import com.example.prm.util.formatTime24h
import com.example.prm.util.parseTime24h

// Shows a stored "HH:mm" time as "8:30 PM"; tapping opens a clock picker.
// onTimeSelected gets the new time back in "HH:mm" format. Works like DateField.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeField(
    label: String,
    time24h: String,
    onTimeSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    error: String? = null,
    placeholder: String = "Select time"
) {
    var showPicker by remember { mutableStateOf(false) }

    PickerField(
        label = label,
        displayValue = if (time24h.isBlank()) "" else formatDisplayTime(time24h),
        placeholder = placeholder,
        iconRes = R.drawable.ic_clock,
        onClick = { showPicker = true },
        modifier = modifier,
        required = required,
        error = error
    )

    if (showPicker) {
        // Start on the current value, or 12:00 when empty
        val (initialHour, initialMinute) = parseTime24h(time24h) ?: (12 to 0)
        val pickerState = rememberTimePickerState(
            initialHour = initialHour,
            initialMinute = initialMinute,
            is24Hour = false
        )
        AlertDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        onTimeSelected(formatTime24h(pickerState.hour, pickerState.minute))
                        showPicker = false
                    }
                ) {
                    Text(text = "OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text(text = "Cancel")
                }
            },
            text = { TimePicker(state = pickerState) }
        )
    }
}
