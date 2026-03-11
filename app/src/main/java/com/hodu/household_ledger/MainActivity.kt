package com.hodu.household_ledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.hodu.household_ledger.core.common.AppError
import com.hodu.household_ledger.core.common.AppState
import com.hodu.household_ledger.core.ui.component.LoadingOverlay
import com.hodu.household_ledger.core.ui.component.NetworkErrorToast
import com.hodu.household_ledger.core.ui.navigation.AppNavigation
import com.hodu.household_ledger.core.ui.theme.AutoLedgerTheme
import com.hodu.household_ledger.core.ui.theme.ThemePreferences

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Householdledger)
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        var themeMode by mutableStateOf(ThemePreferences.getThemeMode(this))

        setContent {
            AutoLedgerTheme(
                themeMode = themeMode,
                onThemeModeChange = { mode ->
                    themeMode = mode
                    ThemePreferences.setThemeMode(this@MainActivity, mode)
                }
            ) {
                var currentError by mutableStateOf<AppError?>(null)

                LaunchedEffect(Unit) {
                    AppState.errorEvent.collect { error ->
                        currentError = error
                    }
                }

                Box(modifier = Modifier.fillMaxSize()) {
                    AppNavigation()
                    LoadingOverlay()
                    NetworkErrorToast(
                        error = currentError,
                        onDismiss = { currentError = null }
                    )
                }
            }
        }
    }
}
