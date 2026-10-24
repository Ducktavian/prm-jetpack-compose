package com.example.prm.ui.personlist

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prm.data.model.Person
import com.example.prm.data.repository.PersonRepository
import kotlinx.coroutines.launch

class PersonListViewModel : ViewModel() {
    private val personRepository = PersonRepository

    // Full list, kept in sync with the repository (e.g. after an edit is saved)
    private var allPeople by mutableStateOf(personRepository.getPeople())

    var searchQuery by mutableStateOf("")
        private set

    init {
        viewModelScope.launch {
            personRepository.people.collect { allPeople = it }
        }
    }

    // True when nobody has been added yet (as opposed to a search with no results)
    val hasNoPeople: Boolean
        get() = allPeople.isEmpty()

    // Re-filters only when searchQuery or allPeople changes
    val people: List<Person> by derivedStateOf {
        val query = searchQuery.trim()
        if (query.isEmpty()) allPeople
        else allPeople.filter { it.matches(query) }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
    }

    // Matches name, relationship category, and interests (case-insensitive)
    private fun Person.matches(query: String): Boolean {
        val fields = listOf(
            "$firstName $lastName",
            relationship.category
        ) + interests
        return fields.any { it.contains(query, ignoreCase = true) }
    }
}
