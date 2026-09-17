package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sellers")
data class SellerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val storeName: String,
    val ownerName: String,
    val phone: String,
    val email: String,
    val categorySpecialty: String,
    val commissionPercent: Double = 5.0, // Commission system
    val isApproved: Boolean = true,
    val totalSalesAmount: Double = 0.0,
    val totalOrdersCount: Int = 0,
    val rating: Double = 4.9,
    val joinedAt: Long = System.currentTimeMillis()
)
