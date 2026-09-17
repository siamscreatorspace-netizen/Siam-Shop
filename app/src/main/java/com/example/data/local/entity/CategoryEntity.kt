package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nameEn: String,
    val nameBn: String,
    val iconKey: String = "category",
    val sortOrder: Int = 0,
    val isActive: Boolean = true
)
