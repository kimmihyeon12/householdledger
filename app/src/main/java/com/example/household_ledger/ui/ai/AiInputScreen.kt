package com.example.household_ledger.ui.ai

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.household_ledger.data.mock.MockData
import com.example.household_ledger.model.TransactionType
import com.example.household_ledger.ui.theme.*
import java.text.NumberFormat
import java.util.*

private data class ParsedResult(
    val type: TransactionType,
    val amount: Long,
    val merchant: String,
    val categoryId: Long,
    val note: String = ""
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiInputScreen() {
    var inputText by remember { mutableStateOf("") }
    var parsedResult by remember { mutableStateOf<ParsedResult?>(null) }
    var registeredMessages by remember { mutableStateOf(listOf<String>()) }
    var showSuccess by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val numberFormat = NumberFormat.getNumberInstance(Locale.KOREA)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "AI 입력",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Guide section
            if (parsedResult == null && registeredMessages.isEmpty()) {
                GuideSection()
            }

            // Registered history
            if (registeredMessages.isNotEmpty()) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(registeredMessages) { msg ->
                        RegisteredMessageBubble(msg)
                    }
                }
            } else if (parsedResult != null) {
                Spacer(modifier = Modifier.weight(1f))
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            // Parsed result preview
            AnimatedVisibility(
                visible = parsedResult != null,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                parsedResult?.let { result ->
                    ParsedResultCard(
                        result = result,
                        numberFormat = numberFormat,
                        onConfirm = {
                            val category = MockData.getCategoryById(result.categoryId)
                            val typeLabel = if (result.type == TransactionType.EXPENSE) "지출" else "수입"
                            registeredMessages = registeredMessages + "[$typeLabel] ${result.merchant} ${numberFormat.format(result.amount)}원 → ${category?.name ?: "기타"} 등록 완료"
                            parsedResult = null
                            inputText = ""
                            showSuccess = true
                        },
                        onCancel = {
                            parsedResult = null
                        }
                    )
                }
            }

            // Success snackbar
            AnimatedVisibility(
                visible = showSuccess,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(2000)
                    showSuccess = false
                }
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = ConfirmSoft
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            null,
                            tint = ConfirmGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "거래가 등록되었습니다",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = ConfirmGreen
                        )
                    }
                }
            }

            // Input bar
            InputBar(
                text = inputText,
                onTextChange = { inputText = it },
                onSubmit = {
                    if (inputText.isNotBlank()) {
                        parsedResult = parseInput(inputText)
                        focusManager.clearFocus()
                    }
                }
            )
        }
    }
}

