package com.example.household_ledger.model

data class Candidate(
    val id: Long,
    val rawEventId: Long,
    val type: TransactionType,
    val amount: Long,
    val merchant: String,
    val occurredAt: Long,
    val channel: String,
    val status: CandidateStatus = CandidateStatus.CANDIDATE,
    val confidence: Float = 0.8f
)

enum class CandidateStatus { CANDIDATE, CANCEL_CANDIDATE, IGNORED }
