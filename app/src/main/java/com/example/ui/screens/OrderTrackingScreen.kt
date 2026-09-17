package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.OrderEntity
import com.example.model.AppStrings
import com.example.model.Language
import com.example.model.OrderStatus
import com.example.ui.components.StatusBadge
import com.example.ui.theme.SiamPrimary
import com.example.ui.theme.SiamSecondary

@Composable
fun OrderTrackingScreen(
    order: OrderEntity?,
    language: Language,
    onBackToHome: () -> Unit
) {
    if (order == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    imageVector = Icons.Filled.ShoppingBag,
                    contentDescription = "No Order",
                    tint = Color.Gray,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (language == Language.BANGLA) "কোনো অর্ডার পাওয়া যায়নি!" else "No order selected!",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onBackToHome) {
                    Text(if (language == Language.BANGLA) "হোম পেজে ফিরুন" else "Return to Home")
                }
            }
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("order_tracking_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Success Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SiamPrimary.copy(alpha = 0.08f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(SiamPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = "Success",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (language == Language.BANGLA) "অর্ডার সফলভাবে সম্পন্ন হয়েছে!" else "Order Placed Successfully!",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = SiamPrimary
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "${AppStrings.get("order_number", language)}: ${order.orderNumber}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    StatusBadge(status = order.orderStatus, language = language)
                }
            }
        }

        // Tracking Timeline Stepper Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = AppStrings.get("track_order", language),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    val steps = listOf(
                        Triple(OrderStatus.PENDING, "Order Placed", "অর্ডার গ্রহণ করা হয়েছে"),
                        Triple(OrderStatus.CONFIRMED, "Order Confirmed", "অর্ডার নিশ্চিত হয়েছে"),
                        Triple(OrderStatus.SHIPPED, "Shipped & In Transit", "কুরিয়ারে পাঠানো হয়েছে"),
                        Triple(OrderStatus.OUT_FOR_DELIVERY, "Out for Delivery", "ডেলিভারির জন্য বের হয়েছে"),
                        Triple(OrderStatus.DELIVERED, "Delivered", "সফলভাবে পৌঁছেছে")
                    )

                    val currentStatusIndex = when (order.orderStatus) {
                        OrderStatus.PENDING -> 0
                        OrderStatus.CONFIRMED -> 1
                        OrderStatus.SHIPPED -> 2
                        OrderStatus.OUT_FOR_DELIVERY -> 3
                        OrderStatus.DELIVERED -> 4
                        OrderStatus.CANCELLED -> -1
                    }

                    steps.forEachIndexed { index, step ->
                        val isCompleted = currentStatusIndex >= index
                        val isCurrent = currentStatusIndex == index

                        Row(modifier = Modifier.fillMaxWidth()) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (isCompleted) SiamPrimary else Color.LightGray.copy(alpha = 0.5f)
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isCompleted) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = "Done",
                                            tint = Color.White,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }

                                if (index < steps.size - 1) {
                                    Box(
                                        modifier = Modifier
                                            .width(2.dp)
                                            .height(36.dp)
                                            .background(
                                                if (currentStatusIndex > index) SiamPrimary else Color.LightGray.copy(alpha = 0.5f)
                                            )
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.padding(bottom = if (index < steps.size - 1) 20.dp else 0.dp)) {
                                Text(
                                    text = if (language == Language.BANGLA) step.third else step.second,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isCompleted) MaterialTheme.colorScheme.onSurface else Color.Gray
                                    )
                                )
                                if (isCurrent && order.trackingNote.isNotBlank()) {
                                    Text(
                                        text = order.trackingNote,
                                        style = MaterialTheme.typography.labelSmall.copy(color = SiamPrimary, fontSize = 11.sp),
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Delivery Information Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = AppStrings.get("delivery_address", language),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${order.customerName} (${order.customerPhone})",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "${order.deliveryAddress}, ${order.district}",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${AppStrings.get("payment_method", language)}: ${if (language == Language.BANGLA) order.paymentMethod.titleBn else order.paymentMethod.titleEn}",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold, color = SiamSecondary)
                    )
                }
            }
        }

        // Order Items & Total Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = AppStrings.get("order_summary", language),
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = order.itemsSummary,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Divider(modifier = Modifier.padding(vertical = 10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = AppStrings.get("total", language),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "৳${order.finalAmount.toInt()}",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                color = SiamSecondary
                            )
                        )
                    }
                }
            }
        }

        // Back to Shopping CTA
        item {
            Button(
                onClick = onBackToHome,
                colors = ButtonDefaults.buttonColors(containerColor = SiamPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("track_back_home_btn")
            ) {
                Text(
                    text = if (language == Language.BANGLA) "আরও কেনাকাটা করুন" else "Continue Shopping",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }
    }
}
