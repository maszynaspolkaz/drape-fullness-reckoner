package com.drapefullness.reckoner.presentation.stackpreview

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drapefullness.reckoner.domain.repository.SettingsRepository
import com.drapefullness.reckoner.domain.repository.WindowRepository
import com.drapefullness.reckoner.domain.usecase.CalculateCurtainUseCase
import com.drapefullness.reckoner.domain.usecase.FormatMeasurementUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.roundToInt

class StackPreviewViewModel(
    private val windows: WindowRepository,
    private val settingsStore: SettingsRepository,
    private val calculate: CalculateCurtainUseCase,
    private val format: FormatMeasurementUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(StackPreviewUiState())
    val state: StateFlow<StackPreviewUiState> = _state.asStateFlow()

    fun load(windowId: String) {
        viewModelScope.launch {
            val next = withContext(Dispatchers.IO) { build(windowId) }
            if (next != null) {
                _state.update { current -> next.copy(openFraction = current.openFraction) }
            }
        }
    }

    fun drag(delta: Float) {
        _state.update { it.copy(openFraction = (it.openFraction + delta).coerceIn(0f, 1f)) }
    }

    fun release() {
        _state.update { it.copy(openFraction = if (it.openFraction >= 0.5f) 1f else 0f) }
    }

    private fun build(windowId: String): StackPreviewUiState? {
        val window = windows.byId(windowId) ?: return null
        val settings = settingsStore.load()
        val result = calculate(window, settings)
        return StackPreviewUiState(
            loading = false,
            windowLabel = window.room + " - " + window.label,
            trackWidthMm = window.trackWidthMm,
            dropMm = window.dropMm,
            clearanceMm = window.clearanceMm,
            foldCount = (window.fullness * 9f).roundToInt(),
            openFraction = 0f,
            stackBackLabel = format.length(result.stackBackPerSideMm, settings.units),
            coveredLabel = format.percent(result.glassCoveredPercent),
            trackLabel = format.length(window.trackWidthMm, settings.units)
        )
    }
}
