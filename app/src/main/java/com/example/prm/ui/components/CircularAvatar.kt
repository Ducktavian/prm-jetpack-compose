package com.example.prm.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Purple circle with initials. Always shown next to the person's name, so screen
// readers skip it (otherwise TalkBack reads "AS" before "Alex Santos").
@Composable
fun CircularAvatar(
    initials: String,
    size: Dp = 48.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .background(
                color = MaterialTheme.colorScheme.primary, shape = CircleShape
            )
            .clearAndSetSemantics {},
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = MaterialTheme.colorScheme.onPrimary,
            // Scales with the avatar: 48.dp -> 18.sp, 64.dp -> 24.sp
            fontSize = (size.value * 0.375f).sp,
            fontWeight = FontWeight.Bold
        )
    }
}
