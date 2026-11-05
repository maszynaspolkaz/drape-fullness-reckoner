package com.drapefullness.reckoner.data.repository

import com.drapefullness.reckoner.core.config.GameConfig
import com.drapefullness.reckoner.data.local.CurtainPreferences
import com.drapefullness.reckoner.domain.model.SewingSettings
import com.drapefullness.reckoner.domain.model.UnitSystem
import com.drapefullness.reckoner.domain.repository.SettingsRepository

class SettingsRepositoryImpl(
    private val preferences: CurtainPreferences
) : SettingsRepository {

    override fun load(): SewingSettings = SewingSettings(
        units = runCatching {
            UnitSystem.valueOf(preferences.text(KEY_UNITS, UnitSystem.CM.name))
        }.getOrDefault(UnitSystem.CM),
        sideHemMm = preferences.number(KEY_SIDE_HEM, GameConfig.DEFAULT_SIDE_HEM_MM),
        bottomHemMm = preferences.number(KEY_BOTTOM_HEM, GameConfig.DEFAULT_BOTTOM_HEM_MM),
        headerAllowanceMm = preferences.number(KEY_HEADER, GameConfig.DEFAULT_HEADER_ALLOWANCE_MM),
        seamAllowanceMm = preferences.number(KEY_SEAM, GameConfig.DEFAULT_SEAM_ALLOWANCE_MM),
        liningHemMm = preferences.number(KEY_LINING_HEM, GameConfig.DEFAULT_LINING_HEM_MM),
        liningHeaderMm = preferences.number(KEY_LINING_HEADER, GameConfig.DEFAULT_LINING_HEADER_MM),
        haptics = preferences.flag(KEY_HAPTICS, true),
        eveningTheme = preferences.flag(KEY_EVENING, false)
    )

    override fun store(settings: SewingSettings) {
        preferences.putText(KEY_UNITS, settings.units.name)
        preferences.putNumber(KEY_SIDE_HEM, settings.sideHemMm)
        preferences.putNumber(KEY_BOTTOM_HEM, settings.bottomHemMm)
        preferences.putNumber(KEY_HEADER, settings.headerAllowanceMm)
        preferences.putNumber(KEY_SEAM, settings.seamAllowanceMm)
        preferences.putNumber(KEY_LINING_HEM, settings.liningHemMm)
        preferences.putNumber(KEY_LINING_HEADER, settings.liningHeaderMm)
        preferences.putFlag(KEY_HAPTICS, settings.haptics)
        preferences.putFlag(KEY_EVENING, settings.eveningTheme)
    }

    private companion object {
        const val KEY_UNITS = "units"
        const val KEY_SIDE_HEM = "side_hem"
        const val KEY_BOTTOM_HEM = "bottom_hem"
        const val KEY_HEADER = "header_allowance"
        const val KEY_SEAM = "seam_allowance"
        const val KEY_LINING_HEM = "lining_hem"
        const val KEY_LINING_HEADER = "lining_header"
        const val KEY_HAPTICS = "haptics"
        const val KEY_EVENING = "evening_theme"
    }
}
