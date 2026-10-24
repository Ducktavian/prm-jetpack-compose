package com.example.prm.data.repository

import com.example.prm.data.model.Contact
import com.example.prm.data.model.Interaction
import com.example.prm.data.model.Person
import com.example.prm.data.model.PersonalInfo
import com.example.prm.data.model.Relationship
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

// Single shared instance (object), so every screen reads and edits the same data
object PersonRepository {

    private val mockPeople = listOf(
        Person(
            id = 1,
            firstName = "Alex",
            lastName = "Santos",
            contact = Contact(
                emails = listOf("alex.santos@email.com"),
                phones = listOf("+63 917 123 4567"),
                location = "Quezon City, Philippines",
                preferredContact = "Email"
            ),
            personalInfo = PersonalInfo(
                birthday = "2002-05-14",
                pronouns = "he/him",
                gender = "Male",
                occupation = "Software Developer"
            ),
            relationship = Relationship(
                category = "Friend",
                workplaceOrSchool = "Tech Solutions Inc.",
                howYouMet = "College",
                dateYouMet = "2021-08-20"
            ),
            interests = listOf(
                "Technology",
                "Photography",
                "Gaming"
            ),
            skills = listOf(
                "JavaScript",
                "Web Development",
                "Photography"
            ),
            notes = "Alex is easy to talk to and often shares interesting programming projects.",
            interactions = listOf(
                Interaction(
                    id = 1001,
                    type = "Message",
                    date = "2026-09-18",
                    time = "20:30",
                    title = "Caught up about work",
                    details = "Talked about Alex's new web development project and upcoming plans."
                ),
                Interaction(
                    id = 1002,
                    type = "Meetup",
                    date = "2026-08-30",
                    time = "15:00",
                    title = "Coffee meetup",
                    details = "Met at a cafe and talked about work, games, and college."
                )
            )
        ),

        Person(
            id = 2,
            firstName = "Mia",
            lastName = "Reyes",
            contact = Contact(
                emails = listOf("mia.reyes@email.com"),
                phones = listOf("+63 918 234 5678"),
                location = "Makati, Philippines",
                preferredContact = "Phone"
            ),
            personalInfo = PersonalInfo(
                birthday = "2001-11-03",
                pronouns = "she/her",
                gender = "Female",
                occupation = "Graphic Designer"
            ),
            relationship = Relationship(
                category = "Friend",
                workplaceOrSchool = "Creative Studio",
                howYouMet = "University organization",
                dateYouMet = "2022-02-10"
            ),
            interests = listOf(
                "Design",
                "Music",
                "Travel"
            ),
            skills = listOf(
                "Graphic Design",
                "Illustration",
                "Branding"
            ),
            notes = "Mia enjoys discovering new cafes and frequently works on creative projects.",
            interactions = listOf(
                Interaction(
                    id = 2001,
                    type = "Message",
                    date = "2026-09-15",
                    time = "18:45",
                    title = "Shared design ideas",
                    details = "Exchanged ideas about improving portfolio designs."
                )
            )
        ),

        Person(
            id = 3,
            firstName = "Daniel",
            lastName = "Cruz",
            contact = Contact(
                emails = listOf("daniel.cruz@email.com"),
                phones = listOf("+63 919 345 6789"),
                location = "Pasig, Philippines",
                preferredContact = "Phone"
            ),
            personalInfo = PersonalInfo(
                birthday = "1998-03-21",
                pronouns = "he/him",
                gender = "Male",
                occupation = "Project Manager"
            ),
            relationship = Relationship(
                category = "Mentor",
                workplaceOrSchool = "Digital Works",
                howYouMet = "Internship",
                dateYouMet = "2023-06-12"
            ),
            interests = listOf(
                "Leadership",
                "Technology",
                "Business"
            ),
            skills = listOf(
                "Project Management",
                "Leadership",
                "Communication"
            ),
            notes = "Daniel has given useful advice about career development and project management.",
            interactions = listOf(
                Interaction(
                    id = 3001,
                    type = "Meeting",
                    date = "2026-09-10",
                    time = "14:00",
                    title = "Career discussion",
                    details = "Discussed career goals and possible professional development opportunities."
                )
            )
        ),

        Person(
            id = 4,
            firstName = "Sofia",
            lastName = "Garcia",
            contact = Contact(
                emails = listOf("sofia.garcia@email.com"),
                phones = listOf("+63 920 456 7890"),
                location = "Manila, Philippines",
                preferredContact = "Email"
            ),
            personalInfo = PersonalInfo(
                birthday = "2003-07-09",
                pronouns = "she/her",
                gender = "Female",
                occupation = "University Student"
            ),
            relationship = Relationship(
                category = "Classmate",
                workplaceOrSchool = "University of Manila",
                howYouMet = "Class",
                dateYouMet = "2024-08-19"
            ),
            interests = listOf(
                "Reading",
                "Movies",
                "Writing"
            ),
            skills = listOf(
                "Writing",
                "Research",
                "Presentation"
            ),
            notes = "Sofia is a classmate who often helps with research and school projects.",
            interactions = listOf(
                Interaction(
                    id = 4001,
                    type = "Message",
                    date = "2026-09-20",
                    time = "19:15",
                    title = "School project discussion",
                    details = "Discussed the requirements and tasks for the upcoming project."
                ),
                Interaction(
                    id = 4002,
                    type = "Meetup",
                    date = "2026-09-05",
                    time = "13:00",
                    title = "Study session",
                    details = "Worked together on school requirements at the library."
                )
            )
        ),

        Person(
            id = 5,
            firstName = "Ethan",
            lastName = "Tan",
            contact = Contact(
                emails = listOf("ethan.tan@email.com"),
                phones = listOf("+63 921 567 8901"),
                location = "Cebu City, Philippines",
                preferredContact = "Email"
            ),
            personalInfo = PersonalInfo(
                birthday = "2000-01-28",
                pronouns = "he/him",
                gender = "Male",
                occupation = "Entrepreneur"
            ),
            relationship = Relationship(
                category = "Professional",
                workplaceOrSchool = "Tan Ventures",
                howYouMet = "Networking event",
                dateYouMet = "2025-04-15"
            ),
            interests = listOf(
                "Business",
                "Startups",
                "Technology"
            ),
            skills = listOf(
                "Business Strategy",
                "Networking",
                "Marketing"
            ),
            notes = "Ethan is interested in startups and frequently discusses business ideas.",
            interactions = listOf(
                Interaction(
                    id = 5001,
                    type = "Call",
                    date = "2026-09-12",
                    time = "16:30",
                    title = "Startup discussion",
                    details = "Discussed a potential collaboration and shared business ideas."
                )
            )
        ),

        Person(
            id = 6,
            firstName = "Lara",
            lastName = "Villanueva",
            contact = Contact(
                emails = listOf("lara.villanueva@email.com"),
                phones = listOf("+63 922 678 9012"),
                location = "Laguna, Philippines",
                preferredContact = "Phone"
            ),
            personalInfo = PersonalInfo(
                birthday = "2002-09-17",
                pronouns = "she/her",
                gender = "Female",
                occupation = "Teacher"
            ),
            relationship = Relationship(
                category = "Friend",
                workplaceOrSchool = "Laguna High School",
                howYouMet = "Mutual friend",
                dateYouMet = "2020-12-06"
            ),
            interests = listOf(
                "Teaching",
                "Cooking",
                "Travel"
            ),
            skills = listOf(
                "Teaching",
                "Public Speaking",
                "Cooking"
            ),
            notes = "Lara enjoys traveling and trying new restaurants with friends.",
            interactions = listOf(
                Interaction(
                    id = 6001,
                    type = "Message",
                    date = "2026-09-08",
                    time = "21:00",
                    title = "Planning a trip",
                    details = "Talked about possible destinations for an upcoming weekend trip."
                )
            )
        ),

        Person(
            id = 7,
            firstName = "Noah",
            lastName = "Lim",
            contact = Contact(
                emails = listOf("noah.lim@email.com"),
                phones = listOf("+63 923 789 0123"),
                location = "Taguig, Philippines",
                preferredContact = "Email"
            ),
            personalInfo = PersonalInfo(
                birthday = "1999-12-11",
                pronouns = "they/them",
                gender = "Non-binary",
                occupation = "UX Designer"
            ),
            relationship = Relationship(
                category = "Professional",
                workplaceOrSchool = "Design Hub",
                howYouMet = "Conference",
                dateYouMet = "2024-10-22"
            ),
            interests = listOf(
                "UX Design",
                "Technology",
                "Games"
            ),
            skills = listOf(
                "UI Design",
                "UX Research",
                "Prototyping"
            ),
            notes = "Noah is someone to contact when discussing UI/UX and design projects.",
            interactions = listOf(
                Interaction(
                    id = 7001,
                    type = "Meeting",
                    date = "2026-09-17",
                    time = "11:00",
                    title = "Design feedback",
                    details = "Reviewed a prototype and discussed possible usability improvements."
                )
            )
        ),

        Person(
            id = 8,
            firstName = "Grace",
            lastName = "Mendoza",
            contact = Contact(
                emails = listOf("grace.mendoza@email.com"),
                phones = listOf("+63 924 890 1234"),
                location = "Batangas, Philippines",
                preferredContact = "Phone"
            ),
            personalInfo = PersonalInfo(
                birthday = "2001-04-30",
                pronouns = "she/her",
                gender = "Female",
                occupation = "Content Writer"
            ),
            relationship = Relationship(
                category = "Friend",
                workplaceOrSchool = "Media Studio",
                howYouMet = "Online community",
                dateYouMet = "2022-11-13"
            ),
            interests = listOf(
                "Writing",
                "Books",
                "Film"
            ),
            skills = listOf(
                "Writing",
                "Editing",
                "Storytelling"
            ),
            notes = "Grace enjoys discussing books, films, and creative writing.",
            interactions = listOf(
                Interaction(
                    id = 8001,
                    type = "Message",
                    date = "2026-09-19",
                    time = "17:20",
                    title = "Book recommendation",
                    details = "Shared recommendations for books and documentaries."
                )
            )
        ),

        Person(
            id = 9,
            firstName = "Marco",
            lastName = "Dela Cruz",
            contact = Contact(
                emails = listOf("marco.delacruz@email.com"),
                phones = listOf("+63 925 901 2345"),
                location = "Cavite, Philippines",
                preferredContact = "Email"
            ),
            personalInfo = PersonalInfo(
                birthday = "2000-06-25",
                pronouns = "he/him",
                gender = "Male",
                occupation = "IT Specialist"
            ),
            relationship = Relationship(
                category = "Family",
                workplaceOrSchool = "IT Services Co.",
                howYouMet = "Family",
                dateYouMet = "2000-06-25"
            ),
            interests = listOf(
                "Computers",
                "Cars",
                "Gaming"
            ),
            skills = listOf(
                "IT Support",
                "Networking",
                "Hardware"
            ),
            notes = "Marco is a family member who is knowledgeable about computers and hardware.",
            interactions = listOf(
                Interaction(
                    id = 9001,
                    type = "Call",
                    date = "2026-09-21",
                    time = "20:00",
                    title = "Tech help",
                    details = "Called to discuss a computer issue and troubleshoot the problem."
                )
            )
        )
    )

