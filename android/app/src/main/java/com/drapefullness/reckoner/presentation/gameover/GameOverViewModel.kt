package com.drapefullness.reckoner.presentation.gameover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drapefullness.reckoner.domain.repository.SettingsRepository
import com.drapefullness.reckoner.domain.repository.WindowRepository
import com.drapefullness.reckoner.domain.usecase.CalculateCurtainUseCase
import com.drapefullness.reckoner.domain.usecase.CalculatePleatPlanUseCase
import com.drapefullness.reckoner.domain.usecase.FormatMeasurementUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GameOverViewModel(
    private val windows: WindowRepository,
    private val settingsStore: SettingsRepository,
    private val calculate: CalculateCurtainUseCase,
    private val pleatPlan: CalculatePleatPlanUseCase,
    private val format: FormatMeasurementUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GameOverUiState())
    val state: StateFlow<GameOverUiState> = _state.asStateFlow()

    private val _events = Channel<GameOverEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var currentId: String? = null

    fun load(windowId: String) {
        currentId = windowId
        viewModelScope.launch {
            val next = withContext(Dispatchers.IO) { build(windowId) }
            _state.update { next.copy(tab = it.tab) }
        }
    }

    fun selectTab(tab: ResultTab) {
        _state.update { it.copy(tab = tab) }
    }

    fun addToShoppingList() {
        val id = currentId ?: return
        viewModelScope.launch {
            withContext(Dispatchers.IO) { windows.addToList(id) }
            _events.send(GameOverEvent.AddedToList)
        }
    }

    private fun build(windowId: String): GameOverUiState {
        val window = windows.byId(windowId)
            ?: return GameOverUiState(loading = false, missing = true)
        val settings = settingsStore.load()
        val result = calculate(window, settings)
        val plan = pleatPlan(window, result)
        val units = settings.units
        val lining = result.lining
        return GameOverUiState(
            loading = false,
            missing = false,
            windowLabel = window.room + " - " + window.label,
            heroTotal = format.bulk(result.totalFabricMm, units),
            fabricWidthCaption = format.length(window.fabricWidthMm, units),
            lined = lining != null,
            finishedWidth = format.length(result.finishedWidthMm, units),
            widths = format.count(result.widths) + " (" +
                format.count(result.widthsPerCurtain) + ")",
            finishedDrop = format.length(result.finishedDropMm, units),
            cutLength = format.length(result.cutLengthMm, units),
            patternAdded = format.length(result.patternAddedMm, units),
            totalFabric = format.bulk(result.totalFabricMm, units),
            sideHems = "2 x " + format.length(result.sideHemMm, units),
            headerAllowance = format.length(result.headerAllowanceMm, units),
            bottomHem = format.length(result.bottomHemMm, units),
            liningWidths = lining?.let { format.count(it.widths) }.orEmpty(),
            liningCutLength = lining?.let { format.length(it.cutLengthMm, units) }.orEmpty(),
            liningTotal = lining?.let { format.bulk(it.totalMm, units) }.orEmpty(),
            liningHeader = lining?.let { format.length(it.headerMm, units) }.orEmpty(),
            liningHem = lining?.let { format.length(it.hemMm, units) }.orEmpty(),
            liningSetBack = lining?.let { format.length(it.setBackMm, units) }.orEmpty(),
            heading = window.heading,
            unitsPerCurtain = format.count(plan.unitsPerCurtain),
            fabricPerUnit = format.length(plan.fabricPerUnitMm, units),
            gapBetween = format.length(plan.gapMm, units),
            hooksPerCurtain = format.count(plan.hooksPerCurtain),
            hooksTotal = format.count(plan.hooksTotal),
            firstFromEdge = format.length(plan.firstFromEdgeMm, units),
            pleatCount = plan.unitsPerCurtain,
            hasHooks = plan.hooksTotal > 0
        )
    }
}
