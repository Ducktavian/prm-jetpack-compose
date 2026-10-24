package com.example.prm.ui.form

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

// Implemented by each form's error class (PersonFormErrors, InteractionFormErrors)
interface FormErrors {
    val hasErrors: Boolean
}

// What an Add/Edit form holds, plus its validation errors. Shared by the person and
// interaction forms (each ViewModel owns one). Errors appear after the first Save
// attempt, then update live as the user fixes fields.
class FormState<F, E : FormErrors>(
    private val initial: F,
    noErrors: E,
    private val validator: (F) -> E
) {
    var form by mutableStateOf(initial)
        private set

    var errors by mutableStateOf(noErrors)
        private set

    private var saveAttempted = false

    // True if anything differs from what the form started with
    val hasChanges: Boolean
        get() = form != initial

    // Generic field update, e.g. update { copy(firstName = newText) }
    fun update(change: F.() -> F) {
        form = form.change()
        if (saveAttempted) errors = validator(form)
    }

    // Shows any errors. Returns true if the form is valid.
    fun validate(): Boolean {
        saveAttempted = true
        errors = validator(form)
        return !errors.hasErrors
    }
}

// Error text for a required field, or null when it's filled in
fun requiredError(value: String, message: String): String? =
    if (value.isBlank()) message else null
