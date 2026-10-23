package com.example.prm.ui.personedit

import androidx.lifecycle.SavedStateHandle
import com.example.prm.data.model.Person
import com.example.prm.data.repository.PersonRepository
import com.example.prm.navigation.Routes
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PersonEditViewModelTest {
    private lateinit var originalPeople: List<Person>
    private lateinit var person: Person

    // PersonRepository is a shared object, so start and end each test with the sample data
    @Before
    fun setUp() {
        PersonRepository.resetToSampleData()
        originalPeople = PersonRepository.getPeople()
        person = originalPeople.first()
    }

    @After
    fun tearDown() {
        PersonRepository.resetToSampleData()
    }

    private fun viewModelFor(personId: Int) =
        PersonEditViewModel(SavedStateHandle(mapOf(Routes.PERSON_ID to personId)))

    @Test
    fun opens_withPersonLoadedAndNoChanges() {
        val viewModel = viewModelFor(person.id)

        assertTrue(viewModel.isPersonFound)
        assertEquals(person.firstName, viewModel.form.firstName)
        assertFalse(viewModel.hasChanges)
    }

    @Test
    fun unknownId_isNotFoundAndCannotSave() {
        val viewModel = viewModelFor(-1)

        assertFalse(viewModel.isPersonFound)
        assertFalse(viewModel.save())
    }

    @Test
    fun save_validForm_updatesRepository() {
        val viewModel = viewModelFor(person.id)
        viewModel.onFormChange { copy(firstName = "Edited", skills = "Kotlin, Compose") }

        assertTrue(viewModel.hasChanges)
        assertTrue(viewModel.save())

        val saved = PersonRepository.getPersonById(person.id)!!
        assertEquals("Edited", saved.firstName)
        assertEquals(listOf("Kotlin", "Compose"), saved.skills)
        assertEquals(person.interactions, saved.interactions)
    }

    @Test
    fun save_invalidForm_showsErrorsAndDoesNotSave() {
        val viewModel = viewModelFor(person.id)
        viewModel.onFormChange { copy(firstName = "") }

        assertFalse(viewModel.save())
        assertNotNull(viewModel.errors.firstName)
        assertEquals(person, PersonRepository.getPersonById(person.id))

        // Error clears as soon as the field is fixed
        viewModel.onFormChange { copy(firstName = "Fixed") }
        assertNull(viewModel.errors.firstName)
    }

    @Test
    fun validate_checksWithoutSaving() {
        val viewModel = viewModelFor(person.id)
        viewModel.onFormChange { copy(firstName = "Edited") }

        // Valid form: validate passes but nothing is saved until save() is called
        assertTrue(viewModel.validate())
        assertEquals(person, PersonRepository.getPersonById(person.id))

        viewModel.onFormChange { copy(firstName = "") }
        assertFalse(viewModel.validate())
        assertNotNull(viewModel.errors.firstName)
    }

    @Test
    fun phones_addEditRemove() {
        val viewModel = viewModelFor(person.id)
        val startCount = viewModel.form.phones.size

        viewModel.addPhone()
        viewModel.onPhoneChange(startCount, "0999")
        assertEquals("0999", viewModel.form.phones.last())

        viewModel.removePhone(0)
        assertEquals(startCount, viewModel.form.phones.size)
        assertEquals("0999", viewModel.form.phones.last())
    }
}
