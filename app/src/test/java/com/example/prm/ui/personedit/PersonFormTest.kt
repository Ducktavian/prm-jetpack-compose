package com.example.prm.ui.personedit

import com.example.prm.data.repository.PersonRepository
import com.example.prm.ui.form.OTHER_OPTION
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
    fun toForm_nonStandardCategory_opensAsOtherWithCustomText() {
        val neighbor = person.copy(relationship = person.relationship.copy(category = "Neighbor"))
        val form = neighbor.toForm()

        assertEquals(OTHER_OPTION, form.category)
        assertEquals("Neighbor", form.customCategory)
        // And saves back unchanged
        assertEquals(neighbor, form.applyTo(neighbor))
    }

    @Test
    fun toForm_standardCategory_hasNoCustomText() {
        val friend = person.copy(relationship = person.relationship.copy(category = "Friend"))
        val form = friend.toForm()

        assertEquals("Friend", form.category)
        assertEquals("", form.customCategory)
    }

    @Test
    fun applyTo_otherSelected_savesTrimmedCustomText() {
        val form = person.toForm().copy(category = OTHER_OPTION, customCategory = "  Professor ")
        assertEquals("Professor", form.applyTo(person).relationship.category)
    }

    @Test
    fun applyTo_standardSelected_ignoresLeftoverCustomText() {
        // User picked Other, typed something, then switched back to Mentor
        val form = person.toForm().copy(category = "Mentor", customCategory = "Professor")
        assertEquals("Mentor", form.applyTo(person).relationship.category)
    }

    @Test
    fun validate_otherSelected_requiresCustomText() {
        val form = PersonForm(firstName = "Alex", category = OTHER_OPTION, customCategory = " ")
        val errors = form.validate()

        assertTrue(errors.hasErrors)
        assertNotNull(errors.customCategory)
        assertNull(errors.category)
    }

    @Test
    fun validate_lastNameIsOptional() {
        val errors = PersonForm(firstName = "Alex", lastName = "", category = "Friend").validate()

        assertFalse(errors.hasErrors)
        assertNull(errors.firstName)
    }
}
