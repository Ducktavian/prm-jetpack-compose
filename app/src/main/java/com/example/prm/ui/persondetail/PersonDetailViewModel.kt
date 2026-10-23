package com.example.prm.ui.persondetail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.prm.data.model.Person
import com.example.prm.data.repository.PersonRepository
import com.example.prm.navigation.Routes
import kotlinx.coroutines.launch

// One instance per detail screen. Navigation passes the route arguments
// (e.g. personId from "person_detail/3") in through savedStateHandle.
class PersonDetailViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val personRepository = PersonRepository

    private val personId: Int? = savedStateHandle[Routes.PERSON_ID]

    // null if the id is missing or no person matches it.
    // Updates automatically when the person is edited.
    var person by mutableStateOf(personId?.let { personRepository.getPersonById(it) })
        private set

    init {
        if (personId != null) {
            viewModelScope.launch {
                personRepository.observePerson(personId).collect { person = it }
            }
        }
    }
}
