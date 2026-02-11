package com.example.household_ledger.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.household_ledger.data.mock.MockData
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.TrendingDown
import androidx.compose.material.icons.outlined.TrendingUp
import com.example.household_ledger.model.Transaction
import com.example.household_ledger.model.TransactionState
import com.example.household_ledger.model.TransactionType
import com.example.household_ledger.ui.components.TransactionItem
import com.example.household_ledger.ui.theme.*
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onNavigateToAddTransaction: (year: Int, month: Int, day: Int) -> Unit,
    onNavigateToEditTransaction: (Long) -> Unit = {}
) {
    var currentYear by remember { mutableIntStateOf(2025) }
    var currentMonth by remember { mutableIntStateOf(2) }
    var selectedDay by remember { mutableStateOf<Int?>(null) }

    val dailyTotals = remember(currentYear, currentMonth) {
        MockData.getDailyTotals(currentYear, currentMonth)
    }
    val selectedTransactions = remember(currentYear, currentMonth, selectedDay) {
        selectedDay?.let { MockData.getTransactionsByDay(currentYear, currentMonth, it) } ?: emptyList()
    }

    val numberFormat = NumberFormat.getNumberInstance(Locale.KOREA)

    val monthIncome = dailyTotals.values.sumOf { it.first }
    val monthExpense = dailyTotals.values.sumOf { it.second }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "캘린더",
                        fontWeight = FontWeight.Bold,
                        color = Navy900
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        },
        containerColor = SurfaceDim
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Month navigation header
            item {
                MonthHeader(
                    year = currentYear,
                    month = currentMonth,
                    onPreviousMonth = {
                        if (currentMonth == 1) {
                            currentMonth = 12; currentYear--
                        } else currentMonth--
                        selectedDay = null
                    },
                    onNextMonth = {
                        if (currentMonth == 12) {
                            currentMonth = 1; currentYear++
                        } else currentMonth++
                        selectedDay = null
                    }
                )
            }

            // Monthly summary
            item {
                var showTransactionList by remember { mutableStateOf<TransactionType?>(null) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally)
                ) {
                    SummaryChip(
                        label = "수입",
                        amount = "+${numberFormat.format(monthIncome)}원",
                        color = IncomeBlue,
                        onClick = { showTransactionList = TransactionType.INCOME }
                    )
                    SummaryChip(
                        label = "지출",
                        amount = "-${numberFormat.format(monthExpense)}원",
                        color = ExpenseRed,
                        onClick = { showTransactionList = TransactionType.EXPENSE }
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))

                if (showTransactionList != null) {
                    val filterType = showTransactionList!!
                    val filteredTransactions = MockData.transactions
                        .filter { tx ->
                            tx.type == filterType && tx.state == TransactionState.NORMAL &&
                            Calendar.getInstance().apply { timeInMillis = tx.occurredAt }.let { cal ->
                                cal.get(Calendar.YEAR) == currentYear && cal.get(Calendar.MONTH) == currentMonth - 1
                            }
                        }
                        .sortedByDescending { it.occurredAt }
                    val isExpense = filterType == TransactionType.EXPENSE
                    val totalAmount = filteredTransactions.sumOf { it.amount }

                    ModalBottomSheet(
                        onDismissRequest = { showTransactionList = null },
                        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                        containerColor = MaterialTheme.colorScheme.surface,
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
                                        .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f))
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
                                        "${currentMonth}월 ${if (isExpense) "지출" else "수입"} 내역",
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
                            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

                            if (filteredTransactions.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 40.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "${currentMonth}월 ${if (isExpense) "지출" else "수입"} 내역이 없습니다",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Navy400
                                    )
                                }
                            } else {
                                // Transaction list
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(max = 400.dp)
                                ) {
                                    items(filteredTransactions) { transaction ->
                                        TransactionItem(
                                            transaction = transaction,
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
            }

            // Calendar grid
            item {
                CalendarGrid(
                    year = currentYear,
                    month = currentMonth,
                    selectedDay = selectedDay,
                    dailyTotals = dailyTotals,
                    onDaySelected = { day ->
                        selectedDay = if (selectedDay == day) null else day
                    }
                )
            }

            // Selected day detail
            if (selectedDay != null) {
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    DayDetailHeader(
                        year = currentYear,
                        month = currentMonth,
                        day = selectedDay!!,
                        income = dailyTotals[selectedDay]?.first ?: 0L,
                        expense = dailyTotals[selectedDay]?.second ?: 0L,
                        numberFormat = numberFormat,
                        onAddTransaction = {
                            onNavigateToAddTransaction(currentYear, currentMonth, selectedDay!!)
                        }
                    )
                }

                if (selectedTransactions.isEmpty()) {
                    item {
                        EmptyDayState(
                            onAddTransaction = {
                                onNavigateToAddTransaction(currentYear, currentMonth, selectedDay!!)
                            }
                        )
                    }
                } else {
                    items(selectedTransactions) { transaction ->
                        TransactionItem(
                            transaction = transaction,
                            onClick = { onNavigateToEditTransaction(transaction.id) }
                        )
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 20.dp),
                            thickness = 0.5.dp,
                            color = Navy200.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            // Bottom spacing
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
private fun MonthHeader(
    year: Int,
    month: Int,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onPreviousMonth,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Navy100)
        ) {
            Icon(
                Icons.Default.ChevronLeft,
                contentDescription = "이전 달",
                tint = Navy700
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${year}년",
                style = MaterialTheme.typography.labelMedium,
                color = Navy400,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "${month}월",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = Navy900
            )
        }

        IconButton(
            onClick = onNextMonth,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Navy100)
        ) {
            Icon(
                Icons.Default.ChevronRight,
                contentDescription = "다음 달",
                tint = Navy700
            )
        }
    }
}