@Composable
private fun GuideSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(GoldLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Filled.AutoAwesome,
                null,
                modifier = Modifier.size(36.dp),
                tint = GoldPrimary
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            "자연어로 거래를 입력하세요",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            "금액과 내용을 입력하면\nAI가 자동으로 분류하여 등록합니다",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Example chips
        val examples = listOf(
            "스타벅스 아메리카노 4500원",
            "택시비 15000원",
            "월급 320만원",
            "넷플릭스 17000원",
            "점심 김치찌개 9000원"
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                "예시",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 4.dp)
            )
            examples.forEach { example ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.FormatQuote,
                            null,
                            modifier = Modifier.size(16.dp),
                            tint = GoldPrimary
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            example,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ParsedResultCard(
    result: ParsedResult,
    numberFormat: NumberFormat,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    val category = MockData.getCategoryById(result.categoryId)
    val isExpense = result.type == TransactionType.EXPENSE
    val amountColor = if (isExpense) ExpenseRed else IncomeBlue
    val typeLabel = if (isExpense) "지출" else "수입"

    OutlinedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            width = 1.5.dp,
            brush = androidx.compose.ui.graphics.SolidColor(GoldPrimary.copy(alpha = 0.4f))
        )
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.AutoAwesome,
                    null,
                    modifier = Modifier.size(18.dp),
                    tint = GoldPrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    "AI 분석 결과",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = GoldPrimary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Type badge + merchant
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = amountColor.copy(alpha = 0.1f)
                ) {
                    Text(
                        typeLabel,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = amountColor
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    result.merchant,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Amount
            Text(
                "${if (isExpense) "-" else "+"}${numberFormat.format(result.amount)}원",
                style = MaterialTheme.typography.headlineSmall.copy(
                    letterSpacing = (-0.5).sp
                ),
                fontWeight = FontWeight.Bold,
                color = amountColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (category != null) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(category.color.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            category.icon,
                            null,
                            modifier = Modifier.size(16.dp),
                            tint = category.color
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        category.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = ButtonDefaults.outlinedButtonBorder().copy(
                        width = 1.dp
                    )
                ) {
                    Text("취소")
                }
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = Navy900
                    )
                ) {
                    Icon(
                        Icons.Filled.Check,
                        null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("등록", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun RegisteredMessageBubble(message: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.CheckCircle,
                null,
                tint = ConfirmGreen,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun InputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSubmit: () -> Unit
) {
    Surface(
        tonalElevation = 0.dp,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = {
                    Text(
                        "예: 스타벅스 아메리카노 4500원",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                },
                modifier = Modifier.weight(1f),
                singleLine = true,
                shape = RoundedCornerShape(16.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = { onSubmit() }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoldPrimary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )
            )
            Spacer(modifier = Modifier.width(10.dp))
            FilledIconButton(
                onClick = onSubmit,
                enabled = text.isNotBlank(),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = GoldPrimary,
                    contentColor = Navy900,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                ),
                modifier = Modifier.size(48.dp)
            ) {
                Icon(Icons.Filled.Send, contentDescription = "전송", modifier = Modifier.size(20.dp))
            }
        }
    }
}

/**
 * Simple mock parser that extracts amount and merchant from natural language input.
 * In production, this would call an AI API.
 */
private fun parseInput(input: String): ParsedResult {
    val text = input.trim()

    // Extract amount: "155만원", "4500원", "3만5천원", "320만원" etc.
    val amount = extractAmount(text)

    // Remove amount part to get merchant/description
    val amountPatterns = listOf(
        Regex("""\d{1,3}만\s?\d{1,4}천?\s?원?"""),
        Regex("""\d{1,4}만\s?원?"""),
        Regex("""\d{1,3}(,\d{3})+\s?원?"""),
        Regex("""\d+\s?원""")
    )
    var remaining = text
    for (pattern in amountPatterns) {
        remaining = pattern.replace(remaining, "").trim()
    }

    // Determine type - income keywords
    val incomeKeywords = listOf("월급", "급여", "수입", "용돈", "입금", "보너스", "상여", "환급")
    val isIncome = incomeKeywords.any { remaining.contains(it) || text.contains(it) }
    val type = if (isIncome) TransactionType.INCOME else TransactionType.EXPENSE

    // Clean up merchant name
    val merchant = remaining
        .replace(Regex("""[,.\s]+$"""), "")
        .replace(Regex("""^[,.\s]+"""), "")
        .ifBlank { if (isIncome) "수입" else "지출" }

    // Category matching
    val categoryId = matchCategory(merchant, text, type)

    return ParsedResult(
        type = type,
        amount = amount,
        merchant = merchant,
        categoryId = categoryId
    )
}

private fun extractAmount(text: String): Long {
    // "155만원", "3만5천원", "320만원"
    val manCheonPattern = Regex("""(\d{1,4})만\s?(\d{1,4})천""")
    manCheonPattern.find(text)?.let {
        val man = it.groupValues[1].toLong()
        val cheon = it.groupValues[2].toLong()
        return man * 10000 + cheon * 1000
    }

    // "155만원", "320만원"
    val manPattern = Regex("""(\d{1,4})만""")
    manPattern.find(text)?.let {
        return it.groupValues[1].toLong() * 10000
    }

    // "1,550,000원", "4,500원"
    val commaPattern = Regex("""(\d{1,3}(?:,\d{3})+)\s?원?""")
    commaPattern.find(text)?.let {
        return it.groupValues[1].replace(",", "").toLong()
    }

    // "4500원", "15000원"
    val numPattern = Regex("""(\d{3,})\s?원""")
    numPattern.find(text)?.let {
        return it.groupValues[1].toLong()
    }

    // Fallback: any number
    val anyNum = Regex("""(\d+)""")
    anyNum.find(text)?.let {
        return it.groupValues[1].toLong()
    }

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
        else -> 8L // 기타
    }
}
