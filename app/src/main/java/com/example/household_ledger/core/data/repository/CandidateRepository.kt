package com.example.household_ledger.core.data.repository

import com.example.household_ledger.core.domain.model.*
import com.example.household_ledger.core.network.ApiClient
import com.example.household_ledger.core.network.dto.CandidateDto
import com.example.household_ledger.core.network.dto.ConfirmCandidateDto
import com.example.household_ledger.core.network.dto.UpdateCandidateStatusDto

class CandidateRepository {

    private val api = ApiClient.candidateApi

    suspend fun getCandidates(): List<Candidate> {
        return api.getCandidates().map { it.toDomain() }
    }

    suspend fun confirmCandidate(id: Long, categoryId: Long): Transaction {
        val dto = api.confirmCandidate(id, ConfirmCandidateDto(categoryId))
        return Transaction(
            id = dto.id,
            type = TransactionType.valueOf(dto.type),
            amount = dto.amount,
            merchant = dto.merchant,
            categoryId = dto.categoryId,
            occurredAt = dto.occurredAt,
            note = dto.note,
            source = runCatching { TransactionSource.valueOf(dto.source) }.getOrDefault(TransactionSource.AUTO),
            state = runCatching { TransactionState.valueOf(dto.state) }.getOrDefault(TransactionState.NORMAL)
        )
    }

    suspend fun deleteCandidate(id: Long) {
        api.deleteCandidate(id)
    }

    private fun CandidateDto.toDomain() = Candidate(
        id = id,
        rawEventId = rawEventId,
        type = TransactionType.valueOf(type),
        amount = amount,
        merchant = merchant,
        occurredAt = occurredAt,
        channel = channel,
        status = runCatching { CandidateStatus.valueOf(status) }.getOrDefault(CandidateStatus.CANDIDATE),
        confidence = confidence
    )
}