@Composable
private fun SummaryChip(label: String, amount: String, color: Color, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.2f),
                shape = RoundedCornerShape(24.dp)
            )
            .background(color.copy(alpha = 0.06f))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = color.copy(alpha = 0.8f),
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = amount,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = color
        )
        Spacer(modifier = Modifier.width(4.dp))
        Icon(
            Icons.Outlined.ChevronRight,
            null,
            modifier = Modifier.size(14.dp),
            tint = color.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun CalendarGrid(
    year: Int,
    month: Int,
    selectedDay: Int?,
    dailyTotals: Map<Int, Pair<Long, Long>>,
    onDaySelected: (Int) -> Unit
) {
    val cal = Calendar.getInstance().apply {
        set(year, month - 1, 1)
    }
    val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
    val firstDayOfWeek = (cal.get(Calendar.DAY_OF_WEEK) - Calendar.SUNDAY)

    val dayLabels = listOf("일", "월", "화", "수", "목", "금", "토")

    Column(
        modifier = Modifier
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .border(
                width = 1.dp,
                color = Navy200.copy(alpha = 0.5f),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(12.dp)
    ) {
        // Day of week header
        Row(modifier = Modifier.fillMaxWidth()) {
            dayLabels.forEachIndexed { index, label ->
                Text(
                    text = label,
                    modifier = Modifier
                        .weight(1f)
                        .padding(bottom = 8.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = when (index) {
                        0 -> ExpenseRed.copy(alpha = 0.55f)
                        6 -> IncomeBlue.copy(alpha = 0.55f)
                        else -> Navy400
                    }
                )
            }
        }

        // Calendar cells
        var dayCounter = 1
        val totalCells = firstDayOfWeek + daysInMonth
        val rows = (totalCells + 6) / 7

        for (row in 0 until rows) {
            Row(modifier = Modifier.fillMaxWidth()) {
                for (col in 0..6) {
                    val cellIndex = row * 7 + col
                    if (cellIndex < firstDayOfWeek || dayCounter > daysInMonth) {
                        Spacer(modifier = Modifier.weight(1f))
                    } else {
                        val day = dayCounter
                        val totals = dailyTotals[day]
                        val isSelected = selectedDay == day
                        val hasExpense = totals != null && totals.second > 0
                        val hasIncome = totals != null && totals.first > 0

                        val today = Calendar.getInstance()
                        val isToday = year == today.get(Calendar.YEAR) &&
                                month == today.get(Calendar.MONTH) + 1 &&
                                day == today.get(Calendar.DAY_OF_MONTH)

                        CalendarDayCell(
                            day = day,
                            isSelected = isSelected,
                            isToday = isToday,
                            isSunday = col == 0,
                            isSaturday = col == 6,
                            hasExpense = hasExpense,
                            hasIncome = hasIncome,
                            expenseAmount = totals?.second ?: 0,
                            incomeAmount = totals?.first ?: 0,
                            onClick = { onDaySelected(day) },
                            modifier = Modifier.weight(1f)
                        )
                        dayCounter++
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarDayCell(
    day: Int,
    isSelected: Boolean,
    isToday: Boolean,
    isSunday: Boolean,
    isSaturday: Boolean,
    hasExpense: Boolean,
    hasIncome: Boolean,
    expenseAmount: Long,
    incomeAmount: Long,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .defaultMinSize(minHeight = 56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(
                when {
                    isSelected -> GoldPrimary.copy(alpha = 0.15f)
                    else -> Color.Transparent
                }
            )
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Day number
        Box(
            modifier = Modifier
                .size(28.dp)
                .then(
                    if (isToday) Modifier
                        .clip(CircleShape)
                        .background(GoldPrimary)
                    else Modifier
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$day",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = when {
                    isToday -> FontWeight.Bold
                    isSelected -> FontWeight.Bold
                    else -> FontWeight.Medium
                },
                color = when {
                    isToday -> Color.White
                    isSelected -> Navy900
                    isSunday -> ExpenseRed.copy(alpha = 0.65f)
                    isSaturday -> IncomeBlue.copy(alpha = 0.65f)
                    else -> Navy800
                }
            )
        }

        // Amount indicators
        if (hasIncome) {
            Text(
                text = "+${formatShortAmount(incomeAmount)}",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = IncomeBlue,
                maxLines = 1
            )
        }
        if (hasExpense) {
            Text(
                text = "-${formatShortAmount(expenseAmount)}",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                ),
                color = ExpenseRed,
                maxLines = 1
            )
        }

        // Spacer when no amounts
        if (!hasIncome && !hasExpense) {
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}

private fun formatShortAmount(amount: Long): String {
    return when {
        amount >= 1000000 -> "${amount / 10000}만"
        amount >= 10000 -> "${amount / 10000}.${(amount % 10000) / 1000}만"
        amount >= 1000 -> "${amount / 1000}천"
        else -> "${amount}"
    }
}

@Composable
private fun DayDetailHeader(
    year: Int,
    month: Int,
    day: Int,
    income: Long,
    expense: Long,
    numberFormat: NumberFormat,
    onAddTransaction: () -> Unit
) {
    val dayOfWeekLabels = arrayOf("일", "월", "화", "수", "목", "금", "토")
    val cal = Calendar.getInstance().apply { set(year, month - 1, day) }
    val dayOfWeek = dayOfWeekLabels[cal.get(Calendar.DAY_OF_WEEK) - 1]

    val accentColor = GoldPrimary

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(
                width = 1.dp,
                color = Navy200.copy(alpha = 0.5f),
                shape = RoundedCornerShape(24.dp)
            )
            .background(Color.White)
    ) {
        // Left accent bar
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .width(4.dp)
                .fillMaxHeight()
                .drawBehind {
                    drawRoundRect(
                        color = accentColor,
                        cornerRadius = CornerRadius(4.dp.toPx()),
                        topLeft = Offset.Zero,
                        size = Size(4.dp.toPx(), size.height)
                    )
                }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${month}월 ${day}일 (${dayOfWeek})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Navy900
                )
                IconButton(
                    onClick = onAddTransaction,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(GoldLight)
                ) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "거래 추가",
                        modifier = Modifier.size(18.dp),
                        tint = GoldPrimary
                    )
                }
            }

            if (income > 0 || expense > 0) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    if (income > 0) {
                        Text(
                            text = "수입 +${numberFormat.format(income)}원",
                            style = MaterialTheme.typography.bodySmall,
                            color = IncomeBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    if (expense > 0) {
                        Text(
                            text = "지출 -${numberFormat.format(expense)}원",
                            style = MaterialTheme.typography.bodySmall,
                            color = ExpenseRed,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyDayState(
    onAddTransaction: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                Icons.Default.EventBusy,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = Navy400.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                "이 날은 거래가 없어요",
                style = MaterialTheme.typography.bodyMedium,
                color = Navy400,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(16.dp))
            FilledTonalButton(
                onClick = onAddTransaction,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = GoldLight,
                    contentColor = GoldPrimary
                )
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "거래 추가",
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
