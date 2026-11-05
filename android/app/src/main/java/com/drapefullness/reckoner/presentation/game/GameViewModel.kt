package com.drapefullness.reckoner.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drapefullness.reckoner.core.config.GameConfig
import com.drapefullness.reckoner.domain.model.CurtainWindow
import com.drapefullness.reckoner.domain.model.HeadingStyle
import com.drapefullness.reckoner.domain.model.MeasureStep
import com.drapefullness.reckoner.domain.repository.SettingsRepository
import com.drapefullness.reckoner.domain.repository.WindowRepository
import com.drapefullness.reckoner.domain.usecase.CalculateCurtainUseCase
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
import kotlin.math.roundToInt

class GameViewModel(
    private val windows: WindowRepository,
    private val settingsStore: SettingsRepository,
    private val calculate: CalculateCurtainUseCase,
    private val format: FormatMeasurementUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state.asStateFlow()

    private val _events = Channel<GameEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    private var loadedId: String? = null

    fun start(windowId: String?) {
        if (loadedId == windowId && _state.value.trackLabel.isNotEmpty()) return
        loadedId = windowId
        viewModelScope.launch {
            val loaded = withContext(Dispatchers.IO) {
                val settings = settingsStore.load()
                val rooms = windows.rooms()
                val window = windowId?.let { windows.byId(it) } ?: CurtainWindow(
                    id = "",
                    room = rooms.firstOrNull() ?: DEFAULT_ROOM,
                    label = DEFAULT_LABEL
                )
                Triple(settings, rooms, window)
            }
            _state.update {
                decorate(
                    it.copy(
                        step = MeasureStep.MEASURE,
                        settings = loaded.first,
                        rooms = loaded.second,
                        window = loaded.third,
                        dialogVisible = false
                    )
                )
            }
        }
    }

    fun setTrackWidth(value: Int) = edit { it.copy(trackWidthMm = value) }

    fun setDrop(value: Int) = edit { it.copy(dropMm = value) }

    fun setClearance(value: Int) = edit { it.copy(clearanceMm = value) }

    fun setFabricWidth(value: Int) = edit { it.copy(fabricWidthMm = value) }

    fun setPatternRepeat(value: Int) = edit {
        it.copy(patternRepeatMm = value, patternMatch = if (value == 0) false else it.patternMatch)
    }

    fun setPatternMatch(value: Boolean) = edit { it.copy(patternMatch = value) }

    fun setLined(value: Boolean) = edit { it.copy(lined = value) }

    fun setHeading(style: HeadingStyle) = edit {
        it.copy(heading = style, fullness = style.defaultFullness)
    }

    fun setFullness(value: Float) = edit {
        it.copy(fullness = it.heading.clampFullness((value * 10f).roundToInt() / 10f))
    }

    fun applyPreset(trackWidthMm: Int, dropMm: Int) = edit {
        it.copy(trackWidthMm = trackWidthMm, dropMm = dropMm)
    }

    fun next() {
        _state.update { it.copy(step = it.step.next()) }
    }

    fun back() {
        _state.update { it.copy(step = it.step.previous()) }
    }

    fun openDialog() {
        _state.update { it.copy(dialogVisible = true) }
    }

    fun dismissDialog() {
        _state.update { it.copy(dialogVisible = false) }
    }

    fun park(room: String, label: String) {
        val current = _state.value
        viewModelScope.launch {
            val stored = withContext(Dispatchers.IO) {
                persist(current.window, room, label)
            }
            _state.update { decorate(it.copy(window = stored, dialogVisible = false)) }
            _events.send(GameEvent.Parked)
        }
    }

    fun calculate() {
        val current = _state.value
        if (current.error != null) return
        viewModelScope.launch {
            val stored = withContext(Dispatchers.IO) {
                persist(current.window, current.window.room, current.window.label)
            }
            _state.update { decorate(it.copy(window = stored)) }
            _events.send(GameEvent.Calculated(stored.id))
        }
    }

    fun persistDraft() {
        val current = _state.value.window
        if (current.id.isBlank()) return
        viewModelScope.launch {
            withContext(Dispatchers.IO) { windows.store(current) }
        }
    }

    private fun persist(window: CurtainWindow, room: String, label: String): CurtainWindow {
        val id = window.id.ifBlank { windows.newId() }
        val stored = window.copy(
            id = id,
            room = room.ifBlank { DEFAULT_ROOM },
            label = label.ifBlank { DEFAULT_LABEL }
        )
        windows.store(stored)
        return stored
    }

    private fun edit(block: (CurtainWindow) -> CurtainWindow) {
        _state.update { current -> decorate(current.copy(window = block(current.window))) }
    }

    private fun decorate(source: GameUiState): GameUiState {
        val window = source.window
        val settings = source.settings
        val result = calculate(window, settings)
        val span = window.heading.maxFullness - window.heading.minFullness
        val position = if (span > 0f) {
            (window.fullness - window.heading.minFullness) / span
        } else {
            0.5f
        }
        val band = when {
            position >= 0.66f -> FoldsBand.GENEROUS
            position >= 0.33f -> FoldsBand.TAILORED
            else -> FoldsBand.FLAT
        }
        val error = when {
            window.trackWidthMm !in
                GameConfig.TRACK_WIDTH_MIN_MM..GameConfig.TRACK_WIDTH_MAX_MM ->
                GameError.TRACK_WIDTH
            window.dropMm !in GameConfig.DROP_MIN_MM..GameConfig.DROP_MAX_MM -> GameError.DROP
            window.fabricWidthMm !in
                GameConfig.FABRIC_WIDTH_MIN_MM..GameConfig.FABRIC_WIDTH_MAX_MM ->
                GameError.FABRIC_WIDTH
            else -> null
        }
        return source.copy(
            trackLabel = format.length(window.trackWidthMm, settings.units),
            dropLabel = format.length(window.dropMm, settings.units),
            clearanceLabel = format.length(window.clearanceMm, settings.units),
            fabricWidthLabel = format.length(window.fabricWidthMm, settings.units),
            repeatLabel = format.length(window.patternRepeatMm, settings.units),
            fullnessLabel = format.fullness(window.fullness),
            foldsBand = band,
            widths = result.widths,
            cutLengthLabel = format.length(result.cutLengthMm, settings.units),
            totalLabel = format.bulk(result.totalFabricMm, settings.units),
            foldCount = (window.fullness * 9f).roundToInt(),
            error = error
        )
    }

    private companion object {
        const val DEFAULT_ROOM = "LIVING ROOM"
        const val DEFAULT_LABEL = "NEW WINDOW"
    }
}
