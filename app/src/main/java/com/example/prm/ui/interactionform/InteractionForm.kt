package com.example.prm.ui.interactionform

import com.example.prm.data.model.Interaction

// Picking this in the Type dropdown shows a text box for a custom type (like relationships)
const val OTHER_INTERACTION_TYPE = "Other / Custom"

// Types found in the data, with "Meeting" first as the default (matches the prototype)
val STANDARD_INTERACTION_TYPES = listOf("Meeting", "Meetup", "Call", "Message")
val INTERACTION_TYPES = STANDARD_INTERACTION_TYPES + OTHER_INTERACTION_TYPE

// What the Add/Edit Interaction form holds while the user types
data class InteractionForm(
    val date: String = "",          // "yyyy-MM-dd"
    val time: String = "",          // "HH:mm"
    val type: String = STANDARD_INTERACTION_TYPES.first(), // may be OTHER_INTERACTION_TYPE
    val customType: String = "",    // only used when type == OTHER_INTERACTION_TYPE
    val title: String = "",         // optional; falls back to the type
    val notes: String = ""          // saved as Interaction.details
)

data class InteractionFormErrors(
    val date: String? = null,
    val time: String? = null,
    val type: String? = null,
    val customType: String? = null
) {
    val hasErrors: Boolean
        get() = date != null || time != null || type != null || customType != null
}

fun InteractionForm.validate(): InteractionFormErrors = InteractionFormErrors(
    date = if (date.isBlank()) "Date is required" else null,
    time = if (time.isBlank()) "Time is required" else null,
    type = if (type.isBlank()) "Type is required" else null,
    customType = if (type == OTHER_INTERACTION_TYPE && customType.isBlank()) "Enter a type" else null
)

// The type actually saved: the custom text when "Other / Custom" is picked
fun InteractionForm.resolvedType(): String =
    if (type == OTHER_INTERACTION_TYPE) customType.trim() else type

// Interaction -> form (when editing)
fun Interaction.toForm(): InteractionForm {
    val isStandard = type in STANDARD_INTERACTION_TYPES
    return InteractionForm(
        date = date,
        time = time,
        type = if (isStandard) type else OTHER_INTERACTION_TYPE,
        customType = if (isStandard) "" else type,
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
