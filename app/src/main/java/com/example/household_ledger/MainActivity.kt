package com.example.household_ledger

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.household_ledger.ui.navigation.AppNavigation
import com.example.household_ledger.ui.theme.AutoLedgerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AutoLedgerTheme {
                AppNavigation()
            }
        }
    }
}
