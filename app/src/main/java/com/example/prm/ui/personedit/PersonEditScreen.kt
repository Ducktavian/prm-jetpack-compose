package com.example.prm.ui.personedit

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.prm.R
import com.example.prm.data.repository.PersonRepository
import com.example.prm.ui.components.DateField
import com.example.prm.ui.components.DropdownField
import com.example.prm.ui.components.FormSectionHeader
import com.example.prm.ui.components.FormTextField
import com.example.prm.ui.theme.PRMTheme
import kotlinx.coroutines.launch

// Entry point used by navigation: connects the ViewModel to the UI and
// handles leaving the screen (Cancel, system back, after Save).
@Composable
fun PersonEditScreen(
    viewModel: PersonEditViewModel,
    onDone: () -> Unit = {}
) {
    var showDiscardDialog by remember { mutableStateOf(false) }

    // Ask before throwing away edits; leave right away if nothing changed
    val requestClose: () -> Unit = {
        if (viewModel.hasChanges) {
            showDiscardDialog = true
        } else {
            onDone()
        }
    }

    // System back button / gesture behaves the same as Cancel
    BackHandler(onBack = requestClose)

    PersonEditContent(
        form = viewModel.form,
        errors = viewModel.errors,
        isPersonFound = viewModel.isPersonFound,
        onFormChange = viewModel::onFormChange,
        onAddPhone = viewModel::addPhone,
        onPhoneChange = viewModel::onPhoneChange,
        onRemovePhone = viewModel::removePhone,
        onAddEmail = viewModel::addEmail,
        onEmailChange = viewModel::onEmailChange,
        onRemoveEmail = viewModel::removeEmail,
        onSave = viewModel::save,
        onSaved = onDone,
        onCancel = requestClose
    )

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text(text = "Discard changes?") },
            text = { Text(text = "Your edits to this person won't be saved.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDiscardDialog = false
                        onDone()
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(text = "Discard")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text(text = "Keep editing")
                }
            }
        )
    }
}

