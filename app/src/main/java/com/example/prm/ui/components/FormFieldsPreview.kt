package com.example.prm.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.prm.ui.theme.PRMTheme

// Preview of all form building blocks together (use Interactive Mode to try them)
@Preview(showBackground = true, heightDp = 900)
@Composable
fun FormFieldsPreview() {
    var firstName by remember { mutableStateOf("Alex") }
    var category by remember { mutableStateOf("Classmate") }
    var dateMet by remember { mutableStateOf("2024-09-03") }
    var notes by remember { mutableStateOf("Works on the same capstone project.") }

    PRMTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FormTextField(
                label = "First name",
                value = firstName,
                onValueChange = { firstName = it },
                required = true,
                error = if (firstName.isBlank()) "First name is required" else null
            )
            DropdownField(
                label = "Relationship",
                selected = category,
                options = listOf("Friend", "Family", "Classmate", "Coworker", "Mentor", "Professional"),
                onSelected = { category = it },
                required = true
            )
            FormSectionHeader(title = "Relationship details")
            DateField(
                label = "Date you met",
                isoDate = dateMet,
                onDateSelected = { dateMet = it }
            )
            FormTextField(
                label = "Notes",
                value = notes,
                onValueChange = { notes = it },
                singleLine = false,
                minLines = 3
            )
        }
    }
}
