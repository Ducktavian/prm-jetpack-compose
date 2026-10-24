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
) {
    // "Alex Santos"; just "Alex" when there's no last name
    val fullName: String
        get() = "$firstName $lastName".trim()

    // "AS", shown in the avatar circle
    val initials: String
        get() = "${firstName.take(1)}${lastName.take(1)}"
}
