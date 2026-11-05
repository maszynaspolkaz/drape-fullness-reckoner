package com.drapefullness.reckoner.presentation.app

import androidx.lifecycle.ViewModel
import com.drapefullness.reckoner.core.navigation.Screen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel : ViewModel() {

    private val backStack = mutableListOf<Screen>()

    private val _screen = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = _screen.asStateFlow()

    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    fun navigate(target: Screen) {
        val current = _screen.value
        if (current != Screen.Splash) {
            backStack.add(current)
        }
        _screen.value = target
        _canGoBack.value = backStack.isNotEmpty()
    }

    fun enterMenu() {
        backStack.clear()
        _screen.value = Screen.Menu
        _canGoBack.value = false
    }

    fun back() {
        if (backStack.isEmpty()) return
        val index = backStack.size - 1
        val previous = backStack[index]
        backStack.removeAt(index)
        _screen.value = previous
        _canGoBack.value = backStack.isNotEmpty()
    }
}
