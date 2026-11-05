package com.drapefullness.reckoner.domain.model

import com.drapefullness.reckoner.core.config.GameConfig

data class CurtainWindow(
    val id: String,
    val room: String,
    val label: String,
    val trackWidthMm: Int = GameConfig.DEFAULT_TRACK_WIDTH_MM,
    val dropMm: Int = GameConfig.DEFAULT_DROP_MM,
    val clearanceMm: Int = GameConfig.DEFAULT_CLEARANCE_MM,
    val heading: HeadingStyle = HeadingStyle.PINCH_PLEAT,
    val fullness: Float = HeadingStyle.PINCH_PLEAT.defaultFullness,
    val fabricWidthMm: Int = GameConfig.DEFAULT_FABRIC_WIDTH_MM,
    val patternRepeatMm: Int = GameConfig.DEFAULT_PATTERN_REPEAT_MM,
    val patternMatch: Boolean = true,
    val lined: Boolean = true
)
