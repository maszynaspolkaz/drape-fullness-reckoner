package com.drapefullness.reckoner.domain.model

enum class ShoppingKind {
    FABRIC,
    LINING,
    HOOKS,
    TAPE
}

data class ShoppingItem(
    val key: String,
    val windowId: String,
    val windowLabel: String,
    val kind: ShoppingKind,
    val amount: Int,
    val ticked: Boolean
)

data class ShoppingGroup(
    val room: String,
    val items: List<ShoppingItem>
)

data class ShoppingTotals(
    val fabricMm: Int,
    val liningMm: Int,
    val hooks: Int,
    val tapeMm: Int
)
