package com.example.prm.ui.persondetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonDetailScreen(
    personId: Int,
    viewModel: PersonDetailViewModel,
    onBackClick: () -> Unit = {}
) {
    val person = viewModel.getPersonById(personId)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = person?.let { "${it.firstName} ${it.lastName}" } ?: "Details") },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text("< Back")
                    }
                },
                actions = {
                    TextButton(onClick = { /* Handle edit action */ }) {
                        Text("Edit")
                    }
                    TextButton(onClick = { /* Handle delete action */ }) {
                        Text("Delete")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (person != null) {
                Text(
                    text = "${person.firstName} ${person.lastName}"
                )
            }
        }
    }
}
