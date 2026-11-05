package com.drapefullness.reckoner.domain.usecase

import com.drapefullness.reckoner.domain.model.CurtainWindow
import com.drapefullness.reckoner.domain.model.SewingSettings
import com.drapefullness.reckoner.domain.model.ShoppingGroup
import com.drapefullness.reckoner.domain.model.ShoppingItem
import com.drapefullness.reckoner.domain.model.ShoppingKind
import com.drapefullness.reckoner.domain.model.ShoppingTotals

data class ShoppingList(
    val groups: List<ShoppingGroup>,
    val totals: ShoppingTotals
)

class BuildShoppingListUseCase(
    private val calculate: CalculateCurtainUseCase,
    private val pleatPlan: CalculatePleatPlanUseCase
) {

    operator fun invoke(
        windows: List<CurtainWindow>,
        settings: SewingSettings,
        ticked: Set<String>
    ): ShoppingList {
        var fabric = 0
        var lining = 0
        var hooks = 0
        var tape = 0
        val groups = windows.groupBy { it.room }.map { entry ->
            val items = mutableListOf<ShoppingItem>()
            entry.value.forEach { window ->
                val result = calculate(window, settings)
                val plan = pleatPlan(window, result)
                fabric += result.buyFabricMm
                tape += result.headingTapeMm
                hooks += plan.hooksTotal
                items += item(window, ShoppingKind.FABRIC, result.buyFabricMm, ticked)
                result.lining?.let {
                    lining += it.totalMm
                    items += item(window, ShoppingKind.LINING, it.totalMm, ticked)
                }
                if (plan.hooksTotal > 0) {
                    items += item(window, ShoppingKind.HOOKS, plan.hooksTotal, ticked)
                }
                items += item(window, ShoppingKind.TAPE, result.headingTapeMm, ticked)
            }
            ShoppingGroup(room = entry.key, items = items)
        }
        return ShoppingList(
            groups = groups,
            totals = ShoppingTotals(
                fabricMm = fabric,
                liningMm = lining,
                hooks = hooks,
                tapeMm = tape
            )
        )
    }

    private fun item(
        window: CurtainWindow,
        kind: ShoppingKind,
        amount: Int,
        ticked: Set<String>
    ): ShoppingItem {
        val key = window.id + "_" + kind.name
        return ShoppingItem(
            key = key,
            windowId = window.id,
            windowLabel = window.label,
            kind = kind,
            amount = amount,
            ticked = ticked.contains(key)
        )
    }
}
