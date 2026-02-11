package com.example.household_ledger.model

data class PointLedger(
    val id: Long,
    val delta: Int,
    val reason: String,
    val createdAt: Long,
    val refId: Long? = null
)
