package com.drapefullness.reckoner.domain.model

data class PleatPlan(
    val heading: HeadingStyle,
    val unitsPerCurtain: Int,
    val fabricPerUnitMm: Int,
    val gapMm: Int,
    val hooksPerCurtain: Int,
    val hooksTotal: Int,
    val firstFromEdgeMm: Int
)
