package com.example.household_ledger.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.household_ledger.ui.navigation.Routes
import com.example.household_ledger.ui.theme.*

enum class BottomNavItem(
    val route: String,
    val label: String,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    HOME(Routes.HOME, "홈", Icons.Outlined.Dashboard, Icons.Filled.Dashboard),
    CALENDAR(Routes.CALENDAR, "캘린더", Icons.Outlined.DateRange, Icons.Filled.DateRange),
    INBOX(Routes.INBOX, "검토함", Icons.Outlined.FactCheck, Icons.Filled.FactCheck),
    SETTINGS(Routes.SETTINGS, "설정", Icons.Outlined.Tune, Icons.Filled.Tune)
}

@Composable
fun BottomNavBar(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        tonalElevation = 0.dp,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        BottomNavItem.entries.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                icon = {
                    Icon(
                        if (selected) item.selectedIcon else item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        item.label,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                selected = selected,
                onClick = { onNavigate(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = GoldPrimary,
                    selectedTextColor = GoldPrimary,
                    unselectedIconColor = Navy400,
                    unselectedTextColor = Navy400,
                    indicatorColor = GoldLight
                )
            )
        }
    }
}
