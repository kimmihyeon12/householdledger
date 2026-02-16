package com.example.household_ledger.core.network.dto

data class TransactionDto(
    val id: Long,
    val type: String,
    val amount: Long,
    val merchant: String,
    val categoryId: Long,
    val category: CategoryDto? = null,
    val occurredAt: Long,
    val note: String = "",
    val source: String = "MANUAL",
    val state: String = "NORMAL"
)

data class CreateTransactionDto(
    val type: String,
    val amount: Long,
    val merchant: String,
    val categoryId: Long,
    val occurredAt: Long,
    val note: String = "",
    val source: String = "MANUAL",
    val state: String = "NORMAL"
)

data class UpdateTransactionDto(
    val type: String? = null,
    val amount: Long? = null,
    val merchant: String? = null,
    val categoryId: Long? = null,
    val occurredAt: Long? = null,
    val note: String? = null,
    val source: String? = null,
    val state: String? = null
)
