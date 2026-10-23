package com.example.prm.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

// "Discard changes?" dialog for leaving a form with unsaved edits
@Composable
fun DiscardChangesDialog(
    message: String,
    onDiscard: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Discard changes?") },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(
                onClick = onDiscard,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(text = "Discard")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Keep editing")
            }
        }
    )
}
