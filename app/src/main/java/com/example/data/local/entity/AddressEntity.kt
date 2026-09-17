package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "addresses")
data class AddressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val recipientName: String,
    val phone: String,
    val alternativePhone: String = "",
    val division: String = "Dhaka",
    val district: String = "Dhaka City",
    val fullAddress: String,
    val landmark: String = "",
    val tag: String = "Home", // Home, Office, Other
    val isDefault: Boolean = false
)
