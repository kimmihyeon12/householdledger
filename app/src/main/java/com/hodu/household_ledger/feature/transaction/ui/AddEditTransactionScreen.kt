package com.hodu.household_ledger.feature.transaction.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hodu.household_ledger.core.domain.model.TransactionType
import com.hodu.household_ledger.core.ui.theme.*
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditTransactionScreen(
    onNavigateBack: () -> Unit,
    initialYear: Int? = null,
    initialMonth: Int? = null,
    initialDay: Int? = null,
    editTransactionId: Long? = null,
    viewModel: AddEditTransactionViewModel = viewModel()
) {
    val categories by viewModel.categories.collectAsState()
    val editTransaction by viewModel.editTransaction.collectAsState()
    val saveSuccess by viewModel.saveSuccess.collectAsState()

    // Load transaction for edit mode
    LaunchedEffect(editTransactionId) {
        if (editTransactionId != null) {
            viewModel.loadTransaction(editTransactionId)
        }
    }

    // Navigate back on save success
    LaunchedEffect(saveSuccess) {
        if (saveSuccess) onNavigateBack()
    }

    val isEditMode = editTransaction != null

    var selectedType by remember { mutableStateOf(editTransaction?.type ?: TransactionType.EXPENSE) }
    var amount by remember { mutableStateOf(editTransaction?.amount?.toString() ?: "") }
    var merchant by remember { mutableStateOf(editTransaction?.merchant ?: "") }
    var note by remember { mutableStateOf(editTransaction?.note ?: "") }
    var selectedCategoryId by remember { mutableLongStateOf(editTransaction?.categoryId ?: 1L) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Update state when editTransaction loads
    LaunchedEffect(editTransaction) {
        editTransaction?.let { tx ->
            selectedType = tx.type
            amount = tx.amount.toString()
            merchant = tx.merchant
            note = tx.note
            selectedCategoryId = tx.categoryId
        }
    }

    val filteredCategories = categories.filter { it.type == selectedType }
    val isSaveEnabled = amount.isNotBlank() && merchant.isNotBlank()

    Scaffold(
        containerColor = Color.White,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditMode) "거래 수정" else "거래 추가",
                        style = MaterialTheme.typography.titleLarge,
                        color = Navy900
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, contentDescription = "닫기", tint = Navy900)
                    }
                },
                actions = {
                    if (isEditMode) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Outlined.Delete, contentDescription = "삭제", tint = ExpenseRed)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Surface(tonalElevation = 0.dp, shadowElevation = 0.dp, color = Color.White) {
                Button(
                    onClick = {
                        val amountValue = amount.toLongOrNull() ?: return@Button
                        val occurredAt = if (editTransaction != null) {
                            editTransaction!!.occurredAt
                        } else if (initialYear != null && initialMonth != null && initialDay != null) {
                            Calendar.getInstance().apply {
                                set(initialYear, initialMonth - 1, initialDay, 12, 0, 0)
                            }.timeInMillis
                        } else {
                            System.currentTimeMillis()
                        }

                        if (isEditMode) {
                            viewModel.updateTransaction(
                                id = editTransaction!!.id,
                                type = selectedType,
                                amount = amountValue,
                                merchant = merchant,
                                categoryId = selectedCategoryId,
                                note = note
                            )
                        } else {
                            viewModel.saveTransaction(
                                type = selectedType,
                                amount = amountValue,
                                merchant = merchant,
                                categoryId = selectedCategoryId,
                                occurredAt = occurredAt,
                                note = note
                            )
                        }
                    },
                    enabled = isSaveEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        disabledContainerColor = Navy200
                    )
                ) {
                    Text(
                        if (isEditMode) "수정하기" else "저장하기",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Type selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val expenseSelected = selectedType == TransactionType.EXPENSE
                OutlinedCard(
                    onClick = {
                        selectedType = TransactionType.EXPENSE
                        val firstExpenseCat = categories.firstOrNull { it.type == TransactionType.EXPENSE }
                        selectedCategoryId = firstExpenseCat?.id ?: 1L
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        width = if (expenseSelected) 2.dp else 1.dp,
                        brush = if (expenseSelected) SolidColor(ExpenseRed) else SolidColor(Navy200)
                    ),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = if (expenseSelected) ExpenseSoft.copy(alpha = 0.3f) else Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Outlined.TrendingDown, null, tint = if (expenseSelected) ExpenseRed else Navy600, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("지출", style = MaterialTheme.typography.titleSmall, fontWeight = if (expenseSelected) FontWeight.Bold else FontWeight.Normal, color = if (expenseSelected) ExpenseRed else Navy600)
                    }
                }

                val incomeSelected = selectedType == TransactionType.INCOME
                OutlinedCard(
                    onClick = {
                        selectedType = TransactionType.INCOME
                        val firstIncomeCat = categories.firstOrNull { it.type == TransactionType.INCOME }
                        selectedCategoryId = firstIncomeCat?.id ?: 9L
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        width = if (incomeSelected) 2.dp else 1.dp,
                        brush = if (incomeSelected) SolidColor(IncomeBlue) else SolidColor(Navy200)
                    ),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = if (incomeSelected) IncomeSoft.copy(alpha = 0.3f) else Color.White
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Outlined.TrendingUp, null, tint = if (incomeSelected) IncomeBlue else Navy600, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("수입", style = MaterialTheme.typography.titleSmall, fontWeight = if (incomeSelected) FontWeight.Bold else FontWeight.Normal, color = if (incomeSelected) IncomeBlue else Navy600)
                    }
                }
            }

            // Amount input
            OutlinedTextField(
                value = amount,
                onValueChange = { newValue -> if (newValue.all { it.isDigit() }) amount = newValue },
                label = { Text("금액") },
                suffix = { Text("원", color = Navy600) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            // Merchant input
            OutlinedTextField(
                value = merchant,
                onValueChange = { merchant = it },
                label = { Text("가맹점 / 상호명") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            // Category selector
            Column {
                Text(
                    text = "카테고리",
                    style = MaterialTheme.typography.labelLarge,
                    color = Navy600,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filteredCategories.forEach { category ->
                        val selected = selectedCategoryId == category.id
                        FilterChip(
                            selected = selected,
                            onClick = { selectedCategoryId = category.id },
                            label = { Text(category.name) },
                            leadingIcon = {
                                Icon(
                                    category.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (selected) category.color else Navy600
                                )
                            },
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // Note input
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("메모 (선택)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4,
                shape = RoundedCornerShape(14.dp)
            )

            // Date display
            val dateText = if (editTransaction != null) {
                val cal = Calendar.getInstance().apply { timeInMillis = editTransaction!!.occurredAt }
                "${cal.get(Calendar.YEAR)}년 ${cal.get(Calendar.MONTH) + 1}월 ${cal.get(Calendar.DAY_OF_MONTH)}일"
            } else if (initialYear != null && initialMonth != null && initialDay != null) {
                "${initialYear}년 ${initialMonth}월 ${initialDay}일"
            } else {
                val cal = Calendar.getInstance()
                "${cal.get(Calendar.YEAR)}년 ${cal.get(Calendar.MONTH) + 1}월 ${cal.get(Calendar.DAY_OF_MONTH)}일 (오늘)"
            }
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    width = 1.dp,
                    brush = SolidColor(Navy200)
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Outlined.CalendarToday, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "날짜", style = MaterialTheme.typography.labelSmall, color = Navy600)
                        Text(text = dateText, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("거래 삭제", fontWeight = FontWeight.Bold) },
            text = { Text("이 거래를 삭제하시겠습니까?\n삭제된 거래는 복구할 수 없습니다.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        editTransaction?.let { viewModel.deleteTransaction(it.id) }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
                ) { Text("삭제") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("취소") }
            },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White
        )
    }
}
