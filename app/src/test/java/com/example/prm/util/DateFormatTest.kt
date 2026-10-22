package com.example.prm.util

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.Locale

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
    fun formatDisplayTime_invalidInput_returnsInputUnchanged() {
        assertEquals("25:99", formatDisplayTime("25:99"))
        assertEquals("evening", formatDisplayTime("evening"))
    }
}
