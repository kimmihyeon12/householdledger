package com.hodu.household_ledger.core.network.api

import com.hodu.household_ledger.core.network.dto.CandidateDto
import com.hodu.household_ledger.core.network.dto.ConfirmCandidateDto
import com.hodu.household_ledger.core.network.dto.TransactionDto
import com.hodu.household_ledger.core.network.dto.UpdateCandidateStatusDto
import retrofit2.http.*

interface CandidateApi {

    @GET("candidates")
    suspend fun getCandidates(): List<CandidateDto>

    @POST("candidates/confirm/{id}")
    suspend fun confirmCandidate(
        @Path("id") id: Long,
        @Body dto: ConfirmCandidateDto
    ): TransactionDto

    @PATCH("candidates/{id}/status")
    suspend fun updateCandidateStatus(
        @Path("id") id: Long,
        @Body dto: UpdateCandidateStatusDto
    ): CandidateDto

    @DELETE("candidates/{id}")
    suspend fun deleteCandidate(@Path("id") id: Long)
}
