package com.drapefullness.reckoner.domain.model

data class LiningResult(
    val widths: Int,
    val cutLengthMm: Int,
    val totalMm: Int,
    val headerMm: Int,
    val hemMm: Int,
    val setBackMm: Int
)

data class CurtainResult(
    val finishedWidthMm: Int,
    val finishedDropMm: Int,
    val widths: Int,
    val widthsPerCurtain: Int,
    val cutLengthMm: Int,
    val patternAddedMm: Int,
    val totalFabricMm: Int,
    val buyFabricMm: Int,
    val flatWidthPerCurtainMm: Int,
    val usableFabricWidthMm: Int,
    val headingTapeMm: Int,
    val sideHemMm: Int,
    val bottomHemMm: Int,
    val headerAllowanceMm: Int,
    val lining: LiningResult?,
    val stackBackPerSideMm: Int,
    val glassCoveredPercent: Int
)
