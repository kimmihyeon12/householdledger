package com.hodu.household_ledger.core.network.api

import com.hodu.household_ledger.core.network.dto.AiParseRequestDto
import com.hodu.household_ledger.core.network.dto.AiParseResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AiApi {

    @POST("ai/parse-transaction")
    suspend fun parseTransaction(@Body dto: AiParseRequestDto): AiParseResponseDto
}
