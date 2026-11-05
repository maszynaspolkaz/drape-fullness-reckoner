package com.drapefullness.reckoner.presentation.settings

import com.drapefullness.reckoner.domain.model.SewingSettings

data class SettingsUiState(
    val loading: Boolean = true,
    val settings: SewingSettings = SewingSettings(),
    val sideHem: String = "",
    val bottomHem: String = "",
    val headerAllowance: String = "",
    val seamAllowance: String = "",
    val liningHem: String = "",
    val liningHeader: String = "",
    val confirmReset: Boolean = false
)
