package com.example.prm.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.prm.ui.theme.AppTextStyles

@Composable
fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    // Divider is drawn above the row, so pass false for the first row in a section
    showDivider: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (showDivider) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                // Screen readers read the label and value together ("Birthday, May 14, 2002")
                .semantics(mergeDescendants = true) {}
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Label on the left, wraps if long (e.g. "Work / School")
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = AppTextStyles.body,
                modifier = Modifier.weight(0.4f)
            )
            // Value right-aligned
            Text(
                text = value,
                color = MaterialTheme.colorScheme.onSurface,
                style = AppTextStyles.body,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(0.6f)
            )
        }
    }
}
