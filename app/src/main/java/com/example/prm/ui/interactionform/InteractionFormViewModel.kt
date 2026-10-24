package com.example.prm.ui.interactionform

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.prm.data.model.Interaction
import com.example.prm.data.model.Person
import com.example.prm.data.repository.PersonRepository
import com.example.prm.navigation.Routes
import com.example.prm.ui.form.FormState

// One instance per Add/Edit Interaction screen. personId (and interactionId when editing)
// come from the route. No interactionId = Add mode.
class InteractionFormViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val personRepository = PersonRepository

    private val personId: Int? = savedStateHandle[Routes.PERSON_ID]
    private val interactionId: Int? = savedStateHandle[Routes.INTERACTION_ID]

    val isEditing: Boolean = interactionId != null

    // Who the interaction is with; null if not found
    private val person: Person? = personId?.let { personRepository.getPersonById(it) }

    // The interaction being edited, as it was when the screen opened
    private val original: Interaction? = interactionId?.let { id ->
        person?.interactions?.find { it.id == id }
    }

    // False if the person (or, when editing, the interaction) doesn't exist
    val isFound: Boolean = person != null && (!isEditing || original != null)

    // e.g. "Mia Santos", for "With Mia Santos" and the confirmation text
    val personName: String = person?.fullName ?: ""

    // Form contents + errors; Edit mode starts filled in from the interaction
    private val formState = FormState(
        initial = original?.toForm() ?: InteractionForm(),
        noErrors = InteractionFormErrors(),
        validator = { it.validate() }
    )

    val form: InteractionForm
        get() = formState.form

    val errors: InteractionFormErrors
        get() = formState.errors

    val hasChanges: Boolean
        get() = isFound && formState.hasChanges

    // Generic field update, e.g. onFormChange { copy(date = newDate) }
    fun onFormChange(update: InteractionForm.() -> InteractionForm) = formState.update(update)

    // Checks required fields and shows any errors. Returns true if the form is valid.
    fun validate(): Boolean = formState.validate()

    // Validates, then adds (Add mode) or updates (Edit mode) the interaction.
    // Returns true if saved.
    fun save(): Boolean {
        val pId = personId ?: return false
        if (!isFound || !validate()) return false

        val existing = original
        return if (existing == null) {
            personRepository.addInteraction(pId, form.toInteraction()) != null
        } else {
            personRepository.updateInteraction(pId, form.toInteraction(id = existing.id))
            true
        }
    }
}
