package com.drapefullness.reckoner.domain.usecase

import com.drapefullness.reckoner.domain.model.CurtainResult
import com.drapefullness.reckoner.domain.model.CurtainWindow
import com.drapefullness.reckoner.domain.model.HeadingStyle
import com.drapefullness.reckoner.domain.model.PleatPlan
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

class CalculatePleatPlanUseCase {

    operator fun invoke(window: CurtainWindow, result: CurtainResult): PleatPlan {
        val finishedHalf = (result.finishedWidthMm / 2).coerceAtLeast(200)
        return when (window.heading) {
            HeadingStyle.PENCIL_PLEAT -> evenly(window, finishedHalf, 80, 80, true)
            HeadingStyle.WAVE -> evenly(window, finishedHalf, 80, 80, true)
            HeadingStyle.TAB_TOP -> evenly(window, finishedHalf, 180, 90, false)
            HeadingStyle.EYELET -> {
                val raw = ceil(finishedHalf.toFloat() / 150f).toInt() + 1
                val eyelets = (if (raw % 2 == 0) raw else raw + 1).coerceIn(4, 60)
                val gap = (finishedHalf - 2 * 75) / (eyelets - 1)
                PleatPlan(
                    heading = window.heading,
                    unitsPerCurtain = eyelets,
                    fabricPerUnitMm = 150,
                    gapMm = gap.coerceAtLeast(0),
                    hooksPerCurtain = eyelets,
                    hooksTotal = eyelets * 2,
                    firstFromEdgeMm = 75
                )
            }
            HeadingStyle.PINCH_PLEAT -> {
                val fabricPerPleat = 140
                val spare = result.flatWidthPerCurtainMm - finishedHalf
                val pleats = floor(spare.toFloat() / fabricPerPleat).toInt().coerceIn(3, 40)
                val gap = (finishedHalf.toFloat() / pleats.toFloat()).roundToInt()
                PleatPlan(
                    heading = window.heading,
                    unitsPerCurtain = pleats,
                    fabricPerUnitMm = fabricPerPleat,
                    gapMm = gap,
                    hooksPerCurtain = pleats,
                    hooksTotal = pleats * 2,
                    firstFromEdgeMm = gap / 2
                )
            }
        }
    }

    private fun evenly(
        window: CurtainWindow,
        finishedHalf: Int,
        spacing: Int,
        edge: Int,
        hooked: Boolean
    ): PleatPlan {
        val count = (floor(finishedHalf.toFloat() / spacing).toInt() + 1).coerceIn(2, 80)
        val gap = (finishedHalf - 2 * edge) / (count - 1)
        return PleatPlan(
            heading = window.heading,
            unitsPerCurtain = count,
            fabricPerUnitMm = spacing,
            gapMm = gap.coerceAtLeast(0),
            hooksPerCurtain = if (hooked) count else 0,
            hooksTotal = if (hooked) count * 2 else 0,
            firstFromEdgeMm = edge
        )
    }
}
