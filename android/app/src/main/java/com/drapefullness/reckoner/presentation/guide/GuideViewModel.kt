package com.drapefullness.reckoner.presentation.guide

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GuideViewModel : ViewModel() {

    private val _state = MutableStateFlow(GuideUiState())
    val state: StateFlow<GuideUiState> = _state.asStateFlow()

    fun toggle(index: Int) {
        _state.update { current ->
            current.copy(expanded = if (current.expanded == index) -1 else index)
        }
    }
}
