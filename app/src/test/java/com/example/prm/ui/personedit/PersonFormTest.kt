package com.example.prm.ui.personedit

import com.example.prm.data.repository.PersonRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class PersonFormTest {
    private val person = PersonRepository.getPeople().first()

    @Test
    fun toFormThenApplyTo_withNoEdits_givesSamePerson() {
        assertEquals(person, person.toForm().applyTo(person))
    }

    @Test
    fun applyTo_keepsIdAndInteractions() {
        val saved = person.toForm().copy(firstName = "New").applyTo(person)

        assertEquals(person.id, saved.id)
        assertEquals(person.interactions, saved.interactions)
        assertEquals("New", saved.firstName)
    }

    @Test
    fun splitCommaList_trimsAndDropsEmptyEntries() {
        assertEquals(
            listOf("UI/UX", "Figma", "Prototyping"),
            splitCommaList("UI/UX,  Figma, , Prototyping,")
        )
        assertEquals(emptyList<String>(), splitCommaList("  "))
    }

    @Test
    fun applyTo_dropsBlankPhonesAndEmails() {
        val form = person.toForm().copy(
            phones = listOf(" 0917 ", ""),
            emails = listOf("")
        )
        val saved = form.applyTo(person)

        assertEquals(listOf("0917"), saved.contact.phones)
        assertEquals(emptyList<String>(), saved.contact.emails)
    }

    @Test
    fun toForm_emptyLists_showOneEmptyBox() {
        val noContact = person.copy(contact = person.contact.copy(phones = emptyList()))
        assertEquals(listOf(""), noContact.toForm().phones)
    }

    @Test
    fun validate_requiresFirstNameAndRelationship() {
        val errors = PersonForm(firstName = "  ", category = "").validate()

        assertTrue(errors.hasErrors)
        assertNotNull(errors.firstName)
        assertNotNull(errors.category)
    }

    @Test
    fun validate_lastNameIsOptional() {
        val errors = PersonForm(firstName = "Alex", lastName = "", category = "Friend").validate()

        assertFalse(errors.hasErrors)
        assertNull(errors.firstName)
    }
}
