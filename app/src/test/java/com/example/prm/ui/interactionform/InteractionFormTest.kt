package com.example.prm.ui.interactionform

import androidx.lifecycle.SavedStateHandle
import com.example.prm.data.repository.PersonRepository
import com.example.prm.navigation.Routes
import com.example.prm.ui.form.OTHER_OPTION
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class InteractionFormTest {

    // PersonRepository is a shared object, so start and end each test with the sample data
    @Before
    fun setUp() {
        PersonRepository.resetToSampleData()
    }

    @After
    fun tearDown() {
        PersonRepository.resetToSampleData()
    }

    @Test
    fun validate_requiresDateAndTime() {
        val errors = InteractionForm().validate()

        assertTrue(errors.hasErrors)
        assertNotNull(errors.date)
        assertNotNull(errors.time)
        assertNull(errors.type) // "Meeting" is preselected
    }

    @Test
    fun toInteraction_blankTitle_usesType() {
        val interaction = InteractionForm(date = "2026-10-01", time = "09:00", type = "Call", notes = "  Hi ")
            .toInteraction()

        assertEquals("Call", interaction.title)
        assertEquals("Hi", interaction.details)
    }

    @Test
    fun toInteraction_keepsTypedTitle() {
        val interaction = InteractionForm(date = "2026-10-01", time = "09:00", title = " Coffee chat ")
            .toInteraction()

        assertEquals("Coffee chat", interaction.title)
    }

    @Test
    fun viewModel_save_addsInteractionToPerson() {
        val person = PersonRepository.getPeople().first()
        val viewModel = InteractionFormViewModel(SavedStateHandle(mapOf(Routes.PERSON_ID to person.id)))
        viewModel.onFormChange { copy(date = "2099-01-01", time = "10:30", title = "Future chat") }

        assertTrue(viewModel.save())

        val updated = PersonRepository.getPersonById(person.id)!!
        assertEquals(person.interactions.size + 1, updated.interactions.size)
        // Newest first, so a future date lands at the top
        assertEquals("Future chat", updated.interactions.first().title)
    }

    @Test
    fun viewModel_missingDate_doesNotSave() {
        val person = PersonRepository.getPeople().first()
        val viewModel = InteractionFormViewModel(SavedStateHandle(mapOf(Routes.PERSON_ID to person.id)))
        viewModel.onFormChange { copy(time = "10:30") }

        assertFalse(viewModel.save())
        assertNotNull(viewModel.errors.date)
        assertEquals(person, PersonRepository.getPersonById(person.id))
    }

    @Test
    fun viewModel_personName_isFullName() {
        val person = PersonRepository.getPeople().first()
        val viewModel = InteractionFormViewModel(SavedStateHandle(mapOf(Routes.PERSON_ID to person.id)))

        assertEquals("${person.firstName} ${person.lastName}", viewModel.personName)
        assertTrue(viewModel.isFound)
    }

    @Test
    fun otherType_savesCustomTextAndRequiresIt() {
        val form = InteractionForm(date = "2026-10-01", time = "09:00", type = OTHER_OPTION)
        assertNotNull(form.validate().customType)

        val interaction = form.copy(customType = " Study Session ").toInteraction()
        assertEquals("Study Session", interaction.type)
        assertEquals("Study Session", interaction.title) // title falls back to the custom type
    }

    @Test
    fun toForm_customTypeOpensAsOther_andFallbackTitleIsCleared() {
        val interaction = InteractionForm(
            date = "2026-09-10", time = "14:00",
            type = OTHER_OPTION, customType = "Study Session"
        ).toInteraction(id = 5)

        val form = interaction.toForm()
        assertEquals(OTHER_OPTION, form.type)
        assertEquals("Study Session", form.customType)
        assertEquals("", form.title)
        // Round trip keeps everything
        assertEquals(interaction, form.toInteraction(id = 5))
    }

    @Test
    fun editMode_loadsInteractionAndSavesChanges() {
        val person = PersonRepository.getPeople().first()
        val original = person.interactions.first()
        val viewModel = InteractionFormViewModel(
            SavedStateHandle(mapOf(Routes.PERSON_ID to person.id, Routes.INTERACTION_ID to original.id))
        )

        assertTrue(viewModel.isEditing)
        assertTrue(viewModel.isFound)
        assertEquals(original.date, viewModel.form.date)
        assertFalse(viewModel.hasChanges)

        viewModel.onFormChange { copy(notes = "Updated notes") }
        assertTrue(viewModel.save())

        val updated = PersonRepository.getPersonById(person.id)!!
        assertEquals(person.interactions.size, updated.interactions.size)
        val saved = updated.interactions.first { it.id == original.id }
        assertEquals("Updated notes", saved.details)
        assertEquals(original.title, saved.title)
    }

    @Test
    fun editMode_unknownInteraction_isNotFound() {
        val person = PersonRepository.getPeople().first()
        val viewModel = InteractionFormViewModel(
            SavedStateHandle(mapOf(Routes.PERSON_ID to person.id, Routes.INTERACTION_ID to -1))
        )

        assertFalse(viewModel.isFound)
        assertFalse(viewModel.save())
    }
}
