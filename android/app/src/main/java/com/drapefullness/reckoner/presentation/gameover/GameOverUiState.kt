package com.drapefullness.reckoner.presentation.gameover

import com.drapefullness.reckoner.domain.model.HeadingStyle

enum class ResultTab {
    CUT_SHEET,
    LINING,
    PLEATS
}

data class GameOverUiState(
    val loading: Boolean = true,
    val missing: Boolean = false,
    val tab: ResultTab = ResultTab.CUT_SHEET,
    val windowLabel: String = "",
    val heroTotal: String = "",
    val fabricWidthCaption: String = "",
    val lined: Boolean = false,
    val finishedWidth: String = "",
    val widths: String = "",
    val finishedDrop: String = "",
    val cutLength: String = "",
    val patternAdded: String = "",
    val totalFabric: String = "",
    val sideHems: String = "",
    val headerAllowance: String = "",
    val bottomHem: String = "",
    val liningWidths: String = "",
    val liningCutLength: String = "",
    val liningTotal: String = "",
    val liningHeader: String = "",
    val liningHem: String = "",
    val liningSetBack: String = "",
    val heading: HeadingStyle = HeadingStyle.PINCH_PLEAT,
    val unitsPerCurtain: String = "",
    val fabricPerUnit: String = "",
    val gapBetween: String = "",
    val hooksPerCurtain: String = "",
    val hooksTotal: String = "",
    val firstFromEdge: String = "",
    val pleatCount: Int = 0,
    val hasHooks: Boolean = true
)

sealed interface GameOverEvent {
    data object AddedToList : GameOverEvent
}
