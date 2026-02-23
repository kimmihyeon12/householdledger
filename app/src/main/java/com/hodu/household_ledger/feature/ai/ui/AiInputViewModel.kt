package com.hodu.household_ledger.feature.ai.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hodu.household_ledger.core.data.repository.CategoryRepository
import com.hodu.household_ledger.core.data.repository.TransactionRepository
import com.hodu.household_ledger.core.domain.model.Category
import com.hodu.household_ledger.core.domain.model.TransactionType
import com.hodu.household_ledger.core.network.ApiClient
import com.hodu.household_ledger.core.network.dto.AiParseRequestDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class AiParsedResult(
    val type: TransactionType,
    val amount: Long,
    val merchant: String,
    val categoryId: Long,
    val note: String = "",
    val confidence: Float = 0f
)

class AiInputViewModel : ViewModel() {

    private val categoryRepo = CategoryRepository()
    private val transactionRepo = TransactionRepository()
    private val aiApi = ApiClient.aiApi

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories

    init {
        viewModelScope.launch {
            try {
                _categories.value = categoryRepo.getCategories()
            } catch (_: Exception) {}
        }
    }

    fun parseInput(
        text: String,
        onResult: (AiParsedResult) -> Unit,
        onFallback: (AiParsedResult) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = aiApi.parseTransaction(AiParseRequestDto(text))
                onResult(
                    AiParsedResult(
                        type = TransactionType.valueOf(response.type),
                        amount = response.amount,
                        merchant = response.merchant,
                        categoryId = response.suggestedCategoryId,
                        note = response.note,
                        confidence = response.confidence
                    )
                )
            } catch (_: Exception) {
                // Fallback to local parsing
                onFallback(localParse(text))
            }
        }
    }

    fun createTransaction(result: AiParsedResult) {
        viewModelScope.launch {
            try {
                transactionRepo.createTransaction(
                    type = result.type,
                    amount = result.amount,
                    merchant = result.merchant,
                    categoryId = result.categoryId,
                    occurredAt = System.currentTimeMillis(),
                    note = result.note
                )
            } catch (_: Exception) {}
        }
    }

    fun getCategoryById(id: Long): Category? = _categories.value.find { it.id == id }

    private fun localParse(input: String): AiParsedResult {
        val text = input.trim()
        val amount = extractAmount(text)

        val amountPatterns = listOf(
            Regex("""\d{1,3}만\s?\d{1,4}천?\s?원?"""),
            Regex("""\d{1,4}만\s?원?"""),
            Regex("""\d{1,4}천\s?원?"""),
            Regex("""\d{1,3}(,\d{3})+\s?원?"""),
            Regex("""\d+\s?원""")
        )
        var remaining = text
        for (pattern in amountPatterns) {
            remaining = pattern.replace(remaining, "").trim()
        }

        val incomeKeywords = listOf("월급", "급여", "수입", "용돈", "입금", "보너스", "상여", "환급")
        val isIncome = incomeKeywords.any { remaining.contains(it) || text.contains(it) }
        val type = if (isIncome) TransactionType.INCOME else TransactionType.EXPENSE

        val merchant = remaining
            .replace(Regex("""[,.\s]+$"""), "")
            .replace(Regex("""^[,.\s]+"""), "")
            .ifBlank { if (isIncome) "수입" else "지출" }

        val categoryId = matchCategory(merchant, text, type)

        return AiParsedResult(type = type, amount = amount, merchant = merchant, categoryId = categoryId)
    }

    private fun extractAmount(text: String): Long {
        val manCheonPattern = Regex("""(\d{1,4})만\s?(\d{1,4})천""")
        manCheonPattern.find(text)?.let {
            return it.groupValues[1].toLong() * 10000 + it.groupValues[2].toLong() * 1000
        }
        val manPattern = Regex("""(\d{1,4})만""")
        manPattern.find(text)?.let { return it.groupValues[1].toLong() * 10000 }
        val cheonPattern = Regex("""(\d{1,4})천""")
        cheonPattern.find(text)?.let { return it.groupValues[1].toLong() * 1000 }
        val commaPattern = Regex("""(\d{1,3}(?:,\d{3})+)\s?원?""")
        commaPattern.find(text)?.let { return it.groupValues[1].replace(",", "").toLong() }
        val numWonPattern = Regex("""(\d+)\s?원""")
        numWonPattern.find(text)?.let { return it.groupValues[1].toLong() }
        val anyNum = Regex("""(\d+)""")
        anyNum.find(text)?.let { return it.groupValues[1].toLong() }
        return 0L
    }

    private fun matchCategory(merchant: String, fullText: String, type: TransactionType): Long {
        if (type == TransactionType.INCOME) {
            return if (fullText.contains("급여") || fullText.contains("월급")) 9L else 10L
        }
        val combined = "$merchant $fullText"
        val foodKeywords = listOf("스타벅스", "커피", "카페", "맥도날드", "치킨", "피자", "식사", "점심", "저녁", "아침", "밥", "국", "찌개", "배달", "음식", "빵", "라떼", "아메리카노", "삼겹살", "편의점", "GS25", "CU", "세븐", "이마트")
        val transportKeywords = listOf("택시", "버스", "지하철", "교통", "주유", "기차", "KTX", "톨게이트")
        val shoppingKeywords = listOf("쿠팡", "무신사", "자라", "올리브영", "쇼핑", "구매", "아이폰", "갤럭시", "노트북", "에어팟")
        val entertainmentKeywords = listOf("영화", "넷플릭스", "게임", "CGV", "유튜브", "디즈니", "공연", "콘서트")
        val healthKeywords = listOf("병원", "약국", "약", "진료", "치과", "한의원", "헬스", "운동")
        val educationKeywords = listOf("학원", "교육", "강의", "책", "교보", "인강", "수업료", "등록금")
        val livingKeywords = listOf("관리비", "전기", "수도", "가스", "월세", "보험", "통신", "인터넷")
        return when {
            foodKeywords.any { combined.contains(it) } -> 1L
            transportKeywords.any { combined.contains(it) } -> 2L
            shoppingKeywords.any { combined.contains(it) } -> 3L
            entertainmentKeywords.any { combined.contains(it) } -> 4L
            healthKeywords.any { combined.contains(it) } -> 5L
            educationKeywords.any { combined.contains(it) } -> 6L
            livingKeywords.any { combined.contains(it) } -> 7L
            else -> 8L
        }
    }
}
