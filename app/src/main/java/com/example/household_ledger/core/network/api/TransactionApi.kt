package com.example.household_ledger.core.network.api

import com.example.household_ledger.core.network.dto.CreateTransactionDto
import com.example.household_ledger.core.network.dto.TransactionDto
import com.example.household_ledger.core.network.dto.UpdateTransactionDto
import retrofit2.http.*

interface TransactionApi {

    @GET("transactions")
    suspend fun getTransactions(
        @Query("year") year: Int? = null,
        @Query("month") month: Int? = null,
        @Query("type") type: String? = null,
        @Query("categoryId") categoryId: Long? = null
    ): List<TransactionDto>

    @GET("transactions/{id}")
    suspend fun getTransaction(@Path("id") id: Long): TransactionDto

    @POST("transactions")
    suspend fun createTransaction(@Body dto: CreateTransactionDto): TransactionDto

    @PUT("transactions/{id}")
    suspend fun updateTransaction(@Path("id") id: Long, @Body dto: UpdateTransactionDto): TransactionDto

    @DELETE("transactions/{id}")
    suspend fun deleteTransaction(@Path("id") id: Long)
}
