package com.hodu.household_ledger.core.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hodu.household_ledger.core.common.AppState
import com.hodu.household_ledger.core.ui.theme.GoldPrimary

@Composable
fun LoadingOverlay() {
    val loadingCount by AppState.loadingCount.collectAsState()
    val isLoading = loadingCount > 0

    AnimatedVisibility(
        visible = isLoading,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                    onClick = {} // 터치 이벤트 소비
                ),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = GoldPrimary,
                strokeWidth = 3.dp
            )
        }
    }
}
