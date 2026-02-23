package com.hodu.household_ledger.core.network.dto

data class CandidateDto(
    val id: Long,
    val rawEventId: Long,
    val type: String,
    val amount: Long,
    val merchant: String,
    val occurredAt: Long,
    val channel: String,
    val status: String = "CANDIDATE",
    val confidence: Float = 0.8f
)

data class ConfirmCandidateDto(
    val categoryId: Long
)

data class UpdateCandidateStatusDto(
    val status: String
)