// The actual form UI. Takes plain data + callbacks (no ViewModel) so it can be previewed.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonEditContent(
    form: PersonForm,
    errors: PersonFormErrors,
    isPersonFound: Boolean,
    onFormChange: (PersonForm.() -> PersonForm) -> Unit,
    onAddPhone: () -> Unit,
    onPhoneChange: (Int, String) -> Unit,
    onRemovePhone: (Int) -> Unit,
    onAddEmail: () -> Unit,
    onEmailChange: (Int, String) -> Unit,
    onRemoveEmail: (Int) -> Unit,
    // Returns true if saved
    onSave: () -> Boolean,
    onSaved: () -> Unit,
    onCancel: () -> Unit
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    TextButton(onClick = onCancel) {
                        Text(text = "< Cancel", fontWeight = FontWeight.Bold)
                    }
                }
            )
        },
        bottomBar = {
            if (isPersonFound) {
                SaveBar(
                    onClick = {
                        if (onSave()) {
                            onSaved()
                        } else {
                            // Required fields are at the top; scroll up so the errors are visible
                            scope.launch { scrollState.animateScrollTo(0) }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        if (!isPersonFound) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Person not found",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Name & relationship (required)
            FormTextField(
                label = "First name",
                value = form.firstName,
                onValueChange = { onFormChange { copy(firstName = it) } },
                required = true,
                error = errors.firstName,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
            )
            FormTextField(
                label = "Last name",
                value = form.lastName,
                onValueChange = { onFormChange { copy(lastName = it) } },
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
            )
            DropdownField(
                label = "Relationship",
                selected = form.category,
                options = RELATIONSHIP_OPTIONS,
                onSelected = { onFormChange { copy(category = it) } },
                required = true,
                error = errors.category
            )
            // Only shown when "Other / Custom" is picked
            if (form.category == OTHER_RELATIONSHIP) {
                FormTextField(
                    label = "Custom relationship",
                    value = form.customCategory,
                    onValueChange = { onFormChange { copy(customCategory = it) } },
                    required = true,
                    error = errors.customCategory,
                    placeholder = "e.g. Professor, Neighbor, Client...",
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
                )
            }

            // 2. Relationship details
            FormSectionHeader(title = "Relationship details", modifier = Modifier.padding(top = 8.dp))
            FormTextField(
                label = "Workplace or school",
                value = form.workplaceOrSchool,
                onValueChange = { onFormChange { copy(workplaceOrSchool = it) } }
            )
            FormTextField(
                label = "How you met",
                value = form.howYouMet,
                onValueChange = { onFormChange { copy(howYouMet = it) } }
            )
            DateField(
                label = "Date you met",
                isoDate = form.dateYouMet,
                onDateSelected = { onFormChange { copy(dateYouMet = it) } }
            )

            // 3. Personal info
            FormSectionHeader(title = "Personal info", modifier = Modifier.padding(top = 8.dp))
            DateField(
                label = "Birthday",
                isoDate = form.birthday,
                onDateSelected = { onFormChange { copy(birthday = it) } }
            )
            FormTextField(
                label = "Pronouns",
                value = form.pronouns,
                onValueChange = { onFormChange { copy(pronouns = it) } }
            )
            FormTextField(
                label = "Gender",
                value = form.gender,
                onValueChange = { onFormChange { copy(gender = it) } }
            )
            FormTextField(
                label = "Occupation",
                value = form.occupation,
                onValueChange = { onFormChange { copy(occupation = it) } }
            )

            // 4. Contact & more
            FormSectionHeader(title = "Contact & more", modifier = Modifier.padding(top = 8.dp))
            RepeatingFields(
                label = "Phone",
                values = form.phones,
                keyboardType = KeyboardType.Phone,
                onValueChange = onPhoneChange,
                onRemove = onRemovePhone,
                onAdd = onAddPhone
            )
            RepeatingFields(
                label = "Email",
                values = form.emails,
                keyboardType = KeyboardType.Email,
                onValueChange = onEmailChange,
                onRemove = onRemoveEmail,
                onAdd = onAddEmail
            )
            FormTextField(
                label = "Location",
                value = form.location,
                onValueChange = { onFormChange { copy(location = it) } }
            )
            DropdownField(
                label = "Preferred contact",
                selected = form.preferredContact,
                options = PREFERRED_CONTACT_OPTIONS,
                onSelected = { onFormChange { copy(preferredContact = it) } }
            )
            FormTextField(
                label = "Skills",
                value = form.skills,
                onValueChange = { onFormChange { copy(skills = it) } },
                placeholder = "Separate with commas, e.g. UI/UX, Figma"
            )
            FormTextField(
                label = "Interests",
                value = form.interests,
                onValueChange = { onFormChange { copy(interests = it) } },
                placeholder = "Separate with commas, e.g. Typography, Design"
            )
            FormTextField(
                label = "Notes",
                value = form.notes,
                onValueChange = { onFormChange { copy(notes = it) } },
                singleLine = false,
                minLines = 3
            )
        }
    }
}

// One text box per entry (phones, emails), each with a remove button, plus "+ Add"
@Composable
private fun RepeatingFields(
    label: String,
    values: List<String>,
    keyboardType: KeyboardType,
    onValueChange: (Int, String) -> Unit,
    onRemove: (Int) -> Unit,
    onAdd: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        values.forEachIndexed { index, value ->
            FormTextField(
                // "Phone" when there's one, "Phone 1", "Phone 2"... when there are more
                label = if (values.size > 1) "$label ${index + 1}" else label,
                value = value,
                onValueChange = { onValueChange(index, it) },
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                trailingIcon = {
                    IconButton(onClick = { onRemove(index) }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_close),
                            contentDescription = "Remove ${label.lowercase()}"
                        )
                    }
                }
            )
        }
        TextButton(onClick = onAdd) {
            Text(text = "+ Add ${label.lowercase()}", fontWeight = FontWeight.Bold)
        }
    }
}

// Full-width Save button pinned to the bottom; rises above the keyboard when it opens
@Composable
private fun SaveBar(onClick: () -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surface) {
        Column(
            modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars.union(WindowInsets.ime))
        ) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Button(
                onClick = onClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(text = "Save", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 1900)
@Composable
fun PersonEditPreview() {
    PRMTheme {
        PersonEditContent(
            form = PersonRepository.getPeople().first().toForm(),
            errors = PersonFormErrors(),
            isPersonFound = true,
            onFormChange = {},
            onAddPhone = {},
            onPhoneChange = { _, _ -> },
            onRemovePhone = {},
            onAddEmail = {},
            onEmailChange = { _, _ -> },
            onRemoveEmail = {},
            onSave = { true },
            onSaved = {},
            onCancel = {}
        )
    }
}
