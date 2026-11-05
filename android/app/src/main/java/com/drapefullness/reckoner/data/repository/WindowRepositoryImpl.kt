package com.drapefullness.reckoner.data.repository

import com.drapefullness.reckoner.data.local.CurtainPreferences
import com.drapefullness.reckoner.data.sample.SampleData
import com.drapefullness.reckoner.domain.model.CurtainWindow
import com.drapefullness.reckoner.domain.model.HeadingStyle
import com.drapefullness.reckoner.domain.repository.WindowRepository

class WindowRepositoryImpl(
    private val preferences: CurtainPreferences
) : WindowRepository {

    init {
        if (!preferences.flag(KEY_SEEDED, false)) {
            preferences.putText(KEY_WINDOWS, encodeAll(SampleData.windows))
            preferences.putKeys(KEY_LISTED, SampleData.listedIds)
            preferences.putFlag(KEY_SEEDED, true)
        }
    }

    override fun all(): List<CurtainWindow> = decodeAll(preferences.text(KEY_WINDOWS, ""))

    override fun byId(id: String): CurtainWindow? = all().firstOrNull { it.id == id }

    override fun store(window: CurtainWindow): CurtainWindow {
        val current = all().toMutableList()
        val index = current.indexOfFirst { it.id == window.id }
        if (index >= 0) current[index] = window else current.add(window)
        preferences.putText(KEY_WINDOWS, encodeAll(current))
        return window
    }

    override fun rooms(): List<String> {
        val saved = all().map { it.room }
        return (saved + SampleData.rooms).distinct()
    }

    override fun listedIds(): Set<String> = preferences.keys(KEY_LISTED)

    override fun addToList(windowId: String) {
        preferences.putKeys(KEY_LISTED, listedIds() + windowId)
    }

    override fun tickedKeys(): Set<String> = preferences.keys(KEY_TICKED)

    override fun setTicked(key: String, ticked: Boolean) {
        val current = tickedKeys()
        preferences.putKeys(KEY_TICKED, if (ticked) current + key else current - key)
    }

    override fun clearTicked() {
        preferences.putKeys(KEY_TICKED, emptySet())
    }

    override fun newId(): String {
        val next = preferences.number(KEY_SEQUENCE, 1)
        preferences.putNumber(KEY_SEQUENCE, next + 1)
        return "w_local_" + next
    }

    private fun encodeAll(windows: List<CurtainWindow>): String =
        windows.joinToString(RECORD) { encode(it) }

    private fun encode(window: CurtainWindow): String = listOf(
        window.id,
        window.room,
        window.label,
        window.trackWidthMm.toString(),
        window.dropMm.toString(),
        window.clearanceMm.toString(),
        window.heading.name,
        window.fullness.toString(),
        window.fabricWidthMm.toString(),
        window.patternRepeatMm.toString(),
        window.patternMatch.toString(),
        window.lined.toString()
    ).joinToString(FIELD)

    private fun decodeAll(raw: String): List<CurtainWindow> {
        if (raw.isEmpty()) return emptyList()
        return raw.split(RECORD).mapNotNull { decode(it) }
    }

    private fun decode(raw: String): CurtainWindow? {
        val parts = raw.split(FIELD)
        if (parts.size < 12) return null
        return runCatching {
            CurtainWindow(
                id = parts[0],
                room = parts[1],
                label = parts[2],
                trackWidthMm = parts[3].toInt(),
                dropMm = parts[4].toInt(),
                clearanceMm = parts[5].toInt(),
                heading = HeadingStyle.valueOf(parts[6]),
                fullness = parts[7].toFloat(),
                fabricWidthMm = parts[8].toInt(),
                patternRepeatMm = parts[9].toInt(),
                patternMatch = parts[10].toBoolean(),
                lined = parts[11].toBoolean()
            )
        }.getOrNull()
    }

    private companion object {
        const val RECORD = "\u001E"
        const val FIELD = "\u001F"
        const val KEY_WINDOWS = "windows"
        const val KEY_LISTED = "listed_windows"
        const val KEY_TICKED = "ticked_items"
        const val KEY_SEEDED = "seeded"
        const val KEY_SEQUENCE = "id_sequence"
    }
}
