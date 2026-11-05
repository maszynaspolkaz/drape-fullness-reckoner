package com.drapefullness.reckoner.domain.repository

import com.drapefullness.reckoner.domain.model.SewingSettings

interface SettingsRepository {
    fun load(): SewingSettings
    fun store(settings: SewingSettings)
}
