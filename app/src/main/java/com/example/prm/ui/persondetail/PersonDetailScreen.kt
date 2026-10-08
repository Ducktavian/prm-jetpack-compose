package com.example.prm.ui.persondetail

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun PersonDetailScreen(
    personId: Int,
    viewModel: PersonDetailViewModel
) {
    val person = viewModel.getPersonById(personId)

    if (person != null) {
        Text(
            text = "${person.firstName} ${person.lastName}"
        )
    }
}