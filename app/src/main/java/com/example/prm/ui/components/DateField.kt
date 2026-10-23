package com.example.prm.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.prm.R
import com.example.prm.util.formatDisplayDate
import com.example.prm.util.isoDateToUtcMillis
import com.example.prm.util.utcMillisToIsoDate

// Shows a stored "yyyy-MM-dd" date as "Sep 3, 2024"; tapping opens a date picker.
// onDateSelected gets the new date back in "yyyy-MM-dd" format.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateField(
    label: String,
    isoDate: String,
    onDateSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    error: String? = null,
    placeholder: String = "Select date"
) {
    var showPicker by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        FormLabel(label = label, required = required)

        Box {
            OutlinedTextField(
                value = if (isoDate.isBlank()) "" else formatDisplayDate(isoDate),
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = placeholder) },
                trailingIcon = {
                    Icon(
                        painter = painterResource(R.drawable.ic_calendar),
                        contentDescription = null
                    )
                },
                isError = error != null,
                supportingText = error?.let { { Text(text = it) } },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            // A read-only text field swallows taps, so this invisible layer
            // on top catches them and opens the picker
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { showPicker = true }
            )
        }
    }

    if (showPicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = isoDateToUtcMillis(isoDate)
        )
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        pickerState.selectedDateMillis?.let { onDateSelected(utcMillisToIsoDate(it)) }
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
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}
