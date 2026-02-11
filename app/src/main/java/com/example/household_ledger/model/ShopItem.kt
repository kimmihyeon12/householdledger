package com.example.household_ledger.model

data class ShopItem(
    val itemId: Long,
    val name: String,
    val rarity: ItemRarity,
    val price: Int,
    val type: ItemType,
    val description: String = "",
    val owned: Boolean = false
)

enum class ItemRarity { COMMON, RARE, EPIC }
enum class ItemType { CLOTH, ACCESSORY, BACKGROUND }
