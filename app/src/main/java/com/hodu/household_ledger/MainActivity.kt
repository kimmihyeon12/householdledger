package com.hodu.household_ledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
                AppNavigation()
            }
        }
    }
}
