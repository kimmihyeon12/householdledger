package com.example.household_ledger.ui.transaction

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
import androidx.compose.ui.unit.dp
import com.example.household_ledger.data.mock.MockData
import com.example.household_ledger.model.TransactionType
import com.example.household_ledger.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AddEditTransactionScreen(
    onNavigateBack: () -> Unit,
    initialYear: Int? = null,
    initialMonth: Int? = null,
    initialDay: Int? = null,
    editTransactionId: Long? = null
) {
    val editTransaction = editTransactionId?.let { MockData.getTransactionById(it) }
    val isEditMode = editTransaction != null

    var selectedType by remember { mutableStateOf(editTransaction?.type ?: TransactionType.EXPENSE) }
    var amount by remember { mutableStateOf(editTransaction?.amount?.toString() ?: "") }
    var merchant by remember { mutableStateOf(editTransaction?.merchant ?: "") }
    var note by remember { mutableStateOf(editTransaction?.note ?: "") }
    var selectedCategoryId by remember { mutableLongStateOf(editTransaction?.categoryId ?: 1L) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val categories = MockData.categories.filter { it.type == selectedType }
    val isSaveEnabled = amount.isNotBlank() && merchant.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (isEditMode) "거래 수정" else "거래 추가",
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, contentDescription = "닫기")
                    }
                },
                actions = {
                    if (isEditMode) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                Icons.Outlined.Delete,
                                contentDescription = "삭제",
                                tint = ExpenseRed
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 0.dp,
                shadowElevation = 0.dp,
                color = MaterialTheme.colorScheme.background
            ) {
                Button(
                    onClick = onNavigateBack,
                    enabled = isSaveEnabled,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp)
                        .height(52.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
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
                        selectedCategoryId = 1L
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        width = if (expenseSelected) 2.dp else 1.dp,
                        brush = if (expenseSelected)
                            androidx.compose.ui.graphics.SolidColor(ExpenseRed)
                        else
                            androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = if (expenseSelected) ExpenseSoft.copy(alpha = 0.3f)
                        else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Outlined.TrendingDown,
                            null,
                            tint = if (expenseSelected) ExpenseRed else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "지출",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (expenseSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (expenseSelected) ExpenseRed else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                val incomeSelected = selectedType == TransactionType.INCOME
                OutlinedCard(
                    onClick = {
                        selectedType = TransactionType.INCOME
                        selectedCategoryId = 9L
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    border = CardDefaults.outlinedCardBorder().copy(
                        width = if (incomeSelected) 2.dp else 1.dp,
                        brush = if (incomeSelected)
                            androidx.compose.ui.graphics.SolidColor(IncomeBlue)
                        else
                            androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ),
                    colors = CardDefaults.outlinedCardColors(
                        containerColor = if (incomeSelected) IncomeSoft.copy(alpha = 0.3f)
                        else MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Outlined.TrendingUp,
                            null,
                            tint = if (incomeSelected) IncomeBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            "수입",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = if (incomeSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (incomeSelected) IncomeBlue else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Amount input
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
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
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
                                    tint = if (selected) category.color else MaterialTheme.colorScheme.onSurfaceVariant
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
                val cal = java.util.Calendar.getInstance().apply { timeInMillis = editTransaction.occurredAt }
                "${cal.get(java.util.Calendar.YEAR)}년 ${cal.get(java.util.Calendar.MONTH) + 1}월 ${cal.get(java.util.Calendar.DAY_OF_MONTH)}일"
            } else if (initialYear != null && initialMonth != null && initialDay != null) {
                "${initialYear}년 ${initialMonth}월 ${initialDay}일"
            } else {
                "2025년 2월 1일 (오늘)"
            }
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                border = CardDefaults.outlinedCardBorder().copy(
                    width = 1.dp,
                    brush = androidx.compose.ui.graphics.SolidColor(
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                    )
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Outlined.CalendarToday,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "날짜",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = dateText,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(60.dp))
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text("거래 삭제", fontWeight = FontWeight.Bold)
            },
            text = {
                Text("이 거래를 삭제하시겠습니까?\n삭제된 거래는 복구할 수 없습니다.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        onNavigateBack()
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = ExpenseRed)
                ) {
                    Text("삭제")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("취소")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}
