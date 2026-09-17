package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productId: Long,
    val customerName: String,
    val rating: Int = 5,
    val comment: String,
    val dateString: String = "Today",
    val verifiedPurchase: Boolean = true
)
