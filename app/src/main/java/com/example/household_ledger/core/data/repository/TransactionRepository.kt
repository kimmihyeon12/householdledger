package com.example.household_ledger.core.data.repository

import com.example.household_ledger.core.domain.model.*
import com.example.household_ledger.core.network.ApiClient
import com.example.household_ledger.core.network.dto.CreateTransactionDto
import com.example.household_ledger.core.network.dto.TransactionDto
import com.example.household_ledger.core.network.dto.UpdateTransactionDto

class TransactionRepository {

    private val api = ApiClient.transactionApi

    suspend fun getTransactions(
        year: Int? = null,
        month: Int? = null,
        type: String? = null,
        categoryId: Long? = null
    ): List<Transaction> {
        return api.getTransactions(year, month, type, categoryId).map { it.toDomain() }
    }

    suspend fun getTransaction(id: Long): Transaction {
        return api.getTransaction(id).toDomain()
    }

    suspend fun createTransaction(
        type: TransactionType,
        amount: Long,
        merchant: String,
        categoryId: Long,
        occurredAt: Long,
        note: String = "",
        source: TransactionSource = TransactionSource.MANUAL
    ): Transaction {
        val dto = CreateTransactionDto(
            type = type.name,
            amount = amount,
            merchant = merchant,
            categoryId = categoryId,
            occurredAt = occurredAt,
            note = note,
            source = source.name
        )
        return api.createTransaction(dto).toDomain()
    }

    suspend fun updateTransaction(
        id: Long,
        type: TransactionType? = null,
        amount: Long? = null,
        merchant: String? = null,
        categoryId: Long? = null,
        occurredAt: Long? = null,
        note: String? = null
    ): Transaction {
        val dto = UpdateTransactionDto(
            type = type?.name,
            amount = amount,
            merchant = merchant,
            categoryId = categoryId,
            occurredAt = occurredAt,
            note = note
        )
        return api.updateTransaction(id, dto).toDomain()
    }

    suspend fun deleteTransaction(id: Long) {
        api.deleteTransaction(id)
    }

    private fun TransactionDto.toDomain() = Transaction(
        id = id,
        type = TransactionType.valueOf(type),
        amount = amount,
        merchant = merchant,
        categoryId = categoryId,
        occurredAt = occurredAt,
        note = note,
        source = runCatching { TransactionSource.valueOf(source) }.getOrDefault(TransactionSource.MANUAL),
        state = runCatching { TransactionState.valueOf(state) }.getOrDefault(TransactionState.NORMAL)
    )
}
