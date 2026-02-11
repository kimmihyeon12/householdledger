package com.example.household_ledger.model

data class CharacterState(
    val equippedCloth: ShopItem? = null,
    val equippedAccessory: ShopItem? = null,
    val equippedBackground: ShopItem? = null,
    val updatedAt: Long = System.currentTimeMillis()
)
