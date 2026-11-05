package com.drapefullness.reckoner.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drapefullness.reckoner.core.di.ServiceLocator
import com.drapefullness.reckoner.domain.model.SewingSettings
import com.drapefullness.reckoner.domain.model.UnitSystem
import com.drapefullness.reckoner.domain.repository.SettingsRepository
import com.drapefullness.reckoner.domain.usecase.FormatMeasurementUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsViewModel(
    private val settingsStore: SettingsRepository,
    private val format: FormatMeasurementUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsUiState())
    val state: StateFlow<SettingsUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val loaded = withContext(Dispatchers.IO) { settingsStore.load() }
            _state.update { present(loaded) }
        }
    }

    fun setUnits(units: UnitSystem) = mutate { it.copy(units = units) }

    fun setSideHem(value: Int) = mutate { it.copy(sideHemMm = value) }

    fun setBottomHem(value: Int) = mutate { it.copy(bottomHemMm = value) }

    fun setHeaderAllowance(value: Int) = mutate { it.copy(headerAllowanceMm = value) }

    fun setSeamAllowance(value: Int) = mutate { it.copy(seamAllowanceMm = value) }

    fun setLiningHem(value: Int) = mutate { it.copy(liningHemMm = value) }

    fun setLiningHeader(value: Int) = mutate { it.copy(liningHeaderMm = value) }

    fun setHaptics(enabled: Boolean) = mutate { it.copy(haptics = enabled) }

    fun setEveningTheme(enabled: Boolean) = mutate { it.copy(eveningTheme = enabled) }

    fun askReset() {
        _state.update { it.copy(confirmReset = true) }
    }

    fun dismissReset() {
        _state.update { it.copy(confirmReset = false) }
    }

    fun resetDefaults() {
        _state.update { it.copy(confirmReset = false) }
        mutate { SewingSettings(units = it.units) }
    }

    private fun mutate(transform: (SewingSettings) -> SewingSettings) {
        viewModelScope.launch {
            val next = transform(_state.value.settings)
            withContext(Dispatchers.IO) { settingsStore.store(next) }
            ServiceLocator.publish(next)
            _state.update { current -> present(next).copy(confirmReset = current.confirmReset) }
        }
    }

    private fun present(settings: SewingSettings): SettingsUiState = SettingsUiState(
        loading = false,
        settings = settings,
        sideHem = format.length(settings.sideHemMm, settings.units),
        bottomHem = format.length(settings.bottomHemMm, settings.units),
        headerAllowance = format.length(settings.headerAllowanceMm, settings.units),
        seamAllowance = format.length(settings.seamAllowanceMm, settings.units),
        liningHem = format.length(settings.liningHemMm, settings.units),
        liningHeader = format.length(settings.liningHeaderMm, settings.units)
    )
}
