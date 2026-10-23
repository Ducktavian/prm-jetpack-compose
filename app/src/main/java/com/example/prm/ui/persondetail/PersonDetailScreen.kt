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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.prm.data.model.Person
import com.example.prm.data.repository.PersonRepository
import com.example.prm.ui.theme.PRMTheme

// Entry point used by navigation: pulls the person from the ViewModel
@Composable
fun PersonDetailScreen(
    viewModel: PersonDetailViewModel,
    onBackClick: () -> Unit = {}
) {
    PersonDetailContent(
        person = viewModel.person,
        onBackClick = onBackClick
    )
}

// The actual UI. Takes plain data (no ViewModel) so it can be previewed.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonDetailContent(
    person: Person?,
    onBackClick: () -> Unit = {}
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
                    TextButton(onClick = { /* Handle edit action */ }) {
                        Text(text = "Edit", fontWeight = FontWeight.Bold)
                    }
                    TextButton(
                        onClick = { /* Handle delete action */ },
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
            item { InteractionsSection(interactions = person.interactions) }
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
