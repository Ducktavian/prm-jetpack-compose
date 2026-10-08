package com.example.prm.ui.personlist

import androidx.lifecycle.ViewModel
import com.example.prm.data.repository.PersonRepository

class PersonListViewModel : ViewModel() {
    private val personRepository = PersonRepository();

    val people = personRepository.getPeople();
}