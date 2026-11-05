package com.drapefullness.reckoner.presentation.menu

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

class MenuViewModel(
    private val windows: WindowRepository,
    private val settingsStore: SettingsRepository,
    private val calculate: CalculateCurtainUseCase,
    private val format: FormatMeasurementUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MenuUiState())
    val state: StateFlow<MenuUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val snapshot = withContext(Dispatchers.IO) {
                val settings = settingsStore.load()
                val saved = windows.all()
                var fabric = 0
                val groups = saved.groupBy { it.room }.map { entry ->
                    RoomGroup(
                        room = entry.key,
                        rows = entry.value.map { window ->
                            val result = calculate(window, settings)
                            fabric += result.buyFabricMm
                            WindowRow(
                                id = window.id,
                                label = window.label,
                                size = format.size(
                                    window.trackWidthMm,
                                    window.dropMm,
                                    settings.units
                                ),
                                fabric = format.bulk(result.buyFabricMm, settings.units)
                            )
                        }
                    )
                }
                MenuUiState(
                    loading = false,
                    groups = groups,
                    windowCount = format.count(saved.size),
                    fabricTotal = format.bulk(fabric, settings.units),
                    showStats = saved.isNotEmpty() && fabric > 0
                )
            }
            _state.update { snapshot }
        }
    }
}
