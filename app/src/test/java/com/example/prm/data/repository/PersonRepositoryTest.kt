package com.example.prm.data.repository

import com.example.prm.data.model.Person
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PersonRepositoryTest {
    private lateinit var originalPeople: List<Person>

    // PersonRepository is a shared object, so put back whatever a test changed
    @Before
    fun setUp() {
        originalPeople = PersonRepository.getPeople()
    }

    @After
    fun tearDown() {
        originalPeople.forEach { PersonRepository.updatePerson(it) }
    }

    @Test
    fun updatePerson_replacesOnlyThatPerson() {
        val original = originalPeople.first()
        val edited = original.copy(firstName = "Edited")

        PersonRepository.updatePerson(edited)

        assertEquals("Edited", PersonRepository.getPersonById(original.id)?.firstName)
        assertEquals(originalPeople.size, PersonRepository.getPeople().size)
        // Everyone else is untouched
        assertEquals(originalPeople.drop(1), PersonRepository.getPeople().drop(1))
    }

    @Test
    fun updatePerson_updatesObservableList() {
        val original = originalPeople.first()

        PersonRepository.updatePerson(original.copy(lastName = "Changed"))

        val fromFlow = PersonRepository.people.value.first { it.id == original.id }
        assertEquals("Changed", fromFlow.lastName)
    }

    @Test
    fun updatePerson_unknownId_changesNothing() {
        val stranger = originalPeople.first().copy(id = -1)

        PersonRepository.updatePerson(stranger)

        assertEquals(originalPeople, PersonRepository.getPeople())
    }
}
