package com.hodu.household_ledger.core.network.api

import com.hodu.household_ledger.core.network.dto.CategoryBreakdownDto
import com.hodu.household_ledger.core.network.dto.DailyTotalItemDto
import com.hodu.household_ledger.core.network.dto.MonthlySummaryDto
import retrofit2.http.GET
import retrofit2.http.Path

interface StatsApi {

    @GET("stats/monthly-summary/{year}/{month}")
    suspend fun getMonthlySummary(
        @Path("year") year: Int,
        @Path("month") month: Int
    ): MonthlySummaryDto

    @GET("stats/category-breakdown/{year}/{month}")
    suspend fun getCategoryBreakdown(
        @Path("year") year: Int,
        @Path("month") month: Int
    ): List<CategoryBreakdownDto>

    @GET("stats/daily-totals/{year}/{month}")
    suspend fun getDailyTotals(
        @Path("year") year: Int,
        @Path("month") month: Int
    ): List<DailyTotalItemDto>
}
