package com.drapefullness.reckoner.domain.repository

import com.drapefullness.reckoner.domain.model.CurtainWindow

interface WindowRepository {
    fun all(): List<CurtainWindow>
    fun byId(id: String): CurtainWindow?
    fun store(window: CurtainWindow): CurtainWindow
    fun rooms(): List<String>
    fun listedIds(): Set<String>
    fun addToList(windowId: String)
    fun tickedKeys(): Set<String>
    fun setTicked(key: String, ticked: Boolean)
    fun clearTicked()
    fun newId(): String
}
