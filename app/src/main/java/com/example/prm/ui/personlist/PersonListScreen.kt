package com.example.prm.ui.personlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.prm.R
import com.example.prm.data.model.Person
import com.example.prm.ui.components.PersonCard
import com.example.prm.ui.components.SearchField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonListScreen(
    viewModel: PersonListViewModel,
    onPersonClick: (Int) -> Unit = {},
    onAddClick: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "People") },
                actions = {
                    // Round purple "+" button: opens the Add Person form
                    FilledIconButton(
                        onClick = onAddClick,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_add),
                            contentDescription = "Add person"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        PersonListContent(
            people = viewModel.people,
            hasNoPeople = viewModel.hasNoPeople,
            searchQuery = viewModel.searchQuery,
            onSearchQueryChange = viewModel::onSearchQueryChange,
            onPersonClick = onPersonClick,
            modifier = Modifier.padding(innerPadding)
        )
    }
}

// Stateless: everything it shows comes in through parameters
@Composable
fun PersonListContent(
    people: List<Person>,
    hasNoPeople: Boolean,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onPersonClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        // 1. Search bar (fixed while the list scrolls)
        SearchField(
            query = searchQuery,
            onQueryChange = onSearchQueryChange,
            placeholder = "Search",
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // 2. Results
        when {
            hasNoPeople -> EmptyState(
                message = "No people yet. Tap + to add someone."
            )
            people.isEmpty() -> EmptyState(
                message = "No people match \"${searchQuery.trim()}\""
            )
            else -> LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(people, key = { it.id }) { person ->
                    PersonCard(
                        person = person,
                        onViewClick = { onPersonClick(person.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState(message: String) {
    Text(
        text = message,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp)
    )
}
