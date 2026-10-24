# PRM — Personal Relationship Manager

PRM is an Android app for keeping track of the people in your life: who they are, how you know them, how to reach them, and every time you've talked. Think of it as a small, personal CRM for friends, classmates, mentors and colleagues.

It is built with **Kotlin** and **Jetpack Compose** (Material 3), following an MVVM structure.

> **Note:** This version keeps data **in memory only**. It starts with sample people, and any changes (adding, editing, deleting) are lost when the app is closed. Saving to a database is planned for a later version.

## Group members

| Name                  | Role |
|-----------------------|---|
| _Lebron Dustin M. Uy_ | _Solo developer: design, implementation, testing_ |

---

## Features

**People list**
- See everyone at a glance: avatar with initials, name, and relationship.
- **Search** by name, relationship (e.g. "Mentor"), or interest (e.g. "Photography").
- **+** button to add a new person.

**Person details**
- Grouped sections: Relationship, Personal Info, Contact, Skills, Interests, Notes, and Interactions.
- Dates are shown in a readable format (e.g. *Aug 20, 2021 · 8:30 PM*).

**Add / edit / delete people**
- One form for both adding and editing, with required fields marked by a red `*`.
- Relationship options: Friend, Family, Classmate, Coworker, Mentor, Professional, or **Other / Custom** (type your own, e.g. "Professor").
- Multiple phone numbers and emails per person.
- Date pickers for birthday and date met.

**Interactions** (meetings, calls, messages…)
- Log interactions with a date, time, type, optional title, and notes.
- Types: Meeting, Meetup, Call, Message, or **Other / Custom**.
- Each interaction has a **•••** menu to edit or delete it.
- Interactions are listed newest first.

**Safety and feedback**
- Every save and delete asks for **confirmation** first.
- Leaving a form with unsaved changes asks **"Discard changes?"**.
- A short message appears after each action (e.g. *"Person added successfully."*).

---

## How to use the app

### Find someone
1. Open the app. The **People** screen lists everyone.
2. Type in the **Search** box to filter by name, relationship, or interest.
3. Tap a person's card to open their details.

### Add a person
1. On the People screen, tap the purple **+** button (top right).
2. Fill in at least **First name** and **Relationship** (both required).
   - Pick **Other / Custom** under Relationship to type your own category.
   - Tap **+ Add phone** / **+ Add email** for extra numbers or addresses.
   - Separate skills and interests with commas, e.g. `UI/UX, Figma`.
3. Tap **Save**, then **Add Person** to confirm.

### Edit or delete a person
- Open the person, then tap **Edit** (top right). Change any fields, tap **Save**, then **Save Changes**.
- To remove them, tap **Delete** (top right) and confirm. This also removes all their interactions.

### Log an interaction
1. Open a person and scroll to **Interactions**.
2. Tap **+ Add**.
3. Pick a **Date** and **Time** (both required) and a **Type**. Title and notes are optional; if you leave the title empty, the type is used (e.g. "Meeting").
4. Tap **Save Interaction**, then **Add Interaction** to confirm.

### Edit or delete an interaction
- Tap the **•••** next to an interaction, then choose **Edit** or **Delete**.

### Leaving without saving
- Tap **Cancel** / **Back** (or use the phone's back gesture). If you've changed anything, the app asks before discarding it.

---

## Running the project

### Requirements
- **Android Studio** _[the version you built with, e.g. from Help → About]_, or any version that supports **Android Gradle Plugin 9.4.1**
- **JDK 17 or newer** (Android Studio's bundled JDK works)
- An Android device or emulator running **Android 7.0 (API 24) or newer**

The project pins its own build tools, so no extra installs are needed:

| Tool | Version |
|---|---|
| Gradle (wrapper) | 9.6.0 |
| Android Gradle Plugin | 9.4.1 |
| Kotlin | 2.0.21 |
| Compose BOM | 2026.02.01 |

### Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/Ducktavian/prm-jetpack-compose.git
   ```
2. Open the project folder in Android Studio and let Gradle sync.
3. Select a device or emulator and press **Run ▶**.

Or from the command line, with a device connected:
```bash
./gradlew installDebug
```

### Running the tests
Unit tests cover the data layer, form logic, ViewModels, and date/time formatting:
```bash
./gradlew testDebugUnitTest
```

---

## How the code is organized

The app follows **MVVM** with **one-way data flow**: screens display state from a ViewModel and send user actions back to it; ViewModels read and write through a single shared repository.

```
Screen (Compose UI)  ──user actions──▶  ViewModel  ──▶  PersonRepository
        ▲                                   │                 │
        └──────────── state ◀───────────────┘◀── StateFlow ───┘
```

Because the repository exposes its data as a `StateFlow`, every screen updates automatically after a change — e.g. editing a name updates the detail page, the list, and search results.

```
app/src/main/java/com/example/prm/
├── MainActivity.kt              App entry point
├── navigation/AppNavHost.kt     All routes + app-wide snackbar messages
├── data/
│   ├── model/                   Person, Contact, PersonalInfo, Relationship, Interaction
│   └── repository/              PersonRepository (shared, in-memory, sample data)
├── ui/
│   ├── personlist/              People screen + search
│   ├── persondetail/            Person details + delete confirmations
│   ├── personedit/              Add/Edit person form (PersonForm = form state & validation)
│   ├── interactionform/         Add/Edit interaction form
│   ├── components/              Reusable UI: cards, form fields, pickers, sheets, snackbar
│   └── theme/                   Colors, typography, Material theme
└── util/DateFormat.kt           Date/time display and conversion helpers
```

### Screens and routes

| Screen | Route | Opened from |
|---|---|---|
| People list | `person_list` | App start |
| Person details | `person_detail/{personId}` | Tapping a person card |
| Edit person | `person_edit/{personId}` | **Edit** on details |
| Add person | `person_add` | **+** on People |
| Add interaction | `interaction_add/{personId}` | **+ Add** on Interactions |
| Edit interaction | `interaction_edit/{personId}/{interactionId}` | **•••** → **Edit** |

### Design notes
- **Dates are stored as text** (`yyyy-MM-dd`, times as `HH:mm`) and only formatted for display. Date pickers convert using UTC so the chosen day never shifts by time zone.
- **Forms hold plain text** while you type (e.g. `"UI/UX, Figma"`) and are converted to the data model only on save, where text is trimmed and empty entries removed.
- **Screens are split** into a stateful part (connected to the ViewModel) and a stateless `…Content` part, so each screen has an Android Studio **preview**.
- The theme uses a fixed purple brand color (dynamic wallpaper colors are turned off). Light and dark mode are both supported.

---

## Tech stack

| | |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose, Material 3 |
| Architecture | MVVM, StateFlow, Compose state |
| Navigation | Navigation Compose |
| Testing | JUnit 4 |
| Min / target SDK | 24 / 37 |

---

## Limitations and next steps
- Data is in memory only — add persistent storage (e.g. Room).
- No photos for people yet; avatars show initials.
- No reminders or follow-up notifications for interactions.
