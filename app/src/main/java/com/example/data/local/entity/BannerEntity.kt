package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "banners")
data class BannerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titleEn: String,
    val titleBn: String,
    val subtitleEn: String,
    val subtitleBn: String,
    val badgeEn: String = "UP TO 50% OFF",
    val badgeBn: String = "৫০% পর্যন্ত ছাড়",
    val drawableResName: String = "banner_hero_promo",
    val targetCategoryId: Long = 0,
    val isActive: Boolean = true
)
