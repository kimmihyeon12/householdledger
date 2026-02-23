package com.hodu.household_ledger.core.data.repository

import com.hodu.household_ledger.core.data.mapper.CategoryMapper
import com.hodu.household_ledger.core.domain.model.Category
import com.hodu.household_ledger.core.network.ApiClient
import com.hodu.household_ledger.core.network.dto.CategoryBreakdownDto
import com.hodu.household_ledger.core.network.dto.MonthlySummaryDto

class StatsRepository {

    private val api = ApiClient.statsApi

    suspend fun getMonthlySummary(year: Int, month: Int): MonthlySummaryDto {
        return api.getMonthlySummary(year, month)
    }

    suspend fun getCategoryBreakdown(year: Int, month: Int): List<CategoryBreakdownDto> {
        return api.getCategoryBreakdown(year, month)
    }

    suspend fun getDailyTotals(year: Int, month: Int): Map<Int, Pair<Long, Long>> {
        val items = api.getDailyTotals(year, month)
        return items.associate { it.day to (it.income to it.expense) }
    }
}
