package com.hodu.household_ledger.feature.stats.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hodu.household_ledger.core.ui.theme.*
import java.text.NumberFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: StatsViewModel = viewModel()
) {
    val numberFormat = NumberFormat.getNumberInstance(Locale.KOREA)
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("월별 추이", "카테고리")

    val monthlyExpense by viewModel.monthlyExpense.collectAsState()
    val categoryBreakdown by viewModel.categoryBreakdown.collectAsState()
    val monthlySummaries by viewModel.monthlySummaries.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "통계",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Segmented tab row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                tabs.forEachIndexed { index, label ->
                    val selected = selectedTab == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { selectedTab = index }
                            .background(if (selected) MaterialTheme.colorScheme.surface else Color.Transparent)
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                            color = if (selected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }

            when (selectedTab) {
                0 -> MonthlyTrendContent(
                    monthlyData = monthlySummaries,
                    currentMonthExpense = monthlyExpense,
                    numberFormat = numberFormat
                )
                1 -> CategoryStatsContent(
                    breakdown = categoryBreakdown,
                    totalExpense = monthlyExpense,
                    numberFormat = numberFormat
                )
            }
        }
    }
}

@Composable
private fun MonthlyTrendContent(
    monthlyData: List<Pair<String, Long>>,
    currentMonthExpense: Long,
    numberFormat: NumberFormat
) {
    val data = if (monthlyData.isEmpty()) {
        listOf("1월" to 0L, "2월" to 0L, "3월" to 0L, "4월" to 0L, "5월" to 0L, "6월" to 0L)
    } else {
        monthlyData
    }

    val maxAmount = data.maxOfOrNull { it.second }?.coerceAtLeast(1L) ?: 1L
    val previousExpense = if (data.size >= 2) data[data.size - 2].second else 0L
    val diff = currentMonthExpense - previousExpense
    val diffPercent = if (previousExpense > 0) (diff.toFloat() / previousExpense * 100) else 0f
    val isIncrease = diff >= 0
    val sixMonthAvg = if (data.isNotEmpty()) data.sumOf { it.second } / data.size else 0L
    val currentMonthLabel = data.lastOrNull()?.first ?: ""

    Column(modifier = Modifier.padding(20.dp)) {
        // Bar chart card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    "월별 지출 추이",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    data.forEach { (month, amount) ->
                        val isCurrentMonth = month == currentMonthLabel
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Bottom,
                            modifier = Modifier.weight(1f)
                        ) {
                            if (amount > 0) {
                                Text(
                                    "${amount / 10000}만",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isCurrentMonth) GoldPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 9.sp,
                                    fontWeight = if (isCurrentMonth) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .width(28.dp)
                                    .height(
                                        if (amount > 0) (amount.toFloat() / maxAmount * 120).dp
                                        else 4.dp
                                    )
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(
                                        if (isCurrentMonth) GoldPrimary
                                        else GoldPrimary.copy(alpha = 0.15f)
                                    )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                month,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isCurrentMonth) FontWeight.Bold else FontWeight.Normal,
                                color = if (isCurrentMonth) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Month-over-month comparison card
        if (previousExpense > 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isIncrease) ExpenseSoft else ConfirmSoft),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (isIncrease) Icons.Outlined.TrendingUp else Icons.Outlined.TrendingDown,
                            contentDescription = null,
                            modifier = Modifier.size(22.dp),
                            tint = if (isIncrease) ExpenseRed else ConfirmGreen
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("전월 대비", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "${if (isIncrease) "+" else ""}${numberFormat.format(diff)}원",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isIncrease) ExpenseRed else ConfirmGreen
                        )
                    }
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isIncrease) ExpenseSoft else ConfirmSoft
                    ) {
                        Text(
                            "${if (isIncrease) "↑" else "↓"}${String.format("%.1f", kotlin.math.abs(diffPercent))}%",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isIncrease) ExpenseRed else ConfirmGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        // 6-month average card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(GoldLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Outlined.Analytics,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = GoldPrimary
                    )
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("6개월 평균", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "${numberFormat.format(sixMonthAvg)}원",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryStatsContent(
    breakdown: List<CategoryBreakdownEntry>,
    totalExpense: Long,
    numberFormat: NumberFormat
) {
    val sortedEntries = breakdown.sortedByDescending { it.amount }
    val colors = sortedEntries.map { it.category.color }
    val proportions = if (totalExpense > 0) {
        sortedEntries.map { it.amount.toFloat() / totalExpense }
    } else emptyList()

    Column(modifier = Modifier.padding(20.dp)) {
        // Donut chart
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentAlignment = Alignment.Center
        ) {
            if (proportions.isNotEmpty()) {
                Canvas(modifier = Modifier.size(200.dp)) {
                    var startAngle = -90f
                    val gap = 3f
                    proportions.forEachIndexed { index, proportion ->
                        val sweepAngle = proportion * 360f - gap
                        drawArc(
                            color = colors[index],
                            startAngle = startAngle,
                            sweepAngle = sweepAngle.coerceAtLeast(1f),
                            useCenter = false,
                            style = Stroke(width = 32f, cap = StrokeCap.Round),
                            topLeft = Offset(16f, 16f),
                            size = Size(size.width - 32f, size.height - 32f)
                        )
                        startAngle += proportion * 360f
                    }
                }
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("총 지출", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    "${numberFormat.format(totalExpense)}",
                    style = MaterialTheme.typography.headlineMedium.copy(letterSpacing = (-0.5).sp),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Category list
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(4.dp)) {
                sortedEntries.forEachIndexed { index, entry ->
                    val percentage = entry.percentage.toInt()
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(entry.category.color.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                entry.category.icon,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = entry.category.color
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = entry.category.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { if (totalExpense > 0) entry.amount.toFloat() / totalExpense else 0f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp)),
                                color = entry.category.color,
                                trackColor = entry.category.color.copy(alpha = 0.1f)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${numberFormat.format(entry.amount)}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${percentage}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    if (index < sortedEntries.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }
}
