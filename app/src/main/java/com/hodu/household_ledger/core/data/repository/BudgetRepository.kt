package com.hodu.household_ledger.core.data.repository

import com.hodu.household_ledger.core.network.ApiClient
import com.hodu.household_ledger.core.network.dto.SetBudgetDto

class BudgetRepository {

    private val api = ApiClient.budgetApi

    suspend fun getBudget(year: Int, month: Int): Long {
        return try {
            api.getBudget(year, month).amount
        } catch (e: Exception) {
            0L
        }
    }

    suspend fun setBudget(year: Int, month: Int, amount: Long): Long {
        return api.setBudget(year, month, SetBudgetDto(amount)).amount
    }
}
