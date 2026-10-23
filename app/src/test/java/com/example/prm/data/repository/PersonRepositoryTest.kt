package com.example.prm.data.repository

import com.example.prm.data.model.Person
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class PersonRepositoryTest {
    private lateinit var originalPeople: List<Person>

    // PersonRepository is a shared object, so start and end each test with the sample data
    @Before
    fun setUp() {
        PersonRepository.resetToSampleData()
        originalPeople = PersonRepository.getPeople()
    }

    @After
    fun tearDown() {
        PersonRepository.resetToSampleData()
    }

    @Test
    fun deletePerson_removesOnlyThatPerson() {
        val target = originalPeople.first()

        PersonRepository.deletePerson(target.id)

        assertNull(PersonRepository.getPersonById(target.id))
        assertEquals(originalPeople.drop(1), PersonRepository.getPeople())
    }

    @Test
    fun deletePerson_unknownId_changesNothing() {
        PersonRepository.deletePerson(-1)

        assertEquals(originalPeople, PersonRepository.getPeople())
    }

    @Test
    fun deletePerson_observePersonEmitsNull() = runBlocking {
        val target = originalPeople.first()

        PersonRepository.deletePerson(target.id)

        assertNull(PersonRepository.observePerson(target.id).first())
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
