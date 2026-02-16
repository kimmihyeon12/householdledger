package com.example.household_ledger.feature.home.ui

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.example.household_ledger.core.ui.theme.LocalIsDark
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.household_ledger.core.domain.model.TransactionState
import com.example.household_ledger.core.domain.model.TransactionType
import com.example.household_ledger.core.ui.component.TransactionItem
import com.example.household_ledger.core.ui.theme.*
import kotlinx.coroutines.delay
import java.text.NumberFormat
import java.util.*

private data class ParsedResult(
    val type: TransactionType,
    val amount: Long,
    val merchant: String,
    val categoryId: Long,
    val note: String = ""
)

private sealed class ParseError {
    data object NoAmount : ParseError()
    data object NoMerchant : ParseError()
    data object EmptyInput : ParseError()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToInbox: () -> Unit,
    onNavigateToAddTransaction: () -> Unit,
    onNavigateToEditTransaction: (Long) -> Unit = {},
    onNavigateToStats: () -> Unit = {},
    viewModel: HomeViewModel = viewModel()
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale.KOREA)
    val isDark = LocalIsDark.current
    val focusManager = LocalFocusManager.current

    val transactions by viewModel.transactions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val candidateCount by viewModel.candidateCount.collectAsState()
    val monthlyIncome by viewModel.monthlyIncome.collectAsState()
    val monthlyExpense by viewModel.monthlyExpense.collectAsState()
    val budget by viewModel.budget.collectAsState()

    var showBudgetDialog by remember { mutableStateOf(false) }
    var aiInputText by remember { mutableStateOf("") }
    var showSheet by remember { mutableStateOf(false) }
    var parsedResult by remember { mutableStateOf<ParsedResult?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showSuccess by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Error auto-dismiss
    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            delay(3000)
            errorMessage = null
        }
    }

    // Success auto-dismiss
    LaunchedEffect(showSuccess) {
        if (showSuccess) {
            delay(2500)
            showSuccess = false
        }
    }

    Scaffold(
        containerColor = if (isDark) Navy900 else SurfaceLight,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "콩돈",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = if (isDark) Navy50 else Navy900
                ),
                actions = {
                    IconButton(onClick = onNavigateToStats) {
                        Icon(
                            Icons.Outlined.BarChart,
                            contentDescription = "통계",
                            tint = if (isDark) Navy400 else Navy600
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddTransaction,
                containerColor = GoldPrimary,
                contentColor = Color.White,
                shape = RoundedCornerShape(20.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "거래 추가")
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                // Monthly summary card
                item {
                    var showTransactionList by remember { mutableStateOf<TransactionType?>(null) }

                    MonthlySummaryCard(
                        income = monthlyIncome,
                        expense = monthlyExpense,
                        budget = budget,
                        numberFormat = numberFormat,
                        onIncomeClick = { showTransactionList = TransactionType.INCOME },
                        onExpenseClick = { showTransactionList = TransactionType.EXPENSE },
                        onBudgetClick = { showBudgetDialog = true }
                    )

                    if (showTransactionList != null) {
                        val filterType = showTransactionList!!
                        val filteredTransactions = transactions
                            .filter { it.type == filterType && it.state == TransactionState.NORMAL }
                            .sortedByDescending { it.occurredAt }
                        val isExpense = filterType == TransactionType.EXPENSE
                        val totalAmount = filteredTransactions.sumOf { it.amount }

                        ModalBottomSheet(
                            onDismissRequest = { showTransactionList = null },
                            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                            containerColor = Color.White,
                            dragHandle = {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Box(
                                        modifier = Modifier
                                            .width(40.dp)
                                            .height(4.dp)
                                            .clip(RoundedCornerShape(2.dp))
                                            .background(Navy400.copy(alpha = 0.3f))
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp)
                                    .padding(bottom = 32.dp)
                            ) {
                                // Header
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            if (isExpense) Icons.Outlined.TrendingDown else Icons.Outlined.TrendingUp,
                                            null,
                                            modifier = Modifier.size(20.dp),
                                            tint = if (isExpense) ExpenseRed else IncomeBlue
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "${viewModel.currentMonth}월 ${if (isExpense) "지출" else "수입"} 내역",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = (if (isExpense) ExpenseRed else IncomeBlue).copy(alpha = 0.1f)
                                    ) {
                                        Text(
                                            "${filteredTransactions.size}건",
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isExpense) ExpenseRed else IncomeBlue
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Total
                                Text(
                                    "${if (isExpense) "-" else "+"}${numberFormat.format(totalAmount)}원",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isExpense) ExpenseRed else IncomeBlue,
                                    modifier = Modifier.padding(start = 28.dp)
                                )

                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(color = Navy200.copy(alpha = 0.5f))

                                // Transaction list
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 400.dp)
                                ) {
                                    items(filteredTransactions) { transaction ->
                                        TransactionItem(
                                            transaction = transaction,
                                            category = viewModel.getCategoryById(transaction.categoryId),
                                            onClick = {
                                                showTransactionList = null
                                                onNavigateToEditTransaction(transaction.id)
                                            }
                                        )
                                        if (transaction != filteredTransactions.last()) {
                                            HorizontalDivider(
                                                modifier = Modifier.padding(horizontal = 20.dp),
                                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // AI Input bar
                item {
                    AiInputBar(
                        text = aiInputText,
                        onTextChange = { aiInputText = it },
                        onSubmit = {
                            val result = tryParse(aiInputText)
                            if (result != null) {
                                parsedResult = result
                                showSheet = true
                                focusManager.clearFocus()
                            }
                        },
                        onError = { error ->
                            errorMessage = error
                            focusManager.clearFocus()
                        },
                        isDark = isDark
                    )
                }

                // Inbox banner
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    InboxBanner(
                        count = candidateCount,
                        onClick = onNavigateToInbox,
                        isDark = isDark
                    )
                }

                // Recent transactions header
                item {
                    Text(
                        text = "최근 거래",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            letterSpacing = 1.5.sp
                        ),
                        color = if (isDark) Navy400 else Navy600,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)
                    )
                }

                // Transaction list
                items(transactions) { transaction ->
                    TransactionItem(
                        transaction = transaction,
                        category = viewModel.getCategoryById(transaction.categoryId),
                        onClick = { onNavigateToEditTransaction(transaction.id) }
                    )
                    if (transaction != transactions.lastOrNull()) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            color = if (isDark) Navy700.copy(alpha = 0.5f)
                            else Navy200.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            // Error message
            AnimatedVisibility(
                visible = errorMessage != null,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(padding)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = ExpenseSoft
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Outlined.ErrorOutline,
                            null,
                            tint = ExpenseRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            errorMessage ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = ExpenseRed
                        )
                    }
                }
            }

            // Success message
            AnimatedVisibility(
                visible = showSuccess,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(padding)
            ) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
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
        }
    }

    // Budget edit dialog
    if (showBudgetDialog) {
        BudgetEditDialog(
            currentBudget = budget,
            onConfirm = { newBudget ->
                viewModel.setBudget(newBudget)
                showBudgetDialog = false
            },
            onDismiss = { showBudgetDialog = false }
        )
    }

    // Bottom sheet modal for parsed result
    if (showSheet && parsedResult != null) {
        ModalBottomSheet(
            onDismissRequest = {
                showSheet = false
                parsedResult = null
            },
            sheetState = sheetState,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
            containerColor = Color.White,
            dragHandle = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Navy400.copy(alpha = 0.3f))
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        ) {
            EditableResultSheet(
                initialResult = parsedResult!!,
                categories = categories,
                getCategoryById = { viewModel.getCategoryById(it) },
                numberFormat = numberFormat,
                onConfirm = { finalResult ->
                    viewModel.createTransaction(
                        type = finalResult.type,
                        amount = finalResult.amount,
                        merchant = finalResult.merchant,
                        categoryId = finalResult.categoryId,
                        note = finalResult.note
                    )
                    showSheet = false
                    parsedResult = null
                    aiInputText = ""
                    showSuccess = true
                },
                onCancel = {
                    showSheet = false
                    parsedResult = null
                }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EditableResultSheet(
    initialResult: ParsedResult,
    categories: List<com.example.household_ledger.core.domain.model.Category>,
    getCategoryById: (Long) -> com.example.household_ledger.core.domain.model.Category?,
    numberFormat: NumberFormat,
    onConfirm: (ParsedResult) -> Unit,
    onCancel: () -> Unit
) {
    var selectedType by remember { mutableStateOf(initialResult.type) }
    var amount by remember { mutableStateOf(initialResult.amount.toString()) }
    var merchant by remember { mutableStateOf(initialResult.merchant) }
    var selectedCategoryId by remember { mutableLongStateOf(initialResult.categoryId) }

    val filteredCategories = categories.filter { it.type == selectedType }
    val category = getCategoryById(selectedCategoryId)
    val isExpense = selectedType == TransactionType.EXPENSE
    val amountColor = if (isExpense) ExpenseRed else IncomeBlue

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 32.dp)
    ) {
        // Header
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.AutoAwesome,
                null,
                modifier = Modifier.size(20.dp),
                tint = GoldPrimary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "AI 분석 결과",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.weight(1f))
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = amountColor.copy(alpha = 0.1f)
            ) {
                Text(
                    if (isExpense) "지출" else "수입",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = amountColor
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Type selector
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            listOf(TransactionType.EXPENSE to "지출", TransactionType.INCOME to "수입").forEach { (type, label) ->
                val selected = selectedType == type
                val color = if (type == TransactionType.EXPENSE) ExpenseRed else IncomeBlue
                FilterChip(
                    selected = selected,
                    onClick = {
                        selectedType = type
                        val firstCat = categories.firstOrNull { it.type == type }
                        selectedCategoryId = firstCat?.id ?: selectedCategoryId
                    },
                    label = { Text(label, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
                    leadingIcon = {
                        Icon(
                            if (type == TransactionType.EXPENSE) Icons.Outlined.TrendingDown
                            else Icons.Outlined.TrendingUp,
                            null,
                            modifier = Modifier.size(16.dp),
                            tint = if (selected) color else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = color.copy(alpha = 0.1f),
                        selectedLabelColor = color
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Merchant
        OutlinedTextField(
            value = merchant,
            onValueChange = { merchant = it },
            label = { Text("가맹점 / 내용") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            leadingIcon = {
                Icon(
                    Icons.Outlined.Storefront,
                    null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Amount
        OutlinedTextField(
            value = amount,
            onValueChange = { newValue ->
                if (newValue.all { it.isDigit() }) amount = newValue
            },
            label = { Text("금액") },
            suffix = { Text("원", color = MaterialTheme.colorScheme.onSurfaceVariant) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            shape = RoundedCornerShape(14.dp),
            leadingIcon = {
                Icon(
                    Icons.Outlined.Payments,
                    null,
                    modifier = Modifier.size(20.dp),
                    tint = amountColor
                )
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Category
        Text(
            "카테고리",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filteredCategories.forEach { cat ->
                val selected = selectedCategoryId == cat.id
                FilterChip(
                    selected = selected,
                    onClick = { selectedCategoryId = cat.id },
                    label = { Text(cat.name) },
                    leadingIcon = {
                        Icon(
                            cat.icon,
                            null,
                            modifier = Modifier.size(16.dp),
                            tint = if (selected) cat.color else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    shape = RoundedCornerShape(10.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text("취소", fontWeight = FontWeight.Medium)
            }
            Button(
                onClick = {
                    val finalAmount = amount.toLongOrNull() ?: 0L
                    if (finalAmount > 0 && merchant.isNotBlank()) {
                        onConfirm(
                            ParsedResult(
                                type = selectedType,
                                amount = finalAmount,
                                merchant = merchant,
                                categoryId = selectedCategoryId
                            )
                        )
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                enabled = amount.toLongOrNull() != null && amount.toLongOrNull()!! > 0 && merchant.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor = Color.White
                )
            ) {
                Icon(Icons.Filled.Check, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("등록", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AiInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSubmit: () -> Unit,
    onError: (String) -> Unit,
    isDark: Boolean
) {
    val focusManager = LocalFocusManager.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        shape = RoundedCornerShape(20.dp),
        color = if (isDark) Navy800 else Color.White,
        border = ButtonDefaults.outlinedButtonBorder().copy(
            width = 1.dp,
            brush = androidx.compose.ui.graphics.SolidColor(
                if (isDark) Navy600.copy(alpha = 0.4f) else Navy200.copy(alpha = 0.8f)
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Filled.AutoAwesome,
                null,
                modifier = Modifier
                    .padding(start = 10.dp)
                    .size(20.dp),
                tint = GoldPrimary
            )
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                placeholder = {
                    Text(
                        "AI 입력: 스타벅스 4500원",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f)
                    )
                },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        val trimmed = text.trim()
                        if (trimmed.isBlank()) {
                            onError("내용을 입력해주세요")
                        } else if (extractAmount(trimmed) == 0L) {
                            onError("금액을 입력할 수 없습니다 (예: 4500원, 3만원)")
                        } else {
                            onSubmit()
                        }
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
            FilledIconButton(
                onClick = {
                    val trimmed = text.trim()
                    if (trimmed.isBlank()) {
                        onError("내용을 입력해주세요")
                    } else if (extractAmount(trimmed) == 0L) {
                        onError("금액을 입력할 수 없습니다 (예: 4500원, 3만원)")
                    } else {
                        onSubmit()
                    }
                },
                enabled = text.isNotBlank(),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = GoldPrimary,
                    contentColor = Color.White,
                    disabledContainerColor = if (isDark) Navy700 else Navy100,
                    disabledContentColor = Navy400
                ),
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    Icons.Outlined.ArrowUpward,
                    contentDescription = "전송",
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

private fun tryParse(input: String): ParsedResult? {
    val text = input.trim()
    val amount = extractAmount(text)
    if (amount == 0L) return null

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

    return ParsedResult(type = type, amount = amount, merchant = merchant, categoryId = categoryId)
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

@Composable
private fun MonthlySummaryCard(
    income: Long,
    expense: Long,
    budget: Long,
    numberFormat: NumberFormat,
    onIncomeClick: () -> Unit = {},
    onExpenseClick: () -> Unit = {},
    onBudgetClick: () -> Unit = {}
) {
    val balance = budget - expense
    val hasBudget = budget > 0L
    val gradientBrush = Brush.linearGradient(
        colors = listOf(AccentGradientStart, AccentGradientEnd, Navy900),
        start = Offset(0f, 0f),
        end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
    )
    val cardShape = RoundedCornerShape(28.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .clip(cardShape)
            .background(gradientBrush)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Outlined.CalendarMonth, null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "${Calendar.getInstance().get(Calendar.MONTH) + 1}월 요약",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Medium, letterSpacing = 0.5.sp
                        ),
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
                // Budget chip
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable(onClick = onBudgetClick),
                    shape = RoundedCornerShape(10.dp),
                    color = Color.White.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Add, null, Modifier.size(14.dp), tint = Color.White.copy(alpha = 0.7f))
                        Spacer(modifier = Modifier.width(3.dp))
                        if (hasBudget) {
                            val budgetLabel = when {
                                budget >= 100_000_000L -> "${numberFormat.format(budget / 100_000_000)}억"
                                budget >= 10_000L -> "${numberFormat.format(budget / 10_000)}만"
                                budget >= 1_000L -> "${numberFormat.format(budget / 1_000)}천"
                                else -> "${numberFormat.format(budget)}원"
                            }
                            Text(
                                "예산 $budgetLabel",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        } else {
                            Text(
                                "예산",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Medium,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onIncomeClick)
                        .padding(vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(20.dp).clip(RoundedCornerShape(6.dp))
                                .background(IncomeBlue.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.TrendingUp, null, Modifier.size(12.dp), tint = Color(0xFF93C5FD))
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("수입", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Outlined.ChevronRight, null, Modifier.size(14.dp), tint = Color.White.copy(alpha = 0.3f))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "+${numberFormat.format(income)}원",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold, color = Color(0xFF93C5FD)
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable(onClick = onExpenseClick)
                        .padding(vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(20.dp).clip(RoundedCornerShape(6.dp))
                                .background(ExpenseRed.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.TrendingDown, null, Modifier.size(12.dp), tint = Color(0xFFFCA5A5))
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("지출", style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.6f))
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Outlined.ChevronRight, null, Modifier.size(14.dp), tint = Color.White.copy(alpha = 0.3f))
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        "-${numberFormat.format(expense)}원",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold, color = Color(0xFFFCA5A5)
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = Color.White.copy(alpha = 0.1f))
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.AccountBalanceWallet, null, Modifier.size(18.dp), tint = Color.White.copy(alpha = 0.6f))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("잔액", style = MaterialTheme.typography.bodyMedium, color = Color.White.copy(alpha = 0.6f))
                }
                if (hasBudget) {
                    Text(
                        buildAnnotatedString {
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color.White, fontSize = 28.sp, letterSpacing = (-1).sp)) {
                                append(numberFormat.format(balance))
                            }
                            withStyle(SpanStyle(fontWeight = FontWeight.Normal, color = Color.White.copy(alpha = 0.5f), fontSize = 16.sp)) {
                                append(" 원")
                            }
                        }
                    )
                } else {
                    Text(
                        "—",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White.copy(alpha = 0.3f)
                    )
                }
            }
        }
    }
}

@Composable
private fun BudgetEditDialog(
    currentBudget: Long,
    onConfirm: (Long) -> Unit,
    onDismiss: () -> Unit
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale.KOREA)
    var budgetText by remember {
        mutableStateOf(if (currentBudget > 0) currentBudget.toString() else "")
    }
    val parsedAmount = budgetText.toLongOrNull() ?: 0L
    val isValid = parsedAmount > 0

    val isDark = LocalIsDark.current

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color.White,
            tonalElevation = 0.dp,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icon header
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(
                                    GoldPrimary.copy(alpha = 0.15f),
                                    AccentGradientEnd.copy(alpha = 0.1f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Savings,
                        contentDescription = null,
                        modifier = Modifier.size(28.dp),
                        tint = GoldPrimary
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    "월 예산 설정",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    "이번 달 사용할 예산을 입력해주세요",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Navy600
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Amount display
                if (isValid) {
                    Text(
                        "${numberFormat.format(parsedAmount)}원",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        ),
                        color = GoldPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Input field
                OutlinedTextField(
                    value = budgetText,
                    onValueChange = { newValue ->
                        if (newValue.all { it.isDigit() } && newValue.length <= 12) {
                            budgetText = newValue
                        }
                    },
                    placeholder = {
                        Text(
                            "예: 3000000",
                            style = MaterialTheme.typography.titleLarge,
                            color = Navy400.copy(alpha = 0.5f)
                        )
                    },
                    suffix = {
                        Text(
                            "원",
                            style = MaterialTheme.typography.titleMedium,
                            color = Navy600
                        )
                    },
                    textStyle = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = (-0.3).sp,
                        color = Navy900
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = Navy200,
                        focusedContainerColor = GoldLight.copy(alpha = 0.3f),
                        unfocusedContainerColor = Color.Transparent,
                        cursorColor = GoldPrimary
                    )
                )

                // Quick amount chips
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(100L to "100만", 200L to "200만", 300L to "300만").forEach { (value, label) ->
                        val amount = value * 10000
                        SuggestionChip(
                            onClick = { budgetText = amount.toString() },
                            label = {
                                Text(
                                    label,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = SurfaceDim,
                                labelColor = Navy700
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = ButtonDefaults.outlinedButtonBorder(enabled = true)
                    ) {
                        Text("취소", fontWeight = FontWeight.Medium)
                    }
                    Button(
                        onClick = { onConfirm(parsedAmount) },
                        enabled = isValid,
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(Icons.Filled.Check, null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("확인", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun InboxBanner(count: Int, onClick: () -> Unit, isDark: Boolean) {
    if (count > 0) {
        val bannerShape = RoundedCornerShape(20.dp)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clip(bannerShape)
                .border(1.dp, if (isDark) Navy600.copy(alpha = 0.4f) else Navy200, bannerShape)
                .background(if (isDark) Navy800 else Color.White)
                .clickable(onClick = onClick)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(32.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Brush.verticalGradient(listOf(AccentGradientStart, AccentGradientEnd)))
                )
                Spacer(modifier = Modifier.width(12.dp))
                Icon(Icons.Outlined.FactCheck, null, tint = GoldPrimary, modifier = Modifier.size(22.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "검토 대기 ${count}건",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold, letterSpacing = (-0.3).sp),
                        color = if (isDark) Navy100 else Navy900
                    )
                    Text(
                        "자동 수집된 거래를 확인해주세요",
                        style = MaterialTheme.typography.bodySmall, color = if (isDark) Navy400 else Navy600
                    )
                }
                Icon(Icons.Default.ChevronRight, null, tint = if (isDark) Navy400 else Navy600, modifier = Modifier.size(20.dp))
            }
        }
    }
}
