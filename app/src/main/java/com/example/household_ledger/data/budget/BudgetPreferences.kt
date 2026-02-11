package com.example.household_ledger.data.budget

import android.content.Context

object BudgetPreferences {
    private const val PREFS_NAME = "budget_prefs"

    private fun budgetKey(year: Int, month: Int): String =
        "budget_${year}_${String.format("%02d", month)}"

    fun getMonthlyBudget(context: Context, year: Int, month: Int): Long {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getLong(budgetKey(year, month), 0L)
    }

    fun setMonthlyBudget(context: Context, year: Int, month: Int, amount: Long) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putLong(budgetKey(year, month), amount)
            .apply()
    }
}
