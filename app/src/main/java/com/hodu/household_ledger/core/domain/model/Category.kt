package com.hodu.household_ledger.core.domain.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class Category(
    val id: Long,
    val name: String,
    val type: TransactionType,
    val icon: ImageVector,
    val color: Color
)
