package com.example.household_ledger.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.household_ledger.ui.theme.*

data class Category(
    val id: Long,
    val name: String,
    val type: TransactionType,
    val icon: ImageVector,
    val color: Color
)
