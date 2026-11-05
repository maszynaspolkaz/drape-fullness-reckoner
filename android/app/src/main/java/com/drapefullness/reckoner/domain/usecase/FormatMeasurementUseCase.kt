package com.drapefullness.reckoner.domain.usecase

import com.drapefullness.reckoner.domain.model.UnitSystem
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

class FormatMeasurementUseCase {

    fun length(mm: Int, units: UnitSystem): String = when (units) {
        UnitSystem.CM -> centimetres(mm)
        UnitSystem.INCHES -> inches(mm)
    }

    fun bulk(mm: Int, units: UnitSystem): String = when (units) {
        UnitSystem.CM -> String.format(Locale.US, "%.1f m", mm / 1000f)
        UnitSystem.INCHES -> String.format(Locale.US, "%.1f yd", mm / 914.4f)
    }

    fun size(widthMm: Int, dropMm: Int, units: UnitSystem): String =
        length(widthMm, units) + " x " + length(dropMm, units)

    fun fullness(value: Float): String = String.format(Locale.US, "%.1fx", value)

    fun percent(value: Int): String = String.format(Locale.US, "%d %%", value)

    fun count(value: Int): String = value.toString()

    private fun centimetres(mm: Int): String {
        val cm = mm / 10f
        return if (abs(cm - cm.roundToInt()) < 0.05f) {
            String.format(Locale.US, "%d cm", cm.roundToInt())
        } else {
            String.format(Locale.US, "%.1f cm", cm)
        }
    }

    private fun inches(mm: Int): String {
        val eighths = (mm / 25.4f * 8f).roundToInt()
        val whole = eighths / 8
        var numerator = eighths % 8
        var denominator = 8
        while (numerator > 0 && numerator % 2 == 0) {
            numerator /= 2
            denominator /= 2
        }
        return if (numerator == 0) {
            String.format(Locale.US, "%d in", whole)
        } else {
            String.format(Locale.US, "%d %d/%d in", whole, numerator, denominator)
        }
    }
}
