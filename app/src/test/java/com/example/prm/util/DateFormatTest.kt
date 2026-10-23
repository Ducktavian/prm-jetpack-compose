package com.example.prm.util

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import java.util.Locale
import java.util.TimeZone

class DateFormatTest {
    private lateinit var originalLocale: Locale

    // Output uses the device locale, so pin it for predictable results
    @Before
    fun setUp() {
        originalLocale = Locale.getDefault()
        Locale.setDefault(Locale.US)
    }

    @After
    fun tearDown() {
        Locale.setDefault(originalLocale)
    }

    @Test
    fun formatDisplayDate_isoDate_returnsReadableDate() {
        assertEquals("May 14, 2002", formatDisplayDate("2002-05-14"))
        assertEquals("Sep 3, 2024", formatDisplayDate("2024-09-03"))
    }

    @Test
    fun formatDisplayDate_invalidInput_returnsInputUnchanged() {
        assertEquals("not a date", formatDisplayDate("not a date"))
        assertEquals("2024-13-40", formatDisplayDate("2024-13-40"))
        assertEquals("", formatDisplayDate(""))
    }

    @Test
    fun formatDisplayTime_24hTime_returns12hTime() {
        assertEquals("8:30 PM", formatDisplayTime("20:30"))
        assertEquals("12:00 AM", formatDisplayTime("00:00"))
        assertEquals("9:05 AM", formatDisplayTime("09:05"))
    }

    @Test
    fun isoDateToUtcMillis_roundTripsBackToSameDate() {
        val millis = isoDateToUtcMillis("2024-09-03")
        assertEquals(1725321600000L, millis) // 2024-09-03T00:00:00Z
        assertEquals("2024-09-03", utcMillisToIsoDate(millis!!))
    }

    @Test
    fun isoDateToUtcMillis_invalidOrBlank_returnsNull() {
        assertNull(isoDateToUtcMillis(""))
        assertNull(isoDateToUtcMillis("2024-13-40"))
    }

    @Test
    fun utcMillisToIsoDate_ignoresDeviceTimezone() {
        val original = TimeZone.getDefault()
        try {
            // Far behind UTC: a local-time formatter would give the previous day
            TimeZone.setDefault(TimeZone.getTimeZone("America/Los_Angeles"))
            assertEquals("2024-09-03", utcMillisToIsoDate(1725321600000L))
        } finally {
            TimeZone.setDefault(original)
        }
    }

    @Test
    fun parseTime24h_validAndInvalid() {
        assertEquals(20 to 30, parseTime24h("20:30"))
        assertEquals(0 to 5, parseTime24h("00:05"))
        assertNull(parseTime24h(""))
        assertNull(parseTime24h("25:00"))
        assertNull(parseTime24h("evening"))
    }

    @Test
    fun formatTime24h_padsWithZeros() {
        assertEquals("08:05", formatTime24h(8, 5))
        assertEquals("20:30", formatTime24h(20, 30))
    }

    @Test
    fun formatDisplayTime_invalidInput_returnsInputUnchanged() {
        assertEquals("25:99", formatDisplayTime("25:99"))
        assertEquals("evening", formatDisplayTime("evening"))
    }
}
