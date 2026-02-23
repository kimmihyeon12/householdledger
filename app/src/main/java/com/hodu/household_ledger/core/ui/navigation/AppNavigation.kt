package com.hodu.household_ledger.core.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.hodu.household_ledger.core.data.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hodu.household_ledger.core.data.local.ConsentManager
import com.hodu.household_ledger.core.data.local.TokenManager
import com.hodu.household_ledger.core.data.local.UserManager
import com.hodu.household_ledger.feature.calendar.ui.CalendarScreen
import com.hodu.household_ledger.core.ui.component.BottomNavBar
import com.hodu.household_ledger.feature.home.ui.HomeScreen
import com.hodu.household_ledger.feature.inbox.ui.InboxScreen
import com.hodu.household_ledger.feature.login.ui.LoginScreen
import com.hodu.household_ledger.feature.consent.ui.ConsentScreen
import com.hodu.household_ledger.feature.settings.ui.SettingsScreen
import com.hodu.household_ledger.feature.stats.ui.StatsScreen
import com.hodu.household_ledger.feature.transaction.ui.AddEditTransactionScreen

object Routes {
    const val LOGIN = "login"
    const val CONSENT = "consent"
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
    val coroutineScope = rememberCoroutineScope()

    val hasToken = TokenManager.getToken() != null
    val startDestination = when {
        !hasToken -> Routes.LOGIN
        !ConsentManager.hasConsented() -> Routes.CONSENT
        else -> Routes.HOME
    }

    // 토큰은 있지만 사용자 정보가 없으면 서버에서 가져오기
    LaunchedEffect(hasToken) {
        if (hasToken && UserManager.getNickname() == null) {
            try {
                val user = withContext(Dispatchers.IO) { AuthRepository().getMe() }
                val provider = when {
                    user.kakaoId != null -> "kakao"
                    user.googleId != null -> "google"
                    else -> ""
                }
                UserManager.save(user.id, user.nickname, user.profileImageUrl, provider)
            } catch (_: Exception) { }
        }
    }

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
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.LOGIN) {
                LoginScreen(
                    onLoginSuccess = {
                        val destination = if (ConsentManager.hasConsented()) Routes.HOME else Routes.CONSENT
                        navController.navigate(destination) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.CONSENT) {
                ConsentScreen(
                    onConsentComplete = {
                        navController.navigate(Routes.HOME) {
                            popUpTo(Routes.CONSENT) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.HOME) {
                HomeScreen(
                    onNavigateToInbox = { navController.navigate(Routes.INBOX) },
                    onNavigateToAddTransaction = { navController.navigate(Routes.ADD_TRANSACTION) },
                    onNavigateToEditTransaction = { id -> navController.navigate("edit_transaction/$id") },
                    onNavigateToStats = {
                        navController.navigate(Routes.STATS) {
                            popUpTo(Routes.HOME) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
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
                SettingsScreen(
                    onLogout = {
                        TokenManager.clearToken()
                        UserManager.clear()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onDeleteAccount = {
                        coroutineScope.launch {
                            try {
                                withContext(Dispatchers.IO) { AuthRepository().deleteAccount() }
                            } catch (_: Exception) { }
                            TokenManager.clearToken()
                            UserManager.clear()
                            ConsentManager.clear()
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }
                )
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
