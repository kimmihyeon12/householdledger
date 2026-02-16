package com.example.household_ledger.feature.home.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.household_ledger.core.data.repository.*
import com.example.household_ledger.core.domain.model.*
import com.example.household_ledger.core.network.ApiClient
import com.example.household_ledger.core.network.dto.AiParseRequestDto
import com.example.household_ledger.core.network.dto.CreateTransactionDto
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

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                launch { loadTransactions() }
                launch { loadCategories() }
                launch { loadCandidateCount() }
                launch { loadBudget() }
                launch { loadMonthlySummary() }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun loadTransactions() {
        try {
            _transactions.value = transactionRepo.getTransactions(
                year = currentYear, month = currentMonth
            ).sortedByDescending { it.occurredAt }
        } catch (_: Exception) {}
    }

    private suspend fun loadCategories() {
        try {
            _categories.value = categoryRepo.getCategories()
        } catch (_: Exception) {}
    }

    private suspend fun loadCandidateCount() {
        try {
            _candidateCount.value = candidateRepo.getCandidates().size
        } catch (_: Exception) {}
    }

    private suspend fun loadBudget() {
        try {
            _budget.value = budgetRepo.getBudget(currentYear, currentMonth)
        } catch (_: Exception) {}
    }

    private suspend fun loadMonthlySummary() {
        try {
            val summary = StatsRepository().getMonthlySummary(currentYear, currentMonth)
            _monthlyIncome.value = summary.totalIncome
            _monthlyExpense.value = summary.totalExpense
        } catch (_: Exception) {}
    }

    fun setBudget(amount: Long) {
        viewModelScope.launch {
            try {
                budgetRepo.setBudget(currentYear, currentMonth, amount)
                _budget.value = amount
            } catch (_: Exception) {}
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
            } catch (_: Exception) {}
        }
    }

    fun getCategoryById(id: Long): Category? = _categories.value.find { it.id == id }
}
