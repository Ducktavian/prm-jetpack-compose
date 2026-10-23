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
    fun addPerson_appendsWithNextFreeId() {
        val maxId = originalPeople.maxOf { it.id }

        val newId = PersonRepository.addPerson(originalPeople.first().copy(id = 999, firstName = "New"))

        assertEquals(maxId + 1, newId)
        assertEquals(originalPeople.size + 1, PersonRepository.getPeople().size)
        assertEquals("New", PersonRepository.getPersonById(newId)?.firstName)
        assertNull(PersonRepository.getPersonById(999))
    }

    @Test
    fun addInteraction_givesUniqueIdAndSortsNewestFirst() {
        val person = originalPeople.first()
        val maxId = originalPeople.flatMap { it.interactions }.maxOf { it.id }
        val old = person.interactions.first().copy(id = 0, date = "2000-01-01", title = "Old")

        val newId = PersonRepository.addInteraction(person.id, old)

        assertEquals(maxId + 1, newId)
        val interactions = PersonRepository.getPersonById(person.id)!!.interactions
        assertEquals(person.interactions.size + 1, interactions.size)
        // Oldest date ends up last
        assertEquals("Old", interactions.last().title)
        // Other people untouched
        assertEquals(originalPeople.drop(1), PersonRepository.getPeople().drop(1))
    }

    @Test
    fun addInteraction_unknownPerson_returnsNull() {
        val interaction = originalPeople.first().interactions.first()

        assertNull(PersonRepository.addInteraction(-1, interaction))
        assertEquals(originalPeople, PersonRepository.getPeople())
    }

    @Test
    fun updateInteraction_replacesItAndKeepsOrder() {
        val person = originalPeople.first()
        val target = person.interactions.last()

        PersonRepository.updateInteraction(person.id, target.copy(title = "Renamed", date = "2099-01-01"))

        val interactions = PersonRepository.getPersonById(person.id)!!.interactions
        assertEquals(person.interactions.size, interactions.size)
        // New date is the newest, so it moves to the top
        assertEquals("Renamed", interactions.first().title)
        assertEquals(target.id, interactions.first().id)
    }

    @Test
    fun deleteInteraction_removesOnlyThatOne() {
        val person = originalPeople.first()
        val target = person.interactions.first()

        PersonRepository.deleteInteraction(person.id, target.id)

        val interactions = PersonRepository.getPersonById(person.id)!!.interactions
        assertEquals(person.interactions.drop(1), interactions)
        assertEquals(originalPeople.drop(1), PersonRepository.getPeople().drop(1))
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
