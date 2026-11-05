package com.drapefullness.reckoner.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.drapefullness.reckoner.core.di.AppViewModelFactory
import com.drapefullness.reckoner.core.navigation.Screen
import com.drapefullness.reckoner.presentation.game.GameScreen
import com.drapefullness.reckoner.presentation.gameover.GameOverScreen
import com.drapefullness.reckoner.presentation.guide.GuideScreen
import com.drapefullness.reckoner.presentation.menu.MenuScreen
import com.drapefullness.reckoner.presentation.settings.SettingsScreen
import com.drapefullness.reckoner.presentation.shoppinglist.ShoppingListScreen
import com.drapefullness.reckoner.presentation.splash.SplashScreen
import com.drapefullness.reckoner.presentation.stackpreview.StackPreviewScreen

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory.instance)
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val canGoBack by appViewModel.canGoBack.collectAsStateWithLifecycle()

    BackHandler(enabled = canGoBack) { appViewModel.back() }

    AnimatedContent(
        targetState = screen,
        transitionSpec = { fadeIn(tween(240)) togetherWith fadeOut(tween(200)) },
        label = "screen",
        modifier = Modifier.fillMaxSize()
    ) { target ->
        when (target) {
            Screen.Splash -> SplashScreen(
                onFinished = { appViewModel.enterMenu() }
            )

            Screen.Menu -> MenuScreen(
                onStart = { appViewModel.navigate(Screen.Game()) },
                onOpenWindow = { windowId -> appViewModel.navigate(Screen.GameOver(windowId)) },
                onShoppingList = { appViewModel.navigate(Screen.ShoppingList) },
                onGuide = { appViewModel.navigate(Screen.Guide) },
                onSettings = { appViewModel.navigate(Screen.Settings) }
            )

            is Screen.Game -> GameScreen(
                windowId = target.windowId,
                onCalculated = { windowId -> appViewModel.navigate(Screen.GameOver(windowId)) },
                onParked = { appViewModel.enterMenu() },
                onBack = { appViewModel.back() }
            )

            is Screen.GameOver -> GameOverScreen(
                windowId = target.windowId,
                onMeasureAgain = { windowId -> appViewModel.navigate(Screen.Game(windowId)) },
                onStackPreview = { windowId ->
                    appViewModel.navigate(Screen.StackPreview(windowId))
                },
                onMenu = { appViewModel.enterMenu() }
            )

            is Screen.StackPreview -> StackPreviewScreen(
                windowId = target.windowId,
                onBack = { appViewModel.back() },
                onMenu = { appViewModel.enterMenu() }
            )

            Screen.ShoppingList -> ShoppingListScreen(
                onBack = { appViewModel.back() },
                onMenu = { appViewModel.enterMenu() }
            )

            Screen.Guide -> GuideScreen(
                onBack = { appViewModel.back() },
                onMenu = { appViewModel.enterMenu() }
            )

            Screen.Settings -> SettingsScreen(
                onBack = { appViewModel.back() },
                onMenu = { appViewModel.enterMenu() }
            )
        }
    }
}
