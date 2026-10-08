package com.example.prm.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.prm.data.model.Person

@Composable
fun PersonCard(person: Person) {

    val initials = "${person.firstName.take(1)}${person.lastName.take(1)}"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(width = 2.dp, color = Color.Black)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth(0.5f),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            CircularAvatar(initials = initials)

            // Vertical [Name, and info]
            Column(
                modifier = Modifier
            ) {
                Text(text = "${person.firstName} ${person.lastName}")
                Text(text = person.relationship.category)
            }

        }

        // View button
        Button(onClick = { /*TODO*/ }) {
            Text(text = ">")
        }
    }

}