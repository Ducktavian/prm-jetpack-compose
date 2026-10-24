package com.example.prm.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.prm.ui.theme.AppTextStyles

// Small purple heading with a line under it, e.g. "RELATIONSHIP DETAILS"
@Composable
fun FormSectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = title.uppercase(),
            color = MaterialTheme.colorScheme.primary,
            style = AppTextStyles.sectionHeader,
            // Lets screen-reader users jump between form sections
            modifier = Modifier.semantics { heading() }
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
    }
}
