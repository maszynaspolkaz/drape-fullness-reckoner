package com.drapefullness.reckoner.core.navigation

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data class Game(val windowId: String? = null) : Screen
    data class GameOver(val windowId: String) : Screen
    data class StackPreview(val windowId: String) : Screen
    data object ShoppingList : Screen
    data object Guide : Screen
    data object Settings : Screen
}
