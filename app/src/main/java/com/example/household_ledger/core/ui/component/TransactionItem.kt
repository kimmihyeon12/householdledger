package com.example.household_ledger.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.household_ledger.core.domain.model.Category
import com.example.household_ledger.core.domain.model.Transaction
import com.example.household_ledger.core.domain.model.TransactionType
import com.example.household_ledger.core.ui.theme.ExpenseRed
import com.example.household_ledger.core.ui.theme.IncomeBlue
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TransactionItem(
    transaction: Transaction,
    category: Category? = null,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    val isExpense = transaction.type == TransactionType.EXPENSE
    val amountColor = if (isExpense) ExpenseRed else IncomeBlue
    val amountPrefix = if (isExpense) "-" else "+"
    val formattedAmount = NumberFormat.getNumberInstance(Locale.KOREA).format(transaction.amount)
    val dateFormat = SimpleDateFormat("MM.dd HH:mm", Locale.KOREA)
    val formattedDate = dateFormat.format(Date(transaction.occurredAt))

    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(category?.color?.copy(alpha = 0.1f) ?: MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (category != null) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = category.name,
                    tint = category.color,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = transaction.merchant,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${category?.name ?: "기타"} · $formattedDate",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = "${amountPrefix}${formattedAmount}",
            style = MaterialTheme.typography.titleSmall.copy(
                letterSpacing = (-0.3).sp
            ),
            fontWeight = FontWeight.Bold,
            color = amountColor
        )
    }
}
