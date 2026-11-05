package com.drapefullness.reckoner.presentation.shoppinglist

import com.drapefullness.reckoner.domain.model.ShoppingKind

data class ShoppingRow(
    val key: String,
    val windowLabel: String,
    val kind: ShoppingKind,
    val amount: String,
    val ticked: Boolean
)

data class ShoppingRoom(
    val room: String,
    val rows: List<ShoppingRow>
)

data class ShoppingListUiState(
    val loading: Boolean = true,
    val rooms: List<ShoppingRoom> = emptyList(),
    val fabricTotal: String = "",
    val liningTotal: String = "",
    val hooksTotal: String = "",
    val tapeTotal: String = "",
    val showLining: Boolean = false,
    val showHooks: Boolean = false
) {
    val isEmpty: Boolean
        get() = !loading && rooms.isEmpty()
}
