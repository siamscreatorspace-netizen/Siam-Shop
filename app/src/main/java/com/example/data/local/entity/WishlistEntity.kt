package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wishlist_items")
data class WishlistEntity(
    @PrimaryKey
    val productId: Long,
    val titleEn: String,
    val titleBn: String,
    val price: Double,
    val originalPrice: Double,
    val imageUrl: String = "",
    val rating: Double = 4.8,
    val addedAt: Long = System.currentTimeMillis()
)
