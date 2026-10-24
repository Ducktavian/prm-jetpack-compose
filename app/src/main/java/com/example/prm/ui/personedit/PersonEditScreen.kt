package com.example.prm.ui.personedit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.prm.ui.components.BottomActionBar
import com.example.prm.ui.components.DateField
import com.example.prm.ui.components.DropdownField
import com.example.prm.ui.components.FormSectionHeader
import com.example.prm.ui.components.FormTextField
import com.example.prm.ui.form.FormFlow
import com.example.prm.ui.form.OTHER_OPTION
import com.example.prm.ui.form.SaveConfirmation
import com.example.prm.ui.theme.PRMTheme
import kotlinx.coroutines.launch

// Entry point used by navigation: connects the ViewModel to the UI and
// handles leaving the screen (Cancel, system back, after Save).
@Composable
fun PersonEditScreen(
    viewModel: PersonEditViewModel,
    // Leave without saving (Cancel / back, or Save with nothing changed)
    onDone: () -> Unit = {},
    // Leave after a confirmed save
    onSaved: () -> Unit = {}
) {
    FormFlow(
        isEditing = !viewModel.isNewPerson,
        hasChanges = { viewModel.hasChanges },
        validate = viewModel::validate,
        save = viewModel::save,
        // Same sheet for Add and Edit, with wording for each
        saveConfirmation = {
            if (viewModel.isNewPerson) {
                SaveConfirmation(
                    title = "Add this person?",
                    message = "Confirm that you want to add this person to your network.",
                    confirmText = "Add Person"
                )
            } else {
                SaveConfirmation(
                    title = "Save changes?",
                    message = "This will update the person's information with your changes.",
                    confirmText = "Save Changes"
                )
            }
        },
        discardMessage = "Your edits to this person won't be saved.",
        onDone = onDone,
        onSaved = onSaved
    ) { onSaveClick, onClose ->
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
            onSaveClick = onSaveClick,
            onCancel = onClose
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
    // Returns false if the form has errors
    onSaveClick: () -> Boolean,
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
                BottomActionBar(
                    text = "Save",
                    onClick = {
                        if (!onSaveClick()) {
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
                placeholder = "e.g. Alex",
                required = true,
                error = errors.firstName,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
            )
            FormTextField(
                label = "Last name",
                value = form.lastName,
                onValueChange = { onFormChange { copy(lastName = it) } },
                placeholder = "e.g. Rivera",
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
            if (form.category == OTHER_OPTION) {
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
                onValueChange = { onFormChange { copy(workplaceOrSchool = it) } },
                placeholder = "e.g. Tech Solutions Inc."
            )
            FormTextField(
                label = "How you met",
                value = form.howYouMet,
                onValueChange = { onFormChange { copy(howYouMet = it) } },
                placeholder = "e.g. College, conference, mutual friend"
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
                onValueChange = { onFormChange { copy(pronouns = it) } },
                placeholder = "e.g. She/her, He/him, They/them"
            )
            FormTextField(
                label = "Gender",
                value = form.gender,
                onValueChange = { onFormChange { copy(gender = it) } },
                placeholder = "Optional"
            )
            FormTextField(
                label = "Occupation",
                value = form.occupation,
                onValueChange = { onFormChange { copy(occupation = it) } },
                placeholder = "e.g. Software engineer"
            )

            // 4. Contact & more
            FormSectionHeader(title = "Contact & more", modifier = Modifier.padding(top = 8.dp))
            RepeatingFields(
                label = "Phone",
                values = form.phones,
                placeholder = "+63 917 000 0000",
                keyboardType = KeyboardType.Phone,
                onValueChange = onPhoneChange,
                onRemove = onRemovePhone,
                onAdd = onAddPhone
            )
            RepeatingFields(
                label = "Email",
                values = form.emails,
                placeholder = "name@example.com",
                keyboardType = KeyboardType.Email,
                onValueChange = onEmailChange,
                onRemove = onRemoveEmail,
                onAdd = onAddEmail
            )
            FormTextField(
                label = "Location",
                value = form.location,
                onValueChange = { onFormChange { copy(location = it) } },
                placeholder = "e.g. Quezon City, Philippines"
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
                placeholder = "e.g. React, Public Speaking"
            )
            FormTextField(
                label = "Interests",
                value = form.interests,
                onValueChange = { onFormChange { copy(interests = it) } },
                placeholder = "e.g. Photography, Hiking"
            )
            FormTextField(
                label = "Notes",
                value = form.notes,
                onValueChange = { onFormChange { copy(notes = it) } },
                placeholder = "Anything worth remembering...",
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
    placeholder: String,
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
                placeholder = placeholder,
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
            onSaveClick = { true },
            onCancel = {}
        )
    }
}
