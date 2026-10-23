package com.example.prm.ui.persondetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prm.R
import com.example.prm.data.model.Contact
import com.example.prm.data.model.Interaction
import com.example.prm.data.model.Person
import com.example.prm.data.model.PersonalInfo
import com.example.prm.data.model.Relationship
import com.example.prm.ui.components.CircularAvatar
import com.example.prm.ui.components.InfoRow
import com.example.prm.ui.components.SectionCard
import com.example.prm.ui.components.TagChip
import com.example.prm.util.formatDisplayDate
import com.example.prm.util.formatDisplayTime

// Sections used only by PersonDetailScreen.
// Reusable building blocks (SectionCard, InfoRow, TagChip) live in ui/components.

@Composable
fun PersonHeader(person: Person) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CircularAvatar(
            initials = "${person.firstName.take(1)}${person.lastName.take(1)}",
            size = 64.dp
        )
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "${person.firstName} ${person.lastName}".trim(),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
            TagChip(text = person.relationship.category)
        }
    }
}

@Composable
fun RelationshipSection(relationship: Relationship) {
    SectionCard(title = "Relationship") {
        InfoRow(label = "Category", value = relationship.category, showDivider = false)
        InfoRow(label = "Work / School", value = relationship.workplaceOrSchool)
        InfoRow(label = "How you met", value = relationship.howYouMet)
        InfoRow(label = "Date met", value = formatDisplayDate(relationship.dateYouMet))
    }
}

@Composable
fun PersonalInfoSection(personalInfo: PersonalInfo) {
    SectionCard(title = "Personal Info") {
        InfoRow(
            label = "Birthday",
            value = formatDisplayDate(personalInfo.birthday),
            showDivider = false
        )
        InfoRow(label = "Pronouns", value = personalInfo.pronouns)
        InfoRow(label = "Gender", value = personalInfo.gender)
        InfoRow(label = "Occupation", value = personalInfo.occupation)
    }
}

@Composable
fun ContactSection(contact: Contact) {
    // One row per phone/email, then location and preferred contact
    val rows = contact.phones.map { "Phone" to it } +
            contact.emails.map { "Email" to it } +
            listOf(
                "Location" to contact.location,
                "Preferred" to contact.preferredContact
            )

    SectionCard(title = "Contact") {
        rows.forEachIndexed { index, (label, value) ->
            InfoRow(label = label, value = value, showDivider = index > 0)
        }
    }
}

// Used for both Skills and Interests
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagsSection(
    title: String,
    tags: List<String>
) {
    SectionCard(title = title) {
        if (tags.isEmpty()) {
            EmptySectionText(text = "None yet")
        } else {
            // Wraps chips onto the next line when the row is full
            FlowRow(
                modifier = Modifier.padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                tags.forEach { TagChip(text = it) }
            }
        }
    }
}

@Composable
fun NotesSection(notes: String) {
    SectionCard(title = "Notes") {
        if (notes.isBlank()) {
            EmptySectionText(text = "No notes")
        } else {
            Text(
                text = notes,
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}

@Composable
fun InteractionsSection(
    interactions: List<Interaction>,
    onAddClick: () -> Unit = {},
    onEditClick: (Interaction) -> Unit = {},
    onDeleteClick: (Interaction) -> Unit = {}
) {
    SectionCard(
        title = "Interactions",
        action = {
            TextButton(
                onClick = onAddClick,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(text = "+ Add", fontWeight = FontWeight.Bold)
            }
        }
    ) {
        if (interactions.isEmpty()) {
            EmptySectionText(text = "No interactions yet")
        } else {
            interactions.forEachIndexed { index, interaction ->
                if (index > 0) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                }
                InteractionItem(
                    interaction = interaction,
                    onEditClick = { onEditClick(interaction) },
                    onDeleteClick = { onDeleteClick(interaction) }
                )
            }
        }
    }
}

@Composable
private fun InteractionItem(
    interaction: Interaction,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 12.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = interaction.title,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Text(
                text = "${formatDisplayDate(interaction.date)} · ${formatDisplayTime(interaction.time)}",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 12.sp
            )
            // Notes are optional, so skip the empty line when there are none
            if (interaction.details.isNotBlank()) {
                Text(
                    text = interaction.details,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp
                )
            }
        }
        InteractionMenu(onEditClick = onEditClick, onDeleteClick = onDeleteClick)
    }
}

// "..." button that opens Edit / Delete for one interaction
@Composable
private fun InteractionMenu(
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                painter = painterResource(R.drawable.ic_more_horiz),
                contentDescription = "Interaction options",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text(text = "Edit", fontWeight = FontWeight.Bold) },
                onClick = {
                    expanded = false
                    onEditClick()
                }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Delete",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.Bold
                    )
                },
                onClick = {
                    expanded = false
                    onDeleteClick()
                }
            )
        }
    }
}

@Composable
private fun EmptySectionText(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 14.sp,
        modifier = Modifier.padding(12.dp)
    )
}
