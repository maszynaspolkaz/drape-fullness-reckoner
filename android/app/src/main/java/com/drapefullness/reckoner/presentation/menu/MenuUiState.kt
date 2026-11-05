package com.drapefullness.reckoner.presentation.menu

data class WindowRow(
    val id: String,
    val label: String,
    val size: String,
    val fabric: String
)

data class RoomGroup(
    val room: String,
    val rows: List<WindowRow>
)

data class MenuUiState(
    val loading: Boolean = true,
    val groups: List<RoomGroup> = emptyList(),
    val windowCount: String = "",
    val fabricTotal: String = "",
    val showStats: Boolean = false
) {
    val isEmpty: Boolean
        get() = !loading && groups.isEmpty()
}
