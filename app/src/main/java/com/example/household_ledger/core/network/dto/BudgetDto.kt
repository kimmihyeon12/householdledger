package com.example.household_ledger.core.network.dto

data class BudgetDto(
    val year: Int,
    val month: Int,
    val amount: Long
)

data class SetBudgetDto(
    val amount: Long
)
