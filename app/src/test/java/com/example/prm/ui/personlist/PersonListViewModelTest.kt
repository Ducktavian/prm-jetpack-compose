package com.example.prm.ui.personlist

import com.example.prm.ViewModelTestRule
import com.example.prm.data.model.Person
import com.example.prm.data.repository.PersonRepository
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PersonListViewModelTest {
    // Runs viewModelScope work immediately and clears the ViewModels after each test
    @get:Rule
    val viewModels = ViewModelTestRule()

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

    private fun newViewModel() = viewModels.create { PersonListViewModel() }

    @Test
    fun blankQuery_showsEveryone() {
        val viewModel = newViewModel()

        assertEquals(originalPeople, viewModel.people)
        assertFalse(viewModel.hasNoPeople)
    }

    @Test
    fun searchByName_ignoresCaseAndSpaces() {
        val viewModel = newViewModel()

        viewModel.onSearchQueryChange("  aLeX  ")

        assertEquals(listOf("Alex Santos"), viewModel.people.map { it.fullName })
    }

    @Test
    fun searchByRelationship_matchesCategory() {
        val viewModel = newViewModel()

        viewModel.onSearchQueryChange("mentor")

        assertTrue(viewModel.people.isNotEmpty())
        assertTrue(viewModel.people.all { it.relationship.category.equals("Mentor", ignoreCase = true) })
    }

    @Test
    fun searchByInterest_matchesInterests() {
        val viewModel = newViewModel()

        viewModel.onSearchQueryChange("photography")

        assertTrue(viewModel.people.any { it.fullName == "Alex Santos" })
        assertTrue(viewModel.people.all { person ->
            person.interests.any { it.contains("photography", ignoreCase = true) } ||
                person.fullName.contains("photography", ignoreCase = true) ||
                person.relationship.category.contains("photography", ignoreCase = true)
        })
    }

    @Test
    fun noMatches_isEmptyButStillHasPeople() {
        val viewModel = newViewModel()

        viewModel.onSearchQueryChange("zzzz")

        assertTrue(viewModel.people.isEmpty())
        // The screen shows "No people match" rather than "No people yet"
        assertFalse(viewModel.hasNoPeople)
    }

    @Test
    fun clearingQuery_showsEveryoneAgain() {
        val viewModel = newViewModel()
        viewModel.onSearchQueryChange("alex")

        viewModel.onSearchQueryChange("")

        assertEquals(originalPeople, viewModel.people)
    }

    @Test
    fun repositoryChange_updatesList() {
        val viewModel = newViewModel()
        val target = originalPeople.first()

        PersonRepository.deletePerson(target.id)

        assertEquals(originalPeople.drop(1), viewModel.people)
    }

    @Test
    fun everyoneDeleted_hasNoPeople() {
        val viewModel = newViewModel()

        originalPeople.forEach { PersonRepository.deletePerson(it.id) }

        assertTrue(viewModel.people.isEmpty())
        assertTrue(viewModel.hasNoPeople)
    }
}
