package com.drapefullness.reckoner.domain.usecase

import com.drapefullness.reckoner.core.config.GameConfig
import com.drapefullness.reckoner.domain.model.CurtainResult
import com.drapefullness.reckoner.domain.model.CurtainWindow
import com.drapefullness.reckoner.domain.model.LiningResult
import com.drapefullness.reckoner.domain.model.SewingSettings
import kotlin.math.ceil
import kotlin.math.roundToInt

class CalculateCurtainUseCase {

    operator fun invoke(window: CurtainWindow, settings: SewingSettings): CurtainResult {
        val finishedDrop = (window.dropMm - window.clearanceMm).coerceAtLeast(100)
        val finishedWidth = (window.trackWidthMm * window.fullness).roundToInt()
        val requirement = finishedWidth + 2 * GameConfig.RETURN_DEPTH_MM + GameConfig.OVERLAP_MM
        val usable = (window.fabricWidthMm - 2 * settings.sideHemMm).coerceAtLeast(100)

        val rawWidths = ceil(requirement.toFloat() / usable.toFloat()).toInt().coerceAtLeast(1)
        val evenWidths = (if (rawWidths % 2 == 0) rawWidths else rawWidths + 1).coerceAtLeast(2)

        val plainCut = finishedDrop + settings.headerAllowanceMm + settings.bottomHemMm
        val matched = window.patternRepeatMm > 0 && window.patternMatch
        val cutLength = if (matched) {
            ceil(plainCut.toFloat() / window.patternRepeatMm.toFloat()).toInt() * window.patternRepeatMm
        } else {
            plainCut
        }
        val patternAdded = cutLength - plainCut

        val totalFabric = evenWidths * cutLength
        val buyFabric = ceil(totalFabric.toFloat() / GameConfig.BUY_ROUNDING_MM).toInt() *
            GameConfig.BUY_ROUNDING_MM
        val flatPerCurtain = (evenWidths / 2) * usable

        val lining = if (window.lined) {
            val liningCut = finishedDrop + settings.liningHeaderMm + settings.liningHemMm
            LiningResult(
                widths = evenWidths,
                cutLengthMm = liningCut,
                totalMm = evenWidths * liningCut,
                headerMm = settings.liningHeaderMm,
                hemMm = settings.liningHemMm,
                setBackMm = GameConfig.LINING_SET_BACK_MM
            )
        } else {
            null
        }

        val ratio = if (window.heading.defaultFullness > 0f) {
            window.fullness / window.heading.defaultFullness
        } else {
            1f
        }
        val fraction = (window.heading.stackFactor * ratio)
            .coerceIn(GameConfig.MIN_STACK_FRACTION, GameConfig.MAX_STACK_FRACTION)
        val stackBack = (window.trackWidthMm * fraction).roundToInt()
        val covered = ((2f * stackBack / window.trackWidthMm.toFloat()) * 100f).roundToInt()

        return CurtainResult(
            finishedWidthMm = finishedWidth,
            finishedDropMm = finishedDrop,
            widths = evenWidths,
            widthsPerCurtain = evenWidths / 2,
            cutLengthMm = cutLength,
            patternAddedMm = patternAdded,
            totalFabricMm = totalFabric,
            buyFabricMm = buyFabric,
            flatWidthPerCurtainMm = flatPerCurtain,
            usableFabricWidthMm = usable,
            headingTapeMm = evenWidths * window.fabricWidthMm,
            sideHemMm = settings.sideHemMm,
            bottomHemMm = settings.bottomHemMm,
            headerAllowanceMm = settings.headerAllowanceMm,
            lining = lining,
            stackBackPerSideMm = stackBack,
            glassCoveredPercent = covered.coerceIn(0, 100)
        )
    }
}
