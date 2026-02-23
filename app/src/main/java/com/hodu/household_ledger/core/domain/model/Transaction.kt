package com.hodu.household_ledger.core.domain.model

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

enum class TransactionType {
    INCOME, EXPENSE
}

enum class TransactionSource {
    MANUAL, AUTO, SMS, NOTIFICATION
}

enum class TransactionState {
    NORMAL, CANCELLED, DELETED
}
