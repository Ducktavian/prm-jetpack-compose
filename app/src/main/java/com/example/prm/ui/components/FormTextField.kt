package com.example.prm.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.example.prm.ui.theme.AppTextStyles

@Composable
fun FormTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    // Shown in red under the field; null means no error
    error: String? = null,
    placeholder: String? = null,
    // Set false + minLines for multi-line input (e.g. Notes)
    singleLine: Boolean = true,
    minLines: Int = 1,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    // Optional icon/button at the end of the box (e.g. remove a phone number)
    trailingIcon: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        FormLabel(label = label, required = required)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = placeholder?.let { { Text(text = it) } },
            trailingIcon = trailingIcon,
            isError = error != null,
            supportingText = error?.let { { Text(text = it) } },
            singleLine = singleLine,
            minLines = minLines,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = keyboardOptions
        )
    }
}

// Bold label above a form field, with a red * when required.
// Shared by FormTextField, DropdownField and DateField.
@Composable
internal fun FormLabel(
    label: String,
    required: Boolean
) {
    val errorColor = MaterialTheme.colorScheme.error
    Text(
        text = buildAnnotatedString {
            append(label)
            if (required) {
                withStyle(SpanStyle(color = errorColor)) { append(" *") }
            }
        },
        color = MaterialTheme.colorScheme.onSurface,
        style = AppTextStyles.fieldLabel,
        // Screen readers say "First name, required" instead of "First name star"
        modifier = Modifier.semantics {
            contentDescription = if (required) "$label, required" else label
        }
    )
}
