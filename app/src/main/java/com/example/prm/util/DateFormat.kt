package com.example.prm.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

// Display-only formatting. Data keeps its stored format ("2002-05-14", "20:30");
// these just turn it into something readable on screen.
// SimpleDateFormat is used (not java.time) because it works on minSdk 24 without desugaring.

// "2002-05-14" -> "May 14, 2002". Returns the input unchanged if it can't be parsed.
fun formatDisplayDate(isoDate: String): String =
    reformat(isoDate, from = "yyyy-MM-dd", to = "MMM d, yyyy")

// "20:30" -> "8:30 PM". Returns the input unchanged if it can't be parsed.
fun formatDisplayTime(time24h: String): String =
    reformat(time24h, from = "HH:mm", to = "h:mm a")

private fun reformat(value: String, from: String, to: String): String {
    return try {
        val parser = SimpleDateFormat(from, Locale.US).apply { isLenient = false }
        val date = parser.parse(value) ?: return value
        SimpleDateFormat(to, Locale.getDefault()).format(date)
    } catch (e: ParseException) {
        value
    }
}
