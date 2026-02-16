package com.example.household_ledger.core.network

import com.example.household_ledger.core.network.api.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    private const val BASE_URL = "http://10.0.2.2:3000/api/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val transactionApi: TransactionApi by lazy { retrofit.create(TransactionApi::class.java) }
    val categoryApi: CategoryApi by lazy { retrofit.create(CategoryApi::class.java) }
    val candidateApi: CandidateApi by lazy { retrofit.create(CandidateApi::class.java) }
    val budgetApi: BudgetApi by lazy { retrofit.create(BudgetApi::class.java) }
    val statsApi: StatsApi by lazy { retrofit.create(StatsApi::class.java) }
    val aiApi: AiApi by lazy { retrofit.create(AiApi::class.java) }
}
