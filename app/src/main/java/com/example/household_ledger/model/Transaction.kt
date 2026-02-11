package com.example.household_ledger.model

data class Transaction(
    val id: Long,
    val type: TransactionType,
    val amount: Long,
    val merchant: String,
    val categoryId: Long,
    val occurredAt: Long,
    val note: String = "",
    val source: TransactionSource = TransactionSource.MANUAL,
    val state: TransactionState = TransactionState.NORMAL
)

enum class TransactionType { INCOME, EXPENSE }
enum class TransactionSource { AUTO, MANUAL }
enum class TransactionState { NORMAL, CANCELED }
