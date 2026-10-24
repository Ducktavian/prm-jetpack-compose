package com.example.prm.ui.interactionform

import com.example.prm.data.model.Interaction
import com.example.prm.ui.form.FormErrors
import com.example.prm.ui.form.OTHER_OPTION
import com.example.prm.ui.form.customOptionError
import com.example.prm.ui.form.requiredError
import com.example.prm.ui.form.resolveCustomOption
import com.example.prm.ui.form.splitCustomOption

// Types found in the data, with "Meeting" first as the default (matches the prototype)
val STANDARD_INTERACTION_TYPES = listOf("Meeting", "Meetup", "Call", "Message")
// "Other / Custom" shows a text box for a custom type (like relationships)
val INTERACTION_TYPES = STANDARD_INTERACTION_TYPES + OTHER_OPTION

// What the Add/Edit Interaction form holds while the user types
data class InteractionForm(
    val date: String = "",          // "yyyy-MM-dd"
    val time: String = "",          // "HH:mm"
    val type: String = STANDARD_INTERACTION_TYPES.first(), // may be OTHER_OPTION
    val customType: String = "",    // only used when type == OTHER_OPTION
    val title: String = "",         // optional; falls back to the type
    val notes: String = ""          // saved as Interaction.details
)

data class InteractionFormErrors(
    val date: String? = null,
    val time: String? = null,
    val type: String? = null,
    val customType: String? = null
) : FormErrors {
    override val hasErrors: Boolean
        get() = date != null || time != null || type != null || customType != null
}

fun InteractionForm.validate(): InteractionFormErrors = InteractionFormErrors(
    date = requiredError(date, "Date is required"),
    time = requiredError(time, "Time is required"),
    type = requiredError(type, "Type is required"),
    customType = customOptionError(type, customType, "Enter a type")
)

// The type actually saved: the custom text when "Other / Custom" is picked
fun InteractionForm.resolvedType(): String = resolveCustomOption(type, customType)

// Interaction -> form (when editing)
fun Interaction.toForm(): InteractionForm {
    val (typeChoice, customType) = splitCustomOption(type, STANDARD_INTERACTION_TYPES)
    return InteractionForm(
        date = date,
        time = time,
        type = typeChoice,
        customType = customType,
        // A title that just repeats the type was a fallback, so leave the box empty;
        // that way changing the type also changes the title
        title = if (title == type) "" else title,
        notes = details
    )
}

// Form -> Interaction. Pass the original id when editing; for a new one the id is a
// placeholder and PersonRepository.addInteraction assigns the real one.
fun InteractionForm.toInteraction(id: Int = 0): Interaction {
    val savedType = resolvedType()
    return Interaction(
        id = id,
        type = savedType,
        date = date,
        time = time,
        // The detail page shows the title in bold, so never leave it empty
        title = title.trim().ifEmpty { savedType },
        details = notes.trim()
    )
}
