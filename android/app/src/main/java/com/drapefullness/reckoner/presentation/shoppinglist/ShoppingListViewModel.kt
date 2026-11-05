package com.drapefullness.reckoner.presentation.shoppinglist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.drapefullness.reckoner.domain.repository.SettingsRepository
import com.drapefullness.reckoner.domain.repository.WindowRepository
import com.drapefullness.reckoner.domain.model.ShoppingKind
import com.drapefullness.reckoner.domain.usecase.BuildShoppingListUseCase
import com.drapefullness.reckoner.domain.usecase.FormatMeasurementUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShoppingListViewModel(
    private val windows: WindowRepository,
    private val settingsStore: SettingsRepository,
    private val buildList: BuildShoppingListUseCase,
    private val format: FormatMeasurementUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ShoppingListUiState())
    val state: StateFlow<ShoppingListUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            val next = withContext(Dispatchers.IO) { build() }
            _state.update { next }
        }
    }

    fun toggle(key: String, ticked: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { windows.setTicked(key, ticked) }
            refresh()
        }
    }

    fun clearTicked() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) { windows.clearTicked() }
            refresh()
        }
    }

    private fun build(): ShoppingListUiState {
        val settings = settingsStore.load()
        val listed = windows.listedIds()
        val selected = windows.all().filter { listed.contains(it.id) }
        val ticked = windows.tickedKeys()
        val result = buildList(selected, settings, ticked)
        val units = settings.units
        return ShoppingListUiState(
            loading = false,
            rooms = result.groups.map { group ->
                ShoppingRoom(
                    room = group.room,
                    rows = group.items.map { item ->
                        ShoppingRow(
                            key = item.key,
                            windowLabel = item.windowLabel,
                            kind = item.kind,
                            amount = if (item.kind == ShoppingKind.HOOKS) {
                                format.count(item.amount)
                            } else {
                                format.bulk(item.amount, units)
                            },
                            ticked = item.ticked
                        )
                    }
                )
            },
            fabricTotal = format.bulk(result.totals.fabricMm, units),
            liningTotal = format.bulk(result.totals.liningMm, units),
            hooksTotal = format.count(result.totals.hooks),
            tapeTotal = format.bulk(result.totals.tapeMm, units),
            showLining = result.totals.liningMm > 0,
            showHooks = result.totals.hooks > 0
        )
    }
}
