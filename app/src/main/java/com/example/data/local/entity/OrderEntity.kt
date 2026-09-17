package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.OrderStatus
import com.example.model.PaymentMethod

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderNumber: String,
    val customerName: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val district: String = "Dhaka",
    val itemsSummary: String, // e.g. "Smart Watch Pro (x1), Cotton Polo Shirt (x2)"
    val itemCount: Int,
    val totalAmount: Double,
    val discountAmount: Double = 0.0,
    val deliveryFee: Double = 60.0,
    val finalAmount: Double,
    val paymentMethod: PaymentMethod = PaymentMethod.COD,
    val orderStatus: OrderStatus = OrderStatus.PENDING,
    val couponCode: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val trackingNote: String = "Order received and pending verification"
)
