package com.drapefullness.reckoner.core.di

import android.content.Context
import com.drapefullness.reckoner.data.local.CurtainPreferences
import com.drapefullness.reckoner.data.repository.SettingsRepositoryImpl
import com.drapefullness.reckoner.data.repository.WindowRepositoryImpl
import com.drapefullness.reckoner.domain.model.SewingSettings
import com.drapefullness.reckoner.domain.repository.SettingsRepository
import com.drapefullness.reckoner.domain.repository.WindowRepository
import com.drapefullness.reckoner.domain.usecase.BuildShoppingListUseCase
import com.drapefullness.reckoner.domain.usecase.CalculateCurtainUseCase
import com.drapefullness.reckoner.domain.usecase.CalculatePleatPlanUseCase
import com.drapefullness.reckoner.domain.usecase.FormatMeasurementUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object ServiceLocator {

    private var preferences: CurtainPreferences? = null
    private var windowStore: WindowRepository? = null
    private var settingsStore: SettingsRepository? = null

    val calculateCurtain = CalculateCurtainUseCase()
    val calculatePleatPlan = CalculatePleatPlanUseCase()
    val formatMeasurement = FormatMeasurementUseCase()
    val buildShoppingList = BuildShoppingListUseCase(calculateCurtain, calculatePleatPlan)

    private val _eveningTheme = MutableStateFlow(false)
    val eveningTheme: StateFlow<Boolean> = _eveningTheme.asStateFlow()

    private val _haptics = MutableStateFlow(true)
    val haptics: StateFlow<Boolean> = _haptics.asStateFlow()

    val windows: WindowRepository
        get() = requireNotNull(windowStore)

    val settings: SettingsRepository
        get() = requireNotNull(settingsStore)

    fun start(context: Context) {
        if (preferences != null) return
        val store = CurtainPreferences(context)
        preferences = store
        windowStore = WindowRepositoryImpl(store)
        settingsStore = SettingsRepositoryImpl(store)
        publish(settings.load())
    }

    fun publish(value: SewingSettings) {
        _eveningTheme.value = value.eveningTheme
        _haptics.value = value.haptics
    }
}