    // Current people list. Starts with the mock data; changes are kept in memory only
    // (lost when the app closes). Screens collect this to update automatically.
    private val _people = MutableStateFlow(mockPeople)
    val people: StateFlow<List<Person>> = _people.asStateFlow()

    // Snapshot of the list right now (doesn't update)
    fun getPeople(): List<Person> {
        return _people.value
    }

    fun getPersonById(id: Int): Person? {
        return _people.value.find { it.id == id }
    }

    // Emits the person again every time they change; null if not found (e.g. deleted)
    fun observePerson(id: Int): Flow<Person?> {
        return people.map { list -> list.find { it.id == id } }
    }

    // Replaces the person with the same id. Does nothing if the id isn't found.
    fun updatePerson(person: Person) {
        _people.update { list ->
            list.map { if (it.id == person.id) person else it }
        }
    }

    // Adds a new person with the next free id (the id on the passed person is ignored).
    // Returns the new id.
    fun addPerson(person: Person): Int {
        var newId = 0
        _people.update { list ->
            newId = (list.maxOfOrNull { it.id } ?: 0) + 1
            list + person.copy(id = newId)
        }
        return newId
    }

    // Adds an interaction to a person, with the next free interaction id (the id on the
    // passed interaction is ignored). Keeps the list newest first. Returns the new id,
    // or null if the person isn't found.
    fun addInteraction(personId: Int, interaction: Interaction): Int? {
        if (getPersonById(personId) == null) return null
        var newId = 0
        _people.update { list ->
            newId = (list.flatMap { it.interactions }.maxOfOrNull { it.id } ?: 0) + 1
            list.map { person ->
                if (person.id != personId) {
                    person
                } else {
                    // "yyyy-MM-dd HH:mm" sorts correctly as plain text
                    val updated = (person.interactions + interaction.copy(id = newId))
                        .sortedByDescending { "${it.date} ${it.time}" }
                    person.copy(interactions = updated)
                }
            }
        }
        return newId
    }

