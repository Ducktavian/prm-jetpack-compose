package com.example.prm.ui.personlist

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.prm.ui.components.PersonCard
import com.example.prm.ui.components.SearchField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonListScreen(
    viewModel: PersonListViewModel,
    onPersonClick: (Int) -> Unit = {}
) {
    val people = viewModel.people

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "People") }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // 1. Search bar (fixed while the list scrolls)
            SearchField(
                query = viewModel.searchQuery,
                onQueryChange = viewModel::onSearchQueryChange,
                placeholder = "Search",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // 2. Results
            if (people.isEmpty()) {
                Text(
                    text = "No people match \"${viewModel.searchQuery.trim()}\"",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp)
                )
            } else {
                LazyColumn(
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
}
