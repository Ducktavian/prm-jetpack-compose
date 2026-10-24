package com.example.prm.data.repository

import com.example.prm.data.model.Person
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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
    fun deletePerson_unknownId_changesNothingAndHasNoUndo() {
        val undo = PersonRepository.deletePerson(-1)

        assertNull(undo)
        assertEquals(originalPeople, PersonRepository.getPeople())
    }

    @Test
    fun deletePerson_undo_restoresAtSamePosition() {
        val target = originalPeople[2]

        val undo = PersonRepository.deletePerson(target.id)
        assertNotNull(undo)
        undo!!()

        assertEquals(originalPeople, PersonRepository.getPeople())
    }

    @Test
    fun deletePerson_undoTwice_restoresOnlyOnce() {
        val undo = PersonRepository.deletePerson(originalPeople.first().id)!!

        undo()
        undo()

        assertEquals(originalPeople, PersonRepository.getPeople())
    }

    @Test
    fun deletePerson_undoAfterIdWasReused_keepsBothWithUniqueIds() {
        // Deleting the person with the highest id frees that id for the next new person
        val target = originalPeople.maxBy { it.id }
        val undo = PersonRepository.deletePerson(target.id)!!
        val newId = PersonRepository.addPerson(target.copy(firstName = "Newcomer"))
        assertEquals(target.id, newId)

        undo()

        val people = PersonRepository.getPeople()
        assertEquals(originalPeople.size + 1, people.size)
        assertEquals(people.size, people.map { it.id }.toSet().size)
        assertTrue(people.any { it.firstName == target.firstName && it.id != target.id })
    }

    @Test
    fun deleteInteraction_undo_restoresIt() {
        val person = originalPeople.first()
        val target = person.interactions.first()

        val undo = PersonRepository.deleteInteraction(person.id, target.id)
        assertNotNull(undo)
        undo!!()

        val interactions = PersonRepository.getPersonById(person.id)!!.interactions
        assertEquals(person.interactions.size, interactions.size)
        // Same content is back; only the id may differ
        assertTrue(interactions.any { it.copy(id = 0) == target.copy(id = 0) })
    }

    @Test
    fun deleteInteraction_unknownInteraction_hasNoUndo() {
        assertNull(PersonRepository.deleteInteraction(originalPeople.first().id, -1))
        assertNull(PersonRepository.deleteInteraction(-1, -1))
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
