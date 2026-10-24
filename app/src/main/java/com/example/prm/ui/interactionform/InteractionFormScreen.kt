package com.example.prm.ui.interactionform

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prm.ui.components.BottomActionBar
import com.example.prm.ui.components.ConfirmationSheet
import com.example.prm.ui.components.DateField
import com.example.prm.ui.components.DiscardChangesDialog
import com.example.prm.ui.components.DropdownField
import com.example.prm.ui.components.FormTextField
import com.example.prm.ui.components.TimeField
import com.example.prm.ui.theme.PRMTheme

// Entry point used by navigation: connects the ViewModel to the UI and
// handles leaving the screen (Back, after Save).
@Composable
fun InteractionFormScreen(
    viewModel: InteractionFormViewModel,
    // Leave without saving
    onDone: () -> Unit = {},
    // Leave after a confirmed save
    onSaved: () -> Unit = {}
) {
    // Saveable so an open sheet/dialog survives rotation
    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    var showSaveSheet by rememberSaveable { mutableStateOf(false) }

    // Ask before throwing away input; leave right away if nothing was entered
    val requestClose: () -> Unit = {
        if (viewModel.hasChanges) {
            showDiscardDialog = true
        } else {
            onDone()
        }
    }

    BackHandler(onBack = requestClose)

    InteractionFormContent(
        personName = viewModel.personName,
        isEditing = viewModel.isEditing,
        isFound = viewModel.isFound,
        form = viewModel.form,
        errors = viewModel.errors,
        onFormChange = viewModel::onFormChange,
        // Errors show under the fields; only a valid form gets the confirmation.
        // Editing with nothing changed just leaves.
        onSaveClick = {
            when {
                !viewModel.validate() -> Unit
                viewModel.isEditing && !viewModel.hasChanges -> onDone()
                else -> showSaveSheet = true
            }
        },
        onBackClick = requestClose
    )

    if (showSaveSheet) {
        val form = viewModel.form
        val name = viewModel.personName
        ConfirmationSheet(
            title = if (viewModel.isEditing) "Save interaction changes?" else "Add interaction?",
            message = if (viewModel.isEditing) {
                "This will update the ${form.toInteraction().title} interaction with $name."
            } else {
                "This will add a new ${form.resolvedType().lowercase()} with $name."
            },
            confirmText = if (viewModel.isEditing) "Save Changes" else "Add Interaction",
            destructive = false,
            onConfirm = {
                showSaveSheet = false
                if (viewModel.save()) onSaved()
            },
            onDismiss = { showSaveSheet = false }
        )
    }

    if (showDiscardDialog) {
        DiscardChangesDialog(
            message = "This interaction won't be saved.",
            onDiscard = {
                showDiscardDialog = false
                onDone()
            },
            onDismiss = { showDiscardDialog = false }
        )
    }
}

// The actual form UI. Takes plain data + callbacks (no ViewModel) so it can be previewed.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InteractionFormContent(
    personName: String,
    isEditing: Boolean,
    isFound: Boolean,
    form: InteractionForm,
    errors: InteractionFormErrors,
    onFormChange: (InteractionForm.() -> InteractionForm) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = if (isEditing) "Edit Interaction" else "Add Interaction",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text(text = "< Back", fontWeight = FontWeight.Bold)
                    }
                }
            )
        },
        bottomBar = {
            if (isFound) {
                BottomActionBar(
                    text = if (isEditing) "Save Changes" else "Save Interaction",
                    onClick = onSaveClick
                )
            }
        }
    ) { innerPadding ->
        if (!isFound) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isEditing) "Interaction not found" else "Person not found",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "With $personName",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp
            )
            DateField(
                label = "Date",
                isoDate = form.date,
                onDateSelected = { onFormChange { copy(date = it) } },
                required = true,
                error = errors.date
            )
            TimeField(
                label = "Time",
                time24h = form.time,
                onTimeSelected = { onFormChange { copy(time = it) } },
                required = true,
                error = errors.time
            )
            DropdownField(
                label = "Type",
                selected = form.type,
                options = INTERACTION_TYPES,
                onSelected = { onFormChange { copy(type = it) } },
                required = true,
                error = errors.type
            )
            // Only shown when "Other / Custom" is picked
            if (form.type == OTHER_INTERACTION_TYPE) {
                FormTextField(
                    label = "Custom type",
                    value = form.customType,
                    onValueChange = { onFormChange { copy(customType = it) } },
                    required = true,
                    error = errors.customType,
                    placeholder = "e.g. Study session, Coffee chat...",
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                )
            }
            // Not in the prototype, but Interaction has a title (shown in bold on the detail page)
            FormTextField(
                label = "Title",
                value = form.title,
                onValueChange = { onFormChange { copy(title = it) } },
                placeholder = "e.g. Coffee chat (defaults to the type)"
            )
            FormTextField(
                label = "Notes",
                value = form.notes,
                onValueChange = { onFormChange { copy(notes = it) } },
                placeholder = "What did you talk about? Any follow-ups?",
                singleLine = false,
                minLines = 4
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InteractionFormPreview() {
    PRMTheme {
        InteractionFormContent(
            personName = "Mia Santos",
            isEditing = false,
            isFound = true,
            form = InteractionForm(),
            errors = InteractionFormErrors(),
            onFormChange = {},
            onSaveClick = {},
            onBackClick = {}
        )
    }
}
