package com.example.prm.ui.personlist

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.prm.data.model.Person
import com.example.prm.data.repository.PersonRepository

class PersonListViewModel : ViewModel() {
    private val personRepository = PersonRepository()

    private val allPeople = personRepository.getPeople()

    var searchQuery by mutableStateOf("")
        private set

    // Re-filters only when searchQuery changes
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
