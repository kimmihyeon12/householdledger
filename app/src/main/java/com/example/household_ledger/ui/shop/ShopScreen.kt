package com.example.household_ledger.ui.shop

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.household_ledger.data.mock.MockData
import com.example.household_ledger.model.ItemRarity
import com.example.household_ledger.model.ItemType
import com.example.household_ledger.model.ShopItem
import com.example.household_ledger.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopScreen(
    onNavigateBack: () -> Unit
) {
    var selectedFilter by remember { mutableStateOf<ItemType?>(null) }
    val items = if (selectedFilter != null) {
        MockData.shopItems.filter { it.type == selectedFilter }
    } else {
        MockData.shopItems
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("상점", style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로")
                    }
                },
                actions = {
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = PointGoldSoft
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Stars, null, tint = PointGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "${MockData.totalPoints}P",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = GoldDark
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { selectedFilter = null },
                    label = { Text("전체") },
                    shape = RoundedCornerShape(10.dp)
                )
                FilterChip(
                    selected = selectedFilter == ItemType.CLOTH,
                    onClick = { selectedFilter = if (selectedFilter == ItemType.CLOTH) null else ItemType.CLOTH },
                    label = { Text("의상") },
                    shape = RoundedCornerShape(10.dp)
                )
                FilterChip(
                    selected = selectedFilter == ItemType.ACCESSORY,
                    onClick = { selectedFilter = if (selectedFilter == ItemType.ACCESSORY) null else ItemType.ACCESSORY },
                    label = { Text("악세서리") },
                    shape = RoundedCornerShape(10.dp)
                )
                FilterChip(
                    selected = selectedFilter == ItemType.BACKGROUND,
                    onClick = { selectedFilter = if (selectedFilter == ItemType.BACKGROUND) null else ItemType.BACKGROUND },
                    label = { Text("배경") },
                    shape = RoundedCornerShape(10.dp)
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items) { item ->
                    ShopItemCard(item = item)
                }
            }
        }
    }
}

@Composable
private fun ShopItemCard(item: ShopItem) {
    val rarityColor = when (item.rarity) {
        ItemRarity.COMMON -> RarityCommon
        ItemRarity.RARE -> RarityRare
        ItemRarity.EPIC -> RarityEpic
    }
    val rarityLabel = when (item.rarity) {
        ItemRarity.COMMON -> "COMMON"
        ItemRarity.RARE -> "RARE"
        ItemRarity.EPIC -> "EPIC"
    }
    val typeIcon = when (item.type) {
        ItemType.CLOTH -> Icons.Outlined.Checkroom
        ItemType.ACCESSORY -> Icons.Outlined.Diamond
        ItemType.BACKGROUND -> Icons.Outlined.Wallpaper
    }
    val canAfford = MockData.totalPoints >= item.price
    var showDialog by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        border = CardDefaults.outlinedCardBorder().copy(
            width = 1.dp,
            brush = androidx.compose.ui.graphics.SolidColor(
                MaterialTheme.colorScheme.outline.copy(alpha = 0.12f)
            )
        )
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Rarity badge
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = rarityColor.copy(alpha = 0.1f),
                modifier = Modifier.align(Alignment.Start)
            ) {
                Text(
                    text = rarityLabel,
                    style = MaterialTheme.typography.labelSmall,
                    color = rarityColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                rarityColor.copy(alpha = 0.12f),
                                rarityColor.copy(alpha = 0.04f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    typeIcon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = rarityColor
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Text(
                text = item.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth(),
                enabled = canAfford,
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Icon(Icons.Default.Stars, null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "${item.price}P",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("구매 확인", fontWeight = FontWeight.Bold) },
            text = { Text("'${item.name}'을(를) ${item.price}P에 구매하시겠습니까?") },
            confirmButton = {
                Button(
                    onClick = { showDialog = false },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("구매")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("취소")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }
}