    // Replaces the interaction with the same id on that person; keeps the list newest first.
    // Does nothing if the person or interaction isn't found.
    fun updateInteraction(personId: Int, interaction: Interaction) {
        updateInteractions(personId) { interactions ->
            interactions.map { if (it.id == interaction.id) interaction else it }
        }
    }

    // Removes one interaction from that person. Returns an action that puts it back
    // (for the snackbar's Undo), or null if the person or interaction isn't found.
    fun deleteInteraction(personId: Int, interactionId: Int): (() -> Unit)? {
        val removed = getPersonById(personId)?.interactions?.find { it.id == interactionId }
            ?: return null
        updateInteractions(personId) { interactions ->
            interactions.filterNot { it.id == interactionId }
        }
        // Re-adding keeps the list sorted; does nothing if the person was deleted since
        return { addInteraction(personId, removed) }
    }

    private fun updateInteractions(personId: Int, change: (List<Interaction>) -> List<Interaction>) {
        _people.update { list ->
            list.map { person ->
                if (person.id != personId) {
                    person
                } else {
                    person.copy(
                        interactions = change(person.interactions)
                            .sortedByDescending { "${it.date} ${it.time}" }
                    )
                }
            }
        }
    }

    // Removes the person (and their interactions, which live inside Person). Returns an
    // action that puts them back where they were in the list (for the snackbar's Undo),
    // or null if the id isn't found.
    fun deletePerson(id: Int): (() -> Unit)? {
        val list = _people.value
        val index = list.indexOfFirst { it.id == id }
        if (index == -1) return null
        val removed = list[index]
        _people.update { current -> current.filterNot { it.id == id } }
        return { restorePerson(removed, index) }
    }

    private fun restorePerson(person: Person, index: Int) {
        _people.update { list ->
            when {
                // Already restored (e.g. Undo tapped twice)
                person in list -> list
                // A person added in the meantime got this id; give the restored one a new id
                list.any { it.id == person.id } -> {
                    val newId = list.maxOf { it.id } + 1
                    list.withInserted(index, person.copy(id = newId))
                }
                else -> list.withInserted(index, person)
            }
        }
    }

    // Inserts at index, or at the end if the list has shrunk since
    private fun List<Person>.withInserted(index: Int, person: Person): List<Person> =
        toMutableList().apply { add(index.coerceAtMost(size), person) }

    // Puts the mock data back. For tests only (internal = not visible outside this module).
    internal fun resetToSampleData() {
        _people.value = mockPeople
    }
}