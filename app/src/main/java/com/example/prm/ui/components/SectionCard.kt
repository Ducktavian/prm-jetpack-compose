package com.example.prm.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.prm.ui.theme.AppTextStyles
import com.example.prm.ui.theme.PRMTheme

@Composable
fun SectionCard(
    title: String,
    modifier: Modifier = Modifier,
    action: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        // 1. Colored header: title + optional action (e.g. "+ Add")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primary)
                .heightIn(min = 36.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title.uppercase(),
                color = MaterialTheme.colorScheme.onPrimary,
                style = AppTextStyles.sectionHeader,
                // Lets screen-reader users jump between sections
                modifier = Modifier
                    .weight(1f)
                    .semantics { heading() }
            )
            if (action != null) {
                // Plain Text/Icon inside the action becomes readable on the header color
                CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onPrimary) {
                    action()
                }
            }
        }

        // 2. Body: no padding here, so InfoRow dividers can span the full width.
        // Callers that need padding (chips, notes) add it themselves.
        Column(content = content)
    }
}

@Preview(showBackground = true)
@Composable
fun SectionCardPreview() {
    PRMTheme(dynamicColor = false) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionCard(title = "Relationship") {
                InfoRow(label = "Category", value = "Classmate", showDivider = false)
                InfoRow(label = "Work / School", value = "Northbridge University")
                InfoRow(label = "How you met", value = "Capstone design course")
            }
            SectionCard(
                title = "Skills",
                modifier = Modifier.padding(top = 16.dp),
                action = { Text(text = "+ Add", fontWeight = FontWeight.Bold) }
            ) {
                Row(modifier = Modifier.padding(12.dp)) {
                    TagChip(text = "UI/UX")
                    TagChip(text = "Figma", modifier = Modifier.padding(start = 6.dp))
                }
            }
        }
    }
}
