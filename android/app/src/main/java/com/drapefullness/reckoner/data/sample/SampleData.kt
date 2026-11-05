package com.drapefullness.reckoner.data.sample

import com.drapefullness.reckoner.domain.model.CurtainWindow
import com.drapefullness.reckoner.domain.model.HeadingStyle

object SampleData {

    val windows: List<CurtainWindow> = listOf(
        CurtainWindow(
            id = "w_bay_south",
            room = "LIVING ROOM",
            label = "BAY WINDOW SOUTH",
            trackWidthMm = 3000,
            dropMm = 2400,
            clearanceMm = 10,
            heading = HeadingStyle.PINCH_PLEAT,
            fullness = 2.2f,
            fabricWidthMm = 1370,
            patternRepeatMm = 0,
            patternMatch = true,
            lined = true
        ),
        CurtainWindow(
            id = "w_patio_door",
            room = "LIVING ROOM",
            label = "PATIO DOOR",
            trackWidthMm = 2200,
            dropMm = 2450,
            clearanceMm = 10,
            heading = HeadingStyle.WAVE,
            fullness = 2.1f,
            fabricWidthMm = 1370,
            patternRepeatMm = 320,
            patternMatch = true,
            lined = true
        ),
        CurtainWindow(
            id = "w_over_bed",
            room = "MASTER BEDROOM",
            label = "WINDOW OVER BED",
            trackWidthMm = 1800,
            dropMm = 2400,
            clearanceMm = 10,
            heading = HeadingStyle.PENCIL_PLEAT,
            fullness = 2.3f,
            fabricWidthMm = 1370,
            patternRepeatMm = 0,
            patternMatch = true,
            lined = true
        ),
        CurtainWindow(
            id = "w_sink_window",
            room = "KITCHEN",
            label = "SINK WINDOW",
            trackWidthMm = 1400,
            dropMm = 1200,
            clearanceMm = 10,
            heading = HeadingStyle.EYELET,
            fullness = 2.0f,
            fabricWidthMm = 1370,
            patternRepeatMm = 0,
            patternMatch = false,
            lined = false
        )
    )

    val rooms: List<String> = listOf("LIVING ROOM", "MASTER BEDROOM", "KITCHEN", "HALLWAY")

    val listedIds: Set<String> = setOf("w_bay_south", "w_over_bed")
}
