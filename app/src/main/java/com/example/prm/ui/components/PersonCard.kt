package com.example.prm.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.prm.data.model.Person

@Composable
fun PersonCard(
    person: Person,
    modifier: Modifier = Modifier,
    onViewClick: () -> Unit = {}
) {
    val initials = "${person.firstName.take(1)}${person.lastName.take(1)}"

    // Tracks if the card is currently being pressed by the user
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // Dynamic theme-aware colors:
    // Automatically Black in Light Mode, White in Dark Mode!
    val defaultColor = MaterialTheme.colorScheme.onSurface
    val activeColor = MaterialTheme.colorScheme.primary
    val currentColor = if (isPressed) activeColor else defaultColor

    OutlinedCard(
        onClick = onViewClick,
        interactionSource = interactionSource,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(2.dp, currentColor)
    ) {
        // No fillMaxWidth needed: the weighted Column already stretches the Row
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Avatar
            CircularAvatar(initials = initials)

            // 2. Name & Category
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "${person.firstName} ${person.lastName}".trim(),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = person.relationship.category,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // 3. Arrow indicator
            Text(
                text = ">",
                color = currentColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
