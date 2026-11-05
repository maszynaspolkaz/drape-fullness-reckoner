package com.drapefullness.reckoner.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.drapefullness.reckoner.core.theme.LocalHapticsEnabled

class HapticTrigger(
    private val feedback: HapticFeedback,
    private val enabled: Boolean
) {
    fun tap() {
        if (enabled) feedback.performHapticFeedback(HapticFeedbackType.LongPress)
    }

    fun tick() {
        if (enabled) feedback.performHapticFeedback(HapticFeedbackType.TextHandleMove)
    }
}

@Composable
fun rememberHapticTrigger(): HapticTrigger {
    val feedback = LocalHapticFeedback.current
    val enabled = LocalHapticsEnabled.current
    return remember(feedback, enabled) { HapticTrigger(feedback, enabled) }
}
