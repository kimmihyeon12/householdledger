package com.hodu.household_ledger.feature.ai.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hodu.household_ledger.core.domain.model.TransactionType
import com.hodu.household_ledger.core.ui.theme.*
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiInputScreen(
    viewModel: AiInputViewModel = viewModel()
) {
    var inputText by remember { mutableStateOf("") }
    var parsedResult by remember { mutableStateOf<AiParsedResult?>(null) }
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (parsedResult == null && registeredMessages.isEmpty()) {
                GuideSection()
            }

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
                        getCategoryById = { viewModel.getCategoryById(it) },
                        numberFormat = numberFormat,
                        onConfirm = {
                            viewModel.createTransaction(result)
                            val category = viewModel.getCategoryById(result.categoryId)
                            val typeLabel = if (result.type == TransactionType.EXPENSE) "지출" else "수입"
                            registeredMessages = registeredMessages + "[$typeLabel] ${result.merchant} ${numberFormat.format(result.amount)}원 → ${category?.name ?: "기타"} 등록 완료"
                            parsedResult = null
                            inputText = ""
                            showSuccess = true
                        },
                        onCancel = { parsedResult = null }
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
                        Icon(Icons.Filled.CheckCircle, null, tint = ConfirmGreen, modifier = Modifier.size(20.dp))
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
                        viewModel.parseInput(
                            text = inputText,
                            onResult = { result ->
                                parsedResult = result
                            },
                            onFallback = { result ->
                                parsedResult = result
                            }
                        )
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
            Icon(Icons.Filled.AutoAwesome, null, modifier = Modifier.size(36.dp), tint = GoldPrimary)
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
                        Icon(Icons.Outlined.FormatQuote, null, modifier = Modifier.size(16.dp), tint = GoldPrimary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(example, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}

@Composable
private fun ParsedResultCard(
    result: AiParsedResult,
    getCategoryById: (Long) -> com.hodu.household_ledger.core.domain.model.Category?,
    numberFormat: NumberFormat,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    val category = getCategoryById(result.categoryId)
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
                Icon(Icons.Filled.AutoAwesome, null, modifier = Modifier.size(18.dp), tint = GoldPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("AI 분석 결과", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = GoldPrimary)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(8.dp), color = amountColor.copy(alpha = 0.1f)) {
                    Text(typeLabel, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = amountColor)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(result.merchant, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                "${if (isExpense) "-" else "+"}${numberFormat.format(result.amount)}원",
                style = MaterialTheme.typography.headlineSmall.copy(letterSpacing = (-0.5).sp),
                fontWeight = FontWeight.Bold,
                color = amountColor
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (category != null) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(category.color.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(category.icon, null, modifier = Modifier.size(16.dp), tint = category.color)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(category.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    border = ButtonDefaults.outlinedButtonBorder().copy(width = 1.dp)
                ) { Text("취소") }
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = Navy900)
                ) {
                    Icon(Icons.Filled.Check, null, modifier = Modifier.size(18.dp))
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
            Icon(Icons.Filled.CheckCircle, null, tint = ConfirmGreen, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
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
