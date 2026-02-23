package com.hodu.household_ledger.core.network.api

import com.hodu.household_ledger.core.network.dto.CategoryDto
import retrofit2.http.GET
import retrofit2.http.Path

interface CategoryApi {

    @GET("categories")
    suspend fun getCategories(): List<CategoryDto>

    @GET("categories/{id}")
    suspend fun getCategory(@Path("id") id: Long): CategoryDto
}
