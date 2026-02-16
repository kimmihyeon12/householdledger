package com.example.household_ledger.core.data.mapper

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.household_ledger.core.domain.model.Category
import com.example.household_ledger.core.domain.model.TransactionType
import com.example.household_ledger.core.network.dto.CategoryDto
import com.example.household_ledger.core.ui.theme.*

object CategoryMapper {

    fun toDomain(dto: CategoryDto): Category {
        val type = when (dto.type) {
            "INCOME" -> TransactionType.INCOME
            else -> TransactionType.EXPENSE
        }
        return Category(
            id = dto.id,
            name = dto.name,
            type = type,
            icon = mapIcon(dto.icon),
            color = mapColor(dto.icon, type)
        )
    }

    fun toDomainList(dtos: List<CategoryDto>): List<Category> = dtos.map { toDomain(it) }

    private fun mapIcon(icon: String): ImageVector = when (icon) {
        "Restaurant" -> Icons.Default.Restaurant
        "DirectionsBus", "DirectionsCar", "Transport" -> Icons.Default.DirectionsBus
        "ShoppingBag", "Shopping" -> Icons.Default.ShoppingBag
        "TheaterComedy", "SportsEsports", "Entertainment" -> Icons.Default.TheaterComedy
        "LocalHospital", "Health" -> Icons.Default.LocalHospital
        "School", "Education" -> Icons.Default.School
        "Home", "Living" -> Icons.Default.Home
        "AccountBalance", "Salary" -> Icons.Default.AccountBalance
        "Wallet", "CardGiftcard", "Allowance" -> Icons.Default.Wallet
        else -> Icons.Default.MoreHoriz
    }

    private fun mapColor(icon: String, type: TransactionType): Color {
        if (type == TransactionType.INCOME) return IncomeBlue
        return when (icon) {
            "Restaurant" -> CategoryFood
            "DirectionsBus", "DirectionsCar", "Transport" -> CategoryTransport
            "ShoppingBag", "Shopping" -> CategoryShopping
            "TheaterComedy", "SportsEsports", "Entertainment" -> CategoryEntertainment
            "LocalHospital", "Health" -> CategoryHealth
            "School", "Education" -> CategoryEducation
            "Home", "Living" -> CategoryLiving
            else -> CategoryOther
        }
    }
}
