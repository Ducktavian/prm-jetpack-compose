package com.example.prm.ui.personedit

import com.example.prm.data.model.Person

val RELATIONSHIP_OPTIONS = listOf("Friend", "Family", "Classmate", "Coworker", "Mentor", "Professional")
val PREFERRED_CONTACT_OPTIONS = listOf("Phone", "Email")

// What the edit form holds while the user types. Everything is plain text so the
// fields can hold "in-progress" values (e.g. "UI/UX, Fig"); it becomes a Person on save.
data class PersonForm(
    // Name & relationship
    val firstName: String = "",
    val lastName: String = "",
    val category: String = "",

    // Relationship details
    val workplaceOrSchool: String = "",
    val howYouMet: String = "",
    val dateYouMet: String = "",        // "yyyy-MM-dd"

    // Personal info
    val birthday: String = "",          // "yyyy-MM-dd"
    val pronouns: String = "",
    val gender: String = "",
    val occupation: String = "",

    // Contact & more
    val phones: List<String> = listOf(""), // one text box per entry
    val emails: List<String> = listOf(""),
    val location: String = "",
    val preferredContact: String = "",
    val skills: String = "",            // comma-separated, e.g. "UI/UX, Figma"
    val interests: String = "",
    val notes: String = ""
)

// Errors to show under fields; null means that field is fine
data class PersonFormErrors(
    val firstName: String? = null,
    val category: String? = null
) {
    val hasErrors: Boolean get() = firstName != null || category != null
}

// Person -> form (when the edit screen opens)
fun Person.toForm(): PersonForm = PersonForm(
    firstName = firstName,
    lastName = lastName,
    category = relationship.category,
    workplaceOrSchool = relationship.workplaceOrSchool,
    howYouMet = relationship.howYouMet,
    dateYouMet = relationship.dateYouMet,
    birthday = personalInfo.birthday,
    pronouns = personalInfo.pronouns,
    gender = personalInfo.gender,
    occupation = personalInfo.occupation,
    // Always show at least one box so there's somewhere to type
    phones = contact.phones.ifEmpty { listOf("") },
    emails = contact.emails.ifEmpty { listOf("") },
    location = contact.location,
    preferredContact = contact.preferredContact,
    skills = skills.joinToString(", "),
    interests = interests.joinToString(", "),
    notes = notes
)

// Form -> updated Person (on save). Starts from the original so anything the form
// doesn't edit (id, interactions) is kept as-is. Text is trimmed, blanks dropped.
fun PersonForm.applyTo(person: Person): Person = person.copy(
    firstName = firstName.trim(),
    lastName = lastName.trim(),
    relationship = person.relationship.copy(
        category = category.trim(),
        workplaceOrSchool = workplaceOrSchool.trim(),
        howYouMet = howYouMet.trim(),
        dateYouMet = dateYouMet
    ),
    personalInfo = person.personalInfo.copy(
        birthday = birthday,
        pronouns = pronouns.trim(),
        gender = gender.trim(),
        occupation = occupation.trim()
    ),
    contact = person.contact.copy(
        phones = phones.cleaned(),
        emails = emails.cleaned(),
        location = location.trim(),
        preferredContact = preferredContact
    ),
    skills = splitCommaList(skills),
    interests = splitCommaList(interests),
    notes = notes.trim()
)

fun PersonForm.validate(): PersonFormErrors = PersonFormErrors(
    firstName = if (firstName.isBlank()) "First name is required" else null,
    category = if (category.isBlank()) "Relationship is required" else null
)

// "UI/UX,  Figma, , Prototyping" -> ["UI/UX", "Figma", "Prototyping"]
fun splitCommaList(text: String): List<String> =
    text.split(",").cleaned()

private fun List<String>.cleaned(): List<String> =
    map { it.trim() }.filter { it.isNotEmpty() }
