package com.example.household_ledger.core.network.api

import com.example.household_ledger.core.network.dto.BudgetDto
import com.example.household_ledger.core.network.dto.SetBudgetDto
import retrofit2.http.*

interface BudgetApi {

    @GET("budget/{year}/{month}")
    suspend fun getBudget(
        @Path("year") year: Int,
        @Path("month") month: Int
    ): BudgetDto

    @PUT("budget/{year}/{month}")
    suspend fun setBudget(
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Body dto: SetBudgetDto
    ): BudgetDto
}
