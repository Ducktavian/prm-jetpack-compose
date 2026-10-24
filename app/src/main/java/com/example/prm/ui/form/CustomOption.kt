package com.example.prm.ui.form

// Picking this in a dropdown shows a text box for typing your own value.
// Used by both the Relationship and the interaction Type dropdowns.
const val OTHER_OPTION = "Other / Custom"

// Stored value -> (dropdown choice, custom text), for when a form opens:
// "Friend" -> ("Friend", ""), "Neighbor" -> (OTHER_OPTION, "Neighbor"), "" -> ("", "")
fun splitCustomOption(value: String, standardOptions: List<String>): Pair<String, String> =
    when {
        value.isBlank() -> "" to ""
        value in standardOptions -> value to ""
        else -> OTHER_OPTION to value
    }

// The value actually saved: the custom text when OTHER_OPTION is picked
fun resolveCustomOption(choice: String, customText: String): String =
    if (choice == OTHER_OPTION) customText.trim() else choice.trim()

// Error for the custom text box: required only while OTHER_OPTION is picked
fun customOptionError(choice: String, customText: String, message: String): String? =
    if (choice == OTHER_OPTION) requiredError(customText, message) else null
