package com.example.prm.data.model

data class Person (
    val id: Int,
    val firstName: String,
    val lastName: String,
    val contact: Contact,
    val personalInfo: PersonalInfo,
    val relationship: Relationship,
    val interests: List<String>,
    val skills: List<String>,
    val notes: String,
    val interactions: List<Interaction>
)