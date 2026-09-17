package com.example.model

enum class OrderStatus(val titleEn: String, val titleBn: String, val stepIndex: Int) {
    PENDING("Pending", "অপেক্ষমান", 0),
    CONFIRMED("Confirmed", "নিশ্চিতকৃত", 1),
    SHIPPED("Shipped", "শিপমেন্টে", 2),
    OUT_FOR_DELIVERY("Out for Delivery", "ডেলিভারির পথে", 3),
    DELIVERED("Delivered", "ডেলিভার্ড", 4),
    CANCELLED("Cancelled", "বাতিল", -1)
}
