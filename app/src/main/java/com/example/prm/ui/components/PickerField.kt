package com.example.prm.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.error
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

// Read-only field that opens a picker when tapped. Shared by DateField and TimeField,
// which only add their own picker dialog.
@Composable
internal fun PickerField(
    label: String,
    // Already formatted for display (e.g. "Sep 3, 2024"); "" when nothing is picked
    displayValue: String,
    placeholder: String,
    @DrawableRes iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    error: String? = null
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        FormLabel(label = label, required = required)

        Box {
            OutlinedTextField(
                value = displayValue,
                onValueChange = {},
                readOnly = true,
                // Hidden from screen readers: the tap layer below describes the field instead
                modifier = Modifier
                    .fillMaxWidth()
                    .clearAndSetSemantics {},
                placeholder = { Text(text = placeholder) },
                trailingIcon = {
                    Icon(painter = painterResource(iconRes), contentDescription = null)
                },
                isError = error != null,
                supportingText = error?.let { { Text(text = it) } },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
            // A read-only text field swallows taps, so this invisible layer on top catches
            // them. TalkBack reads it as e.g. "Date, Sep 3, 2024, button, double tap to choose date".
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable(
                        role = Role.Button,
                        onClickLabel = "choose ${label.lowercase()}",
                        onClick = onClick
                    )
                    .semantics {
                        contentDescription = "$label, ${displayValue.ifEmpty { placeholder }}"
                        if (error != null) error(error)
                    }
            )
        }
    }
}
