package com.example.prm.ui.personedit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.prm.data.model.Person
import com.example.prm.data.repository.PersonRepository
import com.example.prm.navigation.Routes

// One instance per edit/add screen. personId comes from the route, like PersonDetailViewModel.
// No personId (the "person_add" route) means Add mode: blank form, save creates a new person.
class PersonEditViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val personRepository = PersonRepository

    private val personId: Int? = savedStateHandle[Routes.PERSON_ID]

    val isNewPerson: Boolean = personId == null

    // The person as they were when the screen opened; null in Add mode or if not found
    private val original: Person? = personId?.let { personRepository.getPersonById(it) }

    // False only when editing an id that doesn't exist
    val isPersonFound: Boolean = isNewPerson || original != null

    // What the form started with; Add mode starts blank with "Friend" preselected
    private val initialForm: PersonForm = original?.toForm() ?: PersonForm(category = "Friend")

    var form by mutableStateOf(initialForm)
        private set

    var errors by mutableStateOf(PersonFormErrors())
        private set

    // After the first Save attempt, errors update live as the user fixes fields
    private var saveAttempted = false

    // True if anything differs from what the form started with (used for "Discard changes?")
    val hasChanges: Boolean
        get() = isPersonFound && form != initialForm

    // Generic field update, e.g. onFormChange { copy(firstName = newText) }
    fun onFormChange(update: PersonForm.() -> PersonForm) {
        form = form.update()
        if (saveAttempted) errors = form.validate()
    }

    // Phones: one text box per number
    fun addPhone() = onFormChange { copy(phones = phones + "") }
    fun onPhoneChange(index: Int, value: String) =
        onFormChange { copy(phones = phones.replaceAt(index, value)) }
    fun removePhone(index: Int) = onFormChange { copy(phones = phones.withoutIndex(index)) }

    // Emails: one text box per address
    fun addEmail() = onFormChange { copy(emails = emails + "") }
    fun onEmailChange(index: Int, value: String) =
        onFormChange { copy(emails = emails.replaceAt(index, value)) }
    fun removeEmail(index: Int) = onFormChange { copy(emails = emails.withoutIndex(index)) }

    // Checks required fields and shows any errors. Returns true if the form is valid.
    // Called when Save is tapped, before the "Save changes?" confirmation.
    fun validate(): Boolean {
        saveAttempted = true
        errors = form.validate()
        return !errors.hasErrors
    }

    // Validates, then saves to the repository (adds in Add mode, updates otherwise).
    // Returns true if saved (the screen then navigates back), false if there are errors.
    fun save(): Boolean {
        if (!isPersonFound) return false
        if (!validate()) return false

        val person = original
        if (person == null) {
            personRepository.addPerson(form.applyTo(blankPerson()))
        } else {
            personRepository.updatePerson(form.applyTo(person))
        }
        return true
    }

    private fun List<String>.replaceAt(index: Int, value: String) =
        mapIndexed { i, old -> if (i == index) value else old }

    private fun List<String>.withoutIndex(index: Int) =
        filterIndexed { i, _ -> i != index }
}
