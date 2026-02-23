package com.hodu.household_ledger.core.network.dto

data class MonthlySummaryDto(
    val totalIncome: Long = 0,
    val totalExpense: Long = 0,
    val balance: Long = 0,
    val transactionCount: Int = 0
)

data class CategoryBreakdownDto(
    val categoryId: Long,
    val categoryName: String,
    val categoryIcon: String = "",
    val total: Long = 0,
    val count: Int = 0
)

data class DailyTotalItemDto(
    val day: Int,
    val income: Long = 0,
    val expense: Long = 0
)
