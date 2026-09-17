package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titleEn: String,
    val titleBn: String,
    val descriptionEn: String,
    val descriptionBn: String,
    val categoryId: Long,
    val categoryNameEn: String,
    val categoryNameBn: String,
    val price: Double,
    val originalPrice: Double,
    val discountPercent: Int = 0,
    val rating: Double = 4.8,
    val reviewCount: Int = 12,
    val stock: Int = 50,
    val sellerId: Long = 1,
    val sellerName: String = "Siam Official Flagship",
    val imageUrl: String = "",
    val isFlashSale: Boolean = false,
    val isFeatured: Boolean = true,
    val specifications: String = "", // e.g. "Brand: Official, Warranty: 1 Year"
    val createdAt: Long = System.currentTimeMillis()
)
