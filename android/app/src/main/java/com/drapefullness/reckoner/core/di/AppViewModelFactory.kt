package com.drapefullness.reckoner.core.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.drapefullness.reckoner.presentation.app.AppViewModel
import com.drapefullness.reckoner.presentation.game.GameViewModel
import com.drapefullness.reckoner.presentation.gameover.GameOverViewModel
import com.drapefullness.reckoner.presentation.guide.GuideViewModel
import com.drapefullness.reckoner.presentation.menu.MenuViewModel
import com.drapefullness.reckoner.presentation.settings.SettingsViewModel
import com.drapefullness.reckoner.presentation.shoppinglist.ShoppingListViewModel
import com.drapefullness.reckoner.presentation.splash.SplashViewModel
import com.drapefullness.reckoner.presentation.stackpreview.StackPreviewViewModel

object AppViewModelFactory {

    val instance: ViewModelProvider.Factory = viewModelFactory {
        initializer { AppViewModel() }
        initializer { SplashViewModel() }
        initializer { GuideViewModel() }
        initializer {
            MenuViewModel(
                ServiceLocator.windows,
                ServiceLocator.settings,
                ServiceLocator.calculateCurtain,
                ServiceLocator.formatMeasurement
            )
        }
        initializer {
            GameViewModel(
                ServiceLocator.windows,
                ServiceLocator.settings,
                ServiceLocator.calculateCurtain,
                ServiceLocator.formatMeasurement
            )
        }
        initializer {
            GameOverViewModel(
                ServiceLocator.windows,
                ServiceLocator.settings,
                ServiceLocator.calculateCurtain,
                ServiceLocator.calculatePleatPlan,
                ServiceLocator.formatMeasurement
            )
        }
        initializer {
            StackPreviewViewModel(
                ServiceLocator.windows,
                ServiceLocator.settings,
                ServiceLocator.calculateCurtain,
                ServiceLocator.formatMeasurement
            )
        }
        initializer {
            ShoppingListViewModel(
                ServiceLocator.windows,
                ServiceLocator.settings,
                ServiceLocator.buildShoppingList,
                ServiceLocator.formatMeasurement
            )
        }
        initializer {
            SettingsViewModel(
                ServiceLocator.settings,
                ServiceLocator.formatMeasurement
            )
        }
    }
}
