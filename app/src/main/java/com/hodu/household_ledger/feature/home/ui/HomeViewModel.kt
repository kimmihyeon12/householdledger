package com.hodu.household_ledger.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hodu.household_ledger.core.common.AppState
import com.hodu.household_ledger.core.data.repository.*
import com.hodu.household_ledger.core.domain.model.*
import com.hodu.household_ledger.core.network.ApiClient
import com.hodu.household_ledger.core.network.dto.AiParseRequestDto
import com.hodu.household_ledger.core.network.dto.CreateTransactionDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeViewModel : ViewModel() {

    private val transactionRepo = TransactionRepository()
    private val categoryRepo = CategoryRepository()
    private val candidateRepo = CandidateRepository()
    private val budgetRepo = BudgetRepository()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    private val _candidateCount = MutableStateFlow(0)
    val candidateCount: StateFlow<Int> = _candidateCount

    private val _monthlyIncome = MutableStateFlow(0L)
    val monthlyIncome: StateFlow<Long> = _monthlyIncome

    private val _monthlyExpense = MutableStateFlow(0L)
    val monthlyExpense: StateFlow<Long> = _monthlyExpense

    private val _budget = MutableStateFlow(0L)
    val budget: StateFlow<Long> = _budget

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val currentMonth = calendar.get(Calendar.MONTH) + 1

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            AppState.startLoading()
            var hasError = false
            try {
                kotlinx.coroutines.coroutineScope {
                    launch { runCatching { loadTransactions() }.onFailure { hasError = true } }
                    launch { runCatching { loadCategories() }.onFailure { hasError = true } }
                    launch { runCatching { loadCandidateCount() }.onFailure { hasError = true } }
                    launch { runCatching { loadBudget() }.onFailure { hasError = true } }
                    launch { runCatching { loadMonthlySummary() }.onFailure { hasError = true } }
                }
            } finally {
                _isLoading.value = false
                AppState.stopLoading()
            }
            if (hasError) {
                AppState.showError(message = "데이터를 불러오지 못했습니다", retry = { loadData() })
            }
        }
    }

    private suspend fun loadTransactions() {
        _transactions.value = transactionRepo.getTransactions(
            year = currentYear, month = currentMonth
        ).sortedByDescending { it.occurredAt }
    }

    private suspend fun loadCategories() {
        _categories.value = categoryRepo.getCategories()
    }

    private suspend fun loadCandidateCount() {
        _candidateCount.value = candidateRepo.getCandidates().size
    }

    private suspend fun loadBudget() {
        _budget.value = budgetRepo.getBudget(currentYear, currentMonth)
    }

    private suspend fun loadMonthlySummary() {
        val summary = StatsRepository().getMonthlySummary(currentYear, currentMonth)
        _monthlyIncome.value = summary.totalIncome
        _monthlyExpense.value = summary.totalExpense
    }

    fun setBudget(amount: Long) {
        viewModelScope.launch {
            try {
                budgetRepo.setBudget(currentYear, currentMonth, amount)
                _budget.value = amount
            } catch (e: Exception) {
                AppState.showError(message = "예산 설정에 실패했습니다", retry = { setBudget(amount) })
            }
        }
    }

    fun createTransaction(
        type: TransactionType,
        amount: Long,
        merchant: String,
        categoryId: Long,
        note: String = ""
    ) {
        viewModelScope.launch {
            try {
                transactionRepo.createTransaction(
                    type = type,
                    amount = amount,
                    merchant = merchant,
                    categoryId = categoryId,
                    occurredAt = System.currentTimeMillis(),
                    note = note
                )
                loadData()
            } catch (e: Exception) {
                AppState.showError(message = "거래 등록에 실패했습니다")
            }
        }
    }

    fun getCategoryById(id: Long): Category? = _categories.value.find { it.id == id }
}
