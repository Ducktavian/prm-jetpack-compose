package com.example.prm.ui.persondetail

import androidx.lifecycle.ViewModel
import com.example.prm.data.model.Person
import com.example.prm.data.repository.PersonRepository

class PersonDetailViewModel : ViewModel() {
    private val personRepository = PersonRepository()

    fun getPersonById(personId: Int): Person? {
        return personRepository.getPersonById(personId)
    }
}