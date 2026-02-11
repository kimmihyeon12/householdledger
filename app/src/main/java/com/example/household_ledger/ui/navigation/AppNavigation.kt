package com.example.household_ledger.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.household_ledger.ui.calendar.CalendarScreen
import com.example.household_ledger.ui.components.BottomNavBar
import com.example.household_ledger.ui.home.HomeScreen
import com.example.household_ledger.ui.inbox.InboxScreen
import com.example.household_ledger.ui.settings.SettingsScreen
import com.example.household_ledger.ui.stats.StatsScreen
import com.example.household_ledger.ui.transaction.AddEditTransactionScreen

object Routes {
    const val HOME = "home"
    const val CALENDAR = "calendar"
    const val INBOX = "inbox"
    const val STATS = "stats"
    const val SETTINGS = "settings"
    const val ADD_TRANSACTION = "add_transaction"
    const val ADD_TRANSACTION_WITH_DATE = "add_transaction/{year}/{month}/{day}"
    const val EDIT_TRANSACTION = "edit_transaction/{transactionId}"
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Routes.HOME, Routes.CALENDAR, Routes.INBOX, Routes.STATS, Routes.SETTINGS
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavBar(
                    currentRoute = currentRoute ?: Routes.HOME,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(Routes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.HOME) {
                HomeScreen(
                    onNavigateToInbox = { navController.navigate(Routes.INBOX) },
                    onNavigateToAddTransaction = { navController.navigate(Routes.ADD_TRANSACTION) },
                    onNavigateToEditTransaction = { id -> navController.navigate("edit_transaction/$id") }
                )
            }
            composable(Routes.CALENDAR) {
                CalendarScreen(
                    onNavigateToAddTransaction = { year, month, day ->
                        navController.navigate("add_transaction/$year/$month/$day")
                    },
                    onNavigateToEditTransaction = { id -> navController.navigate("edit_transaction/$id") }
                )
            }
            composable(Routes.INBOX) {
                InboxScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Routes.STATS) {
                StatsScreen()
            }
            composable(Routes.SETTINGS) {
                SettingsScreen()
            }
            composable(Routes.ADD_TRANSACTION) {
                AddEditTransactionScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(
                route = Routes.ADD_TRANSACTION_WITH_DATE,
                arguments = listOf(
                    navArgument("year") { type = NavType.IntType },
                    navArgument("month") { type = NavType.IntType },
                    navArgument("day") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val year = backStackEntry.arguments?.getInt("year")
                val month = backStackEntry.arguments?.getInt("month")
                val day = backStackEntry.arguments?.getInt("day")
                AddEditTransactionScreen(
                    onNavigateBack = { navController.popBackStack() },
                    initialYear = year,
                    initialMonth = month,
                    initialDay = day
                )
            }
            composable(
                route = Routes.EDIT_TRANSACTION,
                arguments = listOf(
                    navArgument("transactionId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val transactionId = backStackEntry.arguments?.getLong("transactionId") ?: 0L
                AddEditTransactionScreen(
                    onNavigateBack = { navController.popBackStack() },
                    editTransactionId = transactionId
                )
            }
        }
    }
}
