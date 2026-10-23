package com.example.prm.ui.persondetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.prm.data.model.Interaction
import com.example.prm.data.model.Person
import com.example.prm.data.repository.PersonRepository
import com.example.prm.ui.components.ConfirmationSheet
import com.example.prm.ui.theme.PRMTheme
import com.example.prm.util.formatDisplayDate
import com.example.prm.util.formatDisplayTime

// Entry point used by navigation: pulls the person from the ViewModel
// and handles the delete confirmations (person and interaction).
@Composable
fun PersonDetailScreen(
    viewModel: PersonDetailViewModel,
    onBackClick: () -> Unit = {},
    onEditClick: (Int) -> Unit = {},
    onAddInteractionClick: (Int) -> Unit = {},
    // (personId, interactionId)
    onEditInteractionClick: (Int, Int) -> Unit = { _, _ -> },
    onInteractionDeleted: () -> Unit = {},
    onDeleted: () -> Unit = {}
) {
    val person = viewModel.person
    var showDeleteSheet by remember { mutableStateOf(false) }
    // The interaction waiting for delete confirmation; null = no sheet
    var interactionToDelete by remember { mutableStateOf<Interaction?>(null) }

    PersonDetailContent(
        person = person,
        onBackClick = onBackClick,
        onEditClick = onEditClick,
        onAddInteractionClick = onAddInteractionClick,
        onEditInteractionClick = { interaction ->
            person?.let { onEditInteractionClick(it.id, interaction.id) }
        },
        onDeleteInteractionClick = { interactionToDelete = it },
        onDeleteClick = { showDeleteSheet = true }
    )

    interactionToDelete?.let { interaction ->
        val whenText = "${formatDisplayDate(interaction.date)} · ${formatDisplayTime(interaction.time)}"
        ConfirmationSheet(
            title = "Delete interaction?",
            message = "This will permanently remove the ${interaction.title.lowercase()} " +
                    "from $whenText. This cannot be undone.",
            confirmText = "Delete Interaction",
            onConfirm = {
                interactionToDelete = null
                viewModel.deleteInteraction(interaction.id)
                onInteractionDeleted()
            },
            onDismiss = { interactionToDelete = null }
        )
    }

    if (showDeleteSheet && person != null) {
        val fullName = "${person.firstName} ${person.lastName}".trim()
        ConfirmationSheet(
            title = "Delete $fullName?",
            message = "This will permanently remove $fullName and all their " +
                    "interaction history. This cannot be undone.",
            confirmText = "Delete",
            onConfirm = {
                showDeleteSheet = false
                viewModel.deletePerson()
                onDeleted()
            },
            onDismiss = { showDeleteSheet = false }
        )
    }
}

// The actual UI. Takes plain data (no ViewModel) so it can be previewed.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonDetailContent(
    person: Person?,
    onBackClick: () -> Unit = {},
    onEditClick: (Int) -> Unit = {},
    onAddInteractionClick: (Int) -> Unit = {},
    onEditInteractionClick: (Interaction) -> Unit = {},
    onDeleteInteractionClick: (Interaction) -> Unit = {},
    onDeleteClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text(text = "< Back", fontWeight = FontWeight.Bold)
                    }
                },
                actions = {
                    TextButton(
                        onClick = { person?.let { onEditClick(it.id) } },
                        enabled = person != null
                    ) {
                        Text(text = "Edit", fontWeight = FontWeight.Bold)
                    }
                    TextButton(
                        onClick = onDeleteClick,
                        enabled = person != null,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text(text = "Delete", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { innerPadding ->
        if (person == null) {
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

        // One scrollable page; each item is a section
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { PersonHeader(person = person) }
            item { RelationshipSection(relationship = person.relationship) }
            item { PersonalInfoSection(personalInfo = person.personalInfo) }
            item { ContactSection(contact = person.contact) }
            item { TagsSection(title = "Skills", tags = person.skills) }
            item { TagsSection(title = "Interests", tags = person.interests) }
            item { NotesSection(notes = person.notes) }
            item {
                InteractionsSection(
                    interactions = person.interactions,
                    onAddClick = { onAddInteractionClick(person.id) },
                    onEditClick = onEditInteractionClick,
                    onDeleteClick = onDeleteInteractionClick
                )
            }
        }
    }
}

@Preview(showBackground = true, heightDp = 1800)
@Composable
fun PersonDetailPreview() {
    PRMTheme {
        PersonDetailContent(person = PersonRepository.getPeople().first())
    }
}

@Preview(showBackground = true)
@Composable
fun PersonDetailNotFoundPreview() {
    PRMTheme {
        PersonDetailContent(person = null)
    }
}
