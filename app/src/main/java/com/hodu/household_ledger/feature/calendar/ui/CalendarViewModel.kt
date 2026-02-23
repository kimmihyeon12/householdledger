package com.hodu.household_ledger.feature.calendar.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hodu.household_ledger.core.data.repository.StatsRepository
import com.hodu.household_ledger.core.data.repository.TransactionRepository
import com.hodu.household_ledger.core.domain.model.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CalendarViewModel : ViewModel() {

    private val transactionRepo = TransactionRepository()
    private val statsRepo = StatsRepository()

    private val _dailyTotals = MutableStateFlow<Map<Int, Pair<Long, Long>>>(emptyMap())
    val dailyTotals: StateFlow<Map<Int, Pair<Long, Long>>> = _dailyTotals

    private val _selectedDayTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val selectedDayTransactions: StateFlow<List<Transaction>> = _selectedDayTransactions

    private val _monthTransactions = MutableStateFlow<List<Transaction>>(emptyList())
    val monthTransactions: StateFlow<List<Transaction>> = _monthTransactions

    fun loadMonth(year: Int, month: Int) {
        viewModelScope.launch {
            try {
                _dailyTotals.value = statsRepo.getDailyTotals(year, month)
            } catch (_: Exception) {}
            try {
                _monthTransactions.value = transactionRepo.getTransactions(year = year, month = month)
                    .sortedByDescending { it.occurredAt }
            } catch (_: Exception) {}
        }
    }

    fun loadDayTransactions(year: Int, month: Int, day: Int) {
        viewModelScope.launch {
            try {
                val cal = java.util.Calendar.getInstance().apply {
                    set(year, month - 1, day, 0, 0, 0)
                    set(java.util.Calendar.MILLISECOND, 0)
                }
                val dayStart = cal.timeInMillis
                cal.add(java.util.Calendar.DAY_OF_MONTH, 1)
                val dayEnd = cal.timeInMillis
                _selectedDayTransactions.value = _monthTransactions.value
                    .filter { it.occurredAt in dayStart until dayEnd }
            } catch (_: Exception) {}
        }
    }

    fun clearDayTransactions() {
        _selectedDayTransactions.value = emptyList()
    }
}
