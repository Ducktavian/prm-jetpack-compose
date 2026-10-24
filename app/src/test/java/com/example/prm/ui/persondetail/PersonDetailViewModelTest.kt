package com.example.prm.ui.persondetail

import androidx.lifecycle.SavedStateHandle
import com.example.prm.ViewModelTestRule
import com.example.prm.data.model.Person
import com.example.prm.data.repository.PersonRepository
import com.example.prm.navigation.Routes
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PersonDetailViewModelTest {
    // Runs viewModelScope work immediately and clears the ViewModels after each test
    @get:Rule
    val viewModels = ViewModelTestRule()

    private lateinit var person: Person

    // PersonRepository is a shared object, so start and end each test with the sample data
    @Before
    fun setUp() {
        PersonRepository.resetToSampleData()
        person = PersonRepository.getPeople().first()
    }

    @After
    fun tearDown() {
        PersonRepository.resetToSampleData()
    }

    private fun viewModelFor(personId: Int) = viewModels.create {
        PersonDetailViewModel(SavedStateHandle(mapOf(Routes.PERSON_ID to personId)))
    }

    @Test
    fun opens_withPersonFromRoute() {
        assertEquals(person, viewModelFor(person.id).person)
    }

    @Test
    fun unknownId_hasNoPerson() {
        assertNull(viewModelFor(-1).person)
    }

    @Test
    fun personEditedElsewhere_updatesAutomatically() {
        val viewModel = viewModelFor(person.id)

        PersonRepository.updatePerson(person.copy(firstName = "Edited"))

        assertEquals("Edited", viewModel.person?.firstName)
    }

    @Test
    fun deleteInteraction_removesItAndUndoBringsItBack() {
        val viewModel = viewModelFor(person.id)
        val target = person.interactions.first()

        val undo = viewModel.deleteInteraction(target.id)

        assertNotNull(undo)
        assertEquals(person.interactions.size - 1, viewModel.person?.interactions?.size)
        undo!!()
        assertEquals(person.interactions.size, viewModel.person?.interactions?.size)
    }

    @Test
    fun deletePerson_keepsShowingThemWhileScreenCloses() {
        val viewModel = viewModelFor(person.id)

        val undo = viewModel.deletePerson()

        assertNotNull(undo)
        assertNull(PersonRepository.getPersonById(person.id))
        // No "Person not found" flash during the exit animation
        assertEquals(person, viewModel.person)
    }

    @Test
    fun deletePerson_unknownId_hasNoUndo() {
        assertNull(viewModelFor(-1).deletePerson())
    }
}
