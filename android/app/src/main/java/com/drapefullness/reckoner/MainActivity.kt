package com.drapefullness.reckoner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.drapefullness.reckoner.core.di.ServiceLocator
import com.drapefullness.reckoner.core.theme.AppTheme
import com.drapefullness.reckoner.presentation.app.AppRoot

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        ServiceLocator.start(applicationContext)
        setContent {
            AppTheme {
                AppRoot()
            }
        }
    }
}
