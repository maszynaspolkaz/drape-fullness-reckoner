package com.drapefullness.reckoner.presentation.game

import com.drapefullness.reckoner.domain.model.CurtainWindow
import com.drapefullness.reckoner.domain.model.MeasureStep
import com.drapefullness.reckoner.domain.model.SewingSettings

enum class FoldsBand {
    FLAT,
    TAILORED,
    GENEROUS
}

enum class GameError {
    TRACK_WIDTH,
    DROP,
    FABRIC_WIDTH
}

sealed interface GameEvent {
    data class Calculated(val windowId: String) : GameEvent
    data object Parked : GameEvent
}

data class GameUiState(
    val step: MeasureStep = MeasureStep.MEASURE,
    val window: CurtainWindow = CurtainWindow(id = "", room = "", label = ""),
    val settings: SewingSettings = SewingSettings(),
    val trackLabel: String = "",
    val dropLabel: String = "",
    val clearanceLabel: String = "",
    val fabricWidthLabel: String = "",
    val repeatLabel: String = "",
    val fullnessLabel: String = "",
    val foldsBand: FoldsBand = FoldsBand.TAILORED,
    val widths: Int = 0,
    val cutLengthLabel: String = "",
    val totalLabel: String = "",
    val foldCount: Int = 12,
    val error: GameError? = null,
    val rooms: List<String> = emptyList(),
    val dialogVisible: Boolean = false
)

data class GameActions(
    val onTrack: (Int) -> Unit,
    val onDrop: (Int) -> Unit,
    val onClearance: (Int) -> Unit,
    val onFabricWidth: (Int) -> Unit,
    val onRepeat: (Int) -> Unit,
    val onPatternMatch: (Boolean) -> Unit,
    val onLined: (Boolean) -> Unit,
    val onHeading: (com.drapefullness.reckoner.domain.model.HeadingStyle) -> Unit,
    val onFullness: (Float) -> Unit,
    val onPreset: (Int, Int) -> Unit,
    val onNext: () -> Unit,
    val onStepBack: () -> Unit,
    val onCalculate: () -> Unit,
    val onPark: () -> Unit
)
