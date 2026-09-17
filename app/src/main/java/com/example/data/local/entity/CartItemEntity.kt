package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Long,
    val productTitleEn: String,
    val productTitleBn: String,
    val price: Double,
    val originalPrice: Double,
    val imageUrl: String = "",
    val quantity: Int = 1,
    val sellerName: String = "Siam Official Store",
    val addedAt: Long = System.currentTimeMillis()
)
