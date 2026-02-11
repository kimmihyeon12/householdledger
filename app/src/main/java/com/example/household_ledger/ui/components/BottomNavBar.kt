package com.example.household_ledger.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    val isDark = LocalIsDark.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = if (isDark) Navy800 else Color.White,
        shadowElevation = if (isDark) 0.dp else 8.dp,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomNavItem.entries.forEach { item ->
                val selected = currentRoute == item.route

                val iconColor by animateColorAsState(
                    targetValue = if (selected) GoldPrimary
                    else if (isDark) Navy400 else Navy400,
                    animationSpec = tween(200),
                    label = "iconColor"
                )
                val textColor by animateColorAsState(
                    targetValue = if (selected) GoldPrimary
                    else if (isDark) Navy400 else Navy400,
                    animationSpec = tween(200),
                    label = "textColor"
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onNavigate(item.route) }
                        .padding(vertical = 6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        if (selected) item.selectedIcon else item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.size(22.dp),
                        tint = iconColor
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        item.label,
                        fontSize = 11.sp,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        color = textColor,
                        letterSpacing = (-0.2).sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    // Selected indicator dot
                    Box(
                        modifier = Modifier
                            .width(16.dp)
                            .height(2.dp)
                            .clip(RoundedCornerShape(1.dp))
                            .background(if (selected) GoldPrimary else Color.Transparent)
                    )
                }
            }
        }
    }
}
