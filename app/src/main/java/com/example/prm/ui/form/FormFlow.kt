package com.example.prm.ui.form

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.prm.ui.components.ConfirmationSheet
import com.example.prm.ui.components.DiscardChangesDialog

// Wording for the "Save?" confirmation sheet
data class SaveConfirmation(
    val title: String,
    val message: String,
    val confirmText: String
)

// Save / leave behavior shared by the Add/Edit forms (person and interaction):
// - Save: shows errors if invalid; when editing with nothing changed, just leaves;
//   otherwise asks for confirmation, then saves and calls onSaved.
// - Cancel / Back / system back: asks "Discard changes?" only if something changed.
// content receives onSaveClick (returns false when the form has errors) and onClose.
@Composable
fun FormFlow(
    isEditing: Boolean,
    hasChanges: () -> Boolean,
    validate: () -> Boolean,
    save: () -> Boolean,
    // Called when the sheet opens, so the wording can use the current form values
    saveConfirmation: () -> SaveConfirmation,
    discardMessage: String,
    onDone: () -> Unit,
    onSaved: () -> Unit,
    content: @Composable (onSaveClick: () -> Boolean, onClose: () -> Unit) -> Unit
) {
    // Saveable so an open sheet/dialog survives rotation
    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    var showSaveSheet by rememberSaveable { mutableStateOf(false) }

    val onSaveClick: () -> Boolean = {
        when {
            !validate() -> false
            isEditing && !hasChanges() -> {
                onDone()
                true
            }
            else -> {
                showSaveSheet = true
                true
            }
        }
    }

    // Ask before throwing away edits; leave right away if nothing changed
    val onClose: () -> Unit = {
        if (hasChanges()) showDiscardDialog = true else onDone()
    }

    // System back button / gesture behaves the same as Cancel
    BackHandler(onBack = onClose)

    content(onSaveClick, onClose)

    if (showSaveSheet) {
        val text = saveConfirmation()
        ConfirmationSheet(
            title = text.title,
            message = text.message,
            confirmText = text.confirmText,
            destructive = false,
            onConfirm = {
                showSaveSheet = false
                if (save()) onSaved()
            },
            onDismiss = { showSaveSheet = false }
        )
    }

    if (showDiscardDialog) {
        DiscardChangesDialog(
            message = discardMessage,
            onDiscard = {
                showDiscardDialog = false
                onDone()
            },
            onDismiss = { showDiscardDialog = false }
        )
    }
}
