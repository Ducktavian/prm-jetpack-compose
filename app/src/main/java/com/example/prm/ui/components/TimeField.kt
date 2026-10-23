package com.example.prm.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
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

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        FormLabel(label = label, required = required)

        Box {
            OutlinedTextField(
                value = if (time24h.isBlank()) "" else formatDisplayTime(time24h),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = placeholder) },
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_clock),
                        contentDescription = null
                    )
                },
                isError = error != null,
                supportingText = error?.let { { Text(text = it) } },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            // Catches taps on the read-only field (see DateField)
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showPicker = true }
            )
        }
    }

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
