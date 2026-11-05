package com.drapefullness.reckoner.domain.model

import com.drapefullness.reckoner.core.config.GameConfig

data class SewingSettings(
    val units: UnitSystem = UnitSystem.CM,
    val sideHemMm: Int = GameConfig.DEFAULT_SIDE_HEM_MM,
    val bottomHemMm: Int = GameConfig.DEFAULT_BOTTOM_HEM_MM,
    val headerAllowanceMm: Int = GameConfig.DEFAULT_HEADER_ALLOWANCE_MM,
    val seamAllowanceMm: Int = GameConfig.DEFAULT_SEAM_ALLOWANCE_MM,
    val liningHemMm: Int = GameConfig.DEFAULT_LINING_HEM_MM,
    val liningHeaderMm: Int = GameConfig.DEFAULT_LINING_HEADER_MM,
    val haptics: Boolean = true,
    val eveningTheme: Boolean = false
)
