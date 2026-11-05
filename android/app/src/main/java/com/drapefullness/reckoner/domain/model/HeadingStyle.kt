package com.drapefullness.reckoner.domain.model

import kotlin.math.roundToInt

enum class HeadingStyle(
    val minFullness: Float,
    val maxFullness: Float,
    val defaultFullness: Float,
    val stackFactor: Float,
    val spriteIndex: Int
) {
    PENCIL_PLEAT(2.0f, 2.5f, 2.3f, 0.22f, 1),
    EYELET(2.0f, 2.2f, 2.0f, 0.26f, 2),
    PINCH_PLEAT(2.0f, 2.5f, 2.2f, 0.28f, 3),
    WAVE(2.0f, 2.3f, 2.1f, 0.24f, 4),
    TAB_TOP(1.5f, 2.0f, 1.8f, 0.30f, 5);

    fun clampFullness(value: Float): Float = value.coerceIn(minFullness, maxFullness)

    val sliderSteps: Int
        get() = ((((maxFullness - minFullness) * 10f).roundToInt()) - 1).coerceAtLeast(0)
}
