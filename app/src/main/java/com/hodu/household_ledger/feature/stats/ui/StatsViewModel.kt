package com.hodu.household_ledger.feature.stats.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hodu.household_ledger.core.common.AppState
import com.hodu.household_ledger.core.data.mapper.CategoryMapper
import com.hodu.household_ledger.core.data.repository.StatsRepository
import com.hodu.household_ledger.core.domain.model.Category
import com.hodu.household_ledger.core.network.dto.CategoryBreakdownDto
import com.hodu.household_ledger.core.network.dto.CategoryDto
import com.hodu.household_ledger.core.network.dto.MonthlySummaryDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class StatsViewModel : ViewModel() {

    private val statsRepo = StatsRepository()

    private val calendar = Calendar.getInstance()
    private val currentYear = calendar.get(Calendar.YEAR)
    private val currentMonth = calendar.get(Calendar.MONTH) + 1

    private val _monthlyExpense = MutableStateFlow(0L)
    val monthlyExpense: StateFlow<Long> = _monthlyExpense

    private val _categoryBreakdown = MutableStateFlow<List<CategoryBreakdownEntry>>(emptyList())
    val categoryBreakdown: StateFlow<List<CategoryBreakdownEntry>> = _categoryBreakdown

    private val _monthlySummaries = MutableStateFlow<List<Pair<String, Long>>>(emptyList())
    val monthlySummaries: StateFlow<List<Pair<String, Long>>> = _monthlySummaries

    init {
        loadStats()
    }

    private fun loadStats() {
        viewModelScope.launch {
            AppState.startLoading()
            var hasError = false
            try {
                val summary = statsRepo.getMonthlySummary(currentYear, currentMonth)
                _monthlyExpense.value = summary.totalExpense
            } catch (e: Exception) {
                hasError = true
            }

            try {
                val breakdown = statsRepo.getCategoryBreakdown(currentYear, currentMonth)
                val totalExpense = breakdown.sumOf { it.total }
                _categoryBreakdown.value = breakdown.map { dto ->
                    val category = CategoryMapper.toDomain(
                        CategoryDto(dto.categoryId, dto.categoryName, "EXPENSE", dto.categoryIcon)
                    )
                    val percentage = if (totalExpense > 0) dto.total.toDouble() / totalExpense * 100 else 0.0
                    CategoryBreakdownEntry(category, dto.total, percentage)
                }
            } catch (e: Exception) {
                hasError = true
            }

            // Load last 6 months
            loadMonthlySummaries()
            AppState.stopLoading()
            if (hasError) {
                AppState.showError(message = "통계 데이터를 불러오지 못했습니다", retry = { loadStats() })
            }
        }
    }

    private suspend fun loadMonthlySummaries() {
        try {
            val summaries = mutableListOf<Pair<String, Long>>()
            val cal = Calendar.getInstance()
            for (i in 5 downTo 0) {
                cal.time = calendar.time
                cal.add(Calendar.MONTH, -i)
                val y = cal.get(Calendar.YEAR)
                val m = cal.get(Calendar.MONTH) + 1
                try {
                    val s = statsRepo.getMonthlySummary(y, m)
                    summaries.add("${m}월" to s.totalExpense)
                } catch (_: Exception) {
                    summaries.add("${m}월" to 0L)
                }
            }
            _monthlySummaries.value = summaries
        } catch (e: Exception) {
            AppState.showError(message = "월별 추이를 불러오지 못했습니다")
        }
    }
}

data class CategoryBreakdownEntry(
    val category: Category,
    val amount: Long,
    val percentage: Double
)
