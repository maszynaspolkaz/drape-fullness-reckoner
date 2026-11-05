package com.drapefullness.reckoner.presentation.stackpreview

enum class StackState {
    CLOSED,
    HALF_OPEN,
    OPEN
}

data class StackPreviewUiState(
    val loading: Boolean = true,
    val windowLabel: String = "",
    val trackWidthMm: Int = 1800,
    val dropMm: Int = 2400,
    val clearanceMm: Int = 10,
    val foldCount: Int = 12,
    val openFraction: Float = 0f,
    val stackBackLabel: String = "",
    val coveredLabel: String = "",
    val trackLabel: String = ""
) {
    val stackState: StackState
        get() = when {
            openFraction >= 0.66f -> StackState.OPEN
            openFraction >= 0.33f -> StackState.HALF_OPEN
            else -> StackState.CLOSED
        }
}
