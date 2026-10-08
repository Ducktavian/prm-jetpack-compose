package com.example.prm.ui.personlist

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.example.prm.ui.components.PersonCard

@Composable
fun PersonListScreen(
    viewModel: PersonListViewModel
) {
    val people = viewModel.people

    LazyColumn {
        items(people) { person ->
            PersonCard(person = person)
        }
    }
}