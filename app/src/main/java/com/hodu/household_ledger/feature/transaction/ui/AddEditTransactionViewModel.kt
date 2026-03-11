package com.hodu.household_ledger.feature.transaction.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hodu.household_ledger.core.common.AppState
import com.hodu.household_ledger.core.data.repository.CategoryRepository
import com.hodu.household_ledger.core.data.repository.TransactionRepository
import com.hodu.household_ledger.core.domain.model.Category
import com.hodu.household_ledger.core.domain.model.Transaction
import com.hodu.household_ledger.core.domain.model.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddEditTransactionViewModel : ViewModel() {

    private val transactionRepo = TransactionRepository()
    private val categoryRepo = CategoryRepository()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _editTransaction = MutableStateFlow<Transaction?>(null)
    val editTransaction: StateFlow<Transaction?> = _editTransaction

    private val _saveSuccess = MutableStateFlow(false)
    val saveSuccess: StateFlow<Boolean> = _saveSuccess

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                _categories.value = categoryRepo.getCategories()
            } catch (e: Exception) {
                AppState.showError(message = "카테고리를 불러오지 못했습니다", retry = { loadCategories() })
            }
        }
    }

    fun loadTransaction(id: Long) {
        viewModelScope.launch {
            try {
                _editTransaction.value = transactionRepo.getTransaction(id)
            } catch (e: Exception) {
                AppState.showError(message = "거래 정보를 불러오지 못했습니다")
            }
        }
    }

    fun saveTransaction(
        type: TransactionType,
        amount: Long,
        merchant: String,
        categoryId: Long,
        occurredAt: Long,
        note: String
    ) {
        viewModelScope.launch {
            try {
                transactionRepo.createTransaction(
                    type = type,
                    amount = amount,
                    merchant = merchant,
                    categoryId = categoryId,
                    occurredAt = occurredAt,
                    note = note
                )
                _saveSuccess.value = true
            } catch (e: Exception) {
                AppState.showError(message = "거래 저장에 실패했습니다")
            }
        }
    }

    fun updateTransaction(
        id: Long,
        type: TransactionType,
        amount: Long,
        merchant: String,
        categoryId: Long,
        note: String
    ) {
        viewModelScope.launch {
            try {
                transactionRepo.updateTransaction(
                    id = id,
                    type = type,
                    amount = amount,
                    merchant = merchant,
                    categoryId = categoryId,
                    note = note
                )
                _saveSuccess.value = true
            } catch (e: Exception) {
                AppState.showError(message = "거래 수정에 실패했습니다")
            }
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            try {
                transactionRepo.deleteTransaction(id)
                _saveSuccess.value = true
            } catch (e: Exception) {
                AppState.showError(message = "거래 삭제에 실패했습니다")
            }
        }
    }

    fun getCategoriesForType(type: TransactionType): List<Category> {
        return _categories.value.filter { it.type == type }
    }

    fun getCategoryById(id: Long): Category? = _categories.value.find { it.id == id }
}
