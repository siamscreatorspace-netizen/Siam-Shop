package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coupons")
data class CouponEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String,
    val discountPercent: Int = 10,
    val flatDiscount: Double = 0.0,
    val minSpend: Double = 500.0,
    val maxDiscount: Double = 1000.0,
    val descriptionEn: String = "Special discount for all orders",
    val descriptionBn: String = "সকল অর্ডারের জন্য বিশেষ ছাড়",
    val isActive: Boolean = true,
    val usageCount: Int = 0
)
