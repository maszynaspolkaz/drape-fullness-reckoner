package com.drapefullness.reckoner.core.ui

import com.drapefullness.reckoner.R
import com.drapefullness.reckoner.domain.model.HeadingStyle

fun headingSprite(style: HeadingStyle): Int = when (style) {
    HeadingStyle.PENCIL_PLEAT -> R.drawable.sprite_1
    HeadingStyle.EYELET -> R.drawable.sprite_2
    HeadingStyle.PINCH_PLEAT -> R.drawable.sprite_3
    HeadingStyle.WAVE -> R.drawable.sprite_4
    HeadingStyle.TAB_TOP -> R.drawable.sprite_5
}

fun headingName(style: HeadingStyle): Int = when (style) {
    HeadingStyle.PENCIL_PLEAT -> R.string.heading_pencil_pleat
    HeadingStyle.EYELET -> R.string.heading_eyelet
    HeadingStyle.PINCH_PLEAT -> R.string.heading_pinch_pleat
    HeadingStyle.WAVE -> R.string.heading_wave
    HeadingStyle.TAB_TOP -> R.string.heading_tab_top
}

fun headingBand(style: HeadingStyle): Int = when (style) {
    HeadingStyle.PENCIL_PLEAT -> R.string.band_pencil_pleat
    HeadingStyle.EYELET -> R.string.band_eyelet
    HeadingStyle.PINCH_PLEAT -> R.string.band_pinch_pleat
    HeadingStyle.WAVE -> R.string.band_wave
    HeadingStyle.TAB_TOP -> R.string.band_tab_top
}

fun headingUnitLabel(style: HeadingStyle): Int = when (style) {
    HeadingStyle.PENCIL_PLEAT -> R.string.units_hooks
    HeadingStyle.EYELET -> R.string.units_eyelets
    HeadingStyle.PINCH_PLEAT -> R.string.units_pleats
    HeadingStyle.WAVE -> R.string.units_carriers
    HeadingStyle.TAB_TOP -> R.string.units_tabs
}
