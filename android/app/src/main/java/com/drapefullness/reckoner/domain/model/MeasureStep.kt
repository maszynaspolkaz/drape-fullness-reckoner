package com.drapefullness.reckoner.domain.model

enum class MeasureStep {
    MEASURE,
    HEADING,
    FABRIC;

    fun next(): MeasureStep = when (this) {
        MEASURE -> HEADING
        HEADING -> FABRIC
        FABRIC -> FABRIC
    }

    fun previous(): MeasureStep = when (this) {
        MEASURE -> MEASURE
        HEADING -> MEASURE
        FABRIC -> HEADING
    }
}
