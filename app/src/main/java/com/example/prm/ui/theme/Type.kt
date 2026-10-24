package com.example.prm.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// Material's default body style (16sp, 24sp line height, 0.5sp letter spacing). Plain
// Text uses it, and the app styles below are variations of it. Taken from Material's own
// Typography so it also keeps Material's line-height trimming; a hand-built TextStyle
// would trim single lines shorter and shift text slightly.
private val BaseText = Typography().bodyLarge

// Used by Material components (buttons, text fields, app bars, dialogs)
val Typography = Typography(
    bodyLarge = BaseText
)

// The app's own text styles, in one place. Kept separate from Typography so changing
// them never restyles Material components that use those roles internally.
object AppTextStyles {
    // Person's name at the top of the detail page
    val personName = BaseText.copy(fontSize = 22.sp, fontWeight = FontWeight.Bold)

    // Title of a confirmation sheet, e.g. "Delete Alex Santos?"
    val sheetTitle = BaseText.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold)

    // The ">" on a person card
    val cardArrow = BaseText.copy(fontSize = 20.sp, fontWeight = FontWeight.Bold)

    // Name on a person card
    val cardTitle = BaseText.copy(fontSize = 16.sp, fontWeight = FontWeight.Bold)

    // Interaction title on the detail page
    val itemTitle = BaseText.copy(fontSize = 15.sp, fontWeight = FontWeight.Bold)

    // Regular text: info rows, notes, messages
    val body = BaseText.copy(fontSize = 14.sp)

    // Bold label above a form field
    val fieldLabel = BaseText.copy(fontSize = 14.sp, fontWeight = FontWeight.Bold)

    // Uppercase section titles, e.g. "RELATIONSHIP"
    val sectionHeader = BaseText.copy(
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
    )

    // Small text: dates, tag chips
    val caption = BaseText.copy(fontSize = 12.sp)
}
