package com.example.prm.ui.interactionform

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.prm.data.model.Interaction
import com.example.prm.data.model.Person
import com.example.prm.data.repository.PersonRepository
import com.example.prm.navigation.Routes

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
    val personName: String = person?.let { "${it.firstName} ${it.lastName}".trim() } ?: ""

    private val initialForm: InteractionForm = original?.toForm() ?: InteractionForm()

    var form by mutableStateOf(initialForm)
        private set

    var errors by mutableStateOf(InteractionFormErrors())
        private set

    // After the first Save attempt, errors update live as the user fixes fields
    private var saveAttempted = false

    val hasChanges: Boolean
        get() = isFound && form != initialForm

    // Generic field update, e.g. onFormChange { copy(date = newDate) }
    fun onFormChange(update: InteractionForm.() -> InteractionForm) {
        form = form.update()
        if (saveAttempted) errors = form.validate()
    }

    // Checks required fields and shows any errors. Returns true if the form is valid.
    fun validate(): Boolean {
        saveAttempted = true
        errors = form.validate()
        return !errors.hasErrors
    }

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
