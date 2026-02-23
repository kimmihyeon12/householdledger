package com.hodu.household_ledger.core.network.dto

data class AiParseRequestDto(
    val text: String
)

data class AiParseResponseDto(
    val type: String,
    val amount: Long,
    val merchant: String,
    val suggestedCategoryId: Long,
    val suggestedCategoryName: String,
    val confidence: Float,
    val note: String = ""
)
