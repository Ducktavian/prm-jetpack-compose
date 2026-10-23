package com.example.prm.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// Display-only formatting. Data keeps its stored format ("2002-05-14", "20:30");
// these just turn it into something readable on screen.
// SimpleDateFormat is used (not java.time) because it works on minSdk 24 without desugaring.

// "2002-05-14" -> "May 14, 2002". Returns the input unchanged if it can't be parsed.
fun formatDisplayDate(isoDate: String): String =
    reformat(isoDate, from = "yyyy-MM-dd", to = "MMM d, yyyy")

// "20:30" -> "8:30 PM". Returns the input unchanged if it can't be parsed.
fun formatDisplayTime(time24h: String): String =
    reformat(time24h, from = "HH:mm", to = "h:mm a")

// Material's DatePicker works in UTC milliseconds; data stores "yyyy-MM-dd".
// UTC is used on both sides so the picked day never shifts by a timezone.

// "2024-09-03" -> millis at UTC midnight. null if blank or not a valid date.
fun isoDateToUtcMillis(isoDate: String): Long? {
    return try {
        utcIsoFormat().parse(isoDate)?.time
    } catch (e: ParseException) {
        null
    }
}

// Millis from the DatePicker -> "2024-09-03"
fun utcMillisToIsoDate(millis: Long): String {
    return utcIsoFormat().format(Date(millis))
}

private fun utcIsoFormat() = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
    isLenient = false
    timeZone = TimeZone.getTimeZone("UTC")
}

private fun reformat(value: String, from: String, to: String): String {
    return try {
        val parser = SimpleDateFormat(from, Locale.US).apply { isLenient = false }
        val date = parser.parse(value) ?: return value
        SimpleDateFormat(to, Locale.getDefault()).format(date)
    } catch (e: ParseException) {
        value
    }
}
