package com.example.prm.ui.persondetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.prm.data.model.Person
import com.example.prm.data.repository.PersonRepository
import com.example.prm.navigation.Routes

// One instance per detail screen. Navigation passes the route arguments
// (e.g. personId from "person_detail/3") in through savedStateHandle.
class PersonDetailViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val personRepository = PersonRepository()

    private val personId: Int? = savedStateHandle[Routes.PERSON_ID]

    // null if the id is missing or no person matches it
    val person: Person? = personId?.let { personRepository.getPersonById(it) }
}
