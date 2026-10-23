package com.example.prm.ui.personedit

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.prm.data.model.Person
import com.example.prm.data.repository.PersonRepository
import com.example.prm.navigation.Routes

// One instance per edit screen. personId comes from the route, like PersonDetailViewModel.
class PersonEditViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val personRepository = PersonRepository

    private val personId: Int? = savedStateHandle[Routes.PERSON_ID]

    // The person as they were when the screen opened; null if not found
    private val original: Person? = personId?.let { personRepository.getPersonById(it) }

    val isPersonFound: Boolean = original != null

    var form by mutableStateOf(original?.toForm() ?: PersonForm())
        private set

    var errors by mutableStateOf(PersonFormErrors())
        private set

    // After the first Save attempt, errors update live as the user fixes fields
    private var saveAttempted = false

    // True if anything differs from what was loaded (used for "Discard changes?")
    val hasChanges: Boolean
        get() = original != null && form != original.toForm()

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

    // Validates, then saves to the repository.
    // Returns true if saved (the screen then navigates back), false if there are errors.
    fun save(): Boolean {
        val person = original ?: return false
        if (!validate()) return false

        personRepository.updatePerson(form.applyTo(person))
        return true
    }

    private fun List<String>.replaceAt(index: Int, value: String) =
        mapIndexed { i, old -> if (i == index) value else old }

    private fun List<String>.withoutIndex(index: Int) =
        filterIndexed { i, _ -> i != index }
}
