package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.CartItemEntity
import com.example.model.AppStrings
import com.example.model.Language
import com.example.ui.components.getCategoryVector
import com.example.ui.theme.SiamPrimary
import com.example.ui.theme.SiamSecondary
import com.example.ui.viewmodel.CartSummary

@Composable
fun CartScreen(
    cartItems: List<CartItemEntity>,
    cartSummary: CartSummary,
    couponMessage: String?,
    language: Language,
    onQuantityChange: (Long, Int) -> Unit,
    onRemoveItem: (Long) -> Unit,
    onApplyCoupon: (String) -> Unit,
    onRemoveCoupon: () -> Unit,
    onProceedToCheckout: () -> Unit,
    onStartShopping: () -> Unit
) {
    var couponInput by remember { mutableStateOf("") }

    if (cartItems.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.RemoveShoppingCart,
                    contentDescription = "Empty Cart",
                    tint = Color.Gray.copy(alpha = 0.6f),
                    modifier = Modifier.size(80.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = AppStrings.get("cart_empty", language),
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = AppStrings.get("cart_empty_sub", language),
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onStartShopping,
                    colors = ButtonDefaults.buttonColors(containerColor = SiamPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.testTag("start_shopping_button")
                ) {
                    Text(
                        text = AppStrings.get("start_shopping", language),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("cart_items_list"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Cart Items Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${AppStrings.get("cart_title", language)} (${cartItems.size})",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }

            // List of items
            items(cartItems, key = { it.id }) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Product Icon Thumbnail
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getCategoryVector(item.productId % 10 + 1),
                                contentDescription = item.productTitleEn,
                                tint = SiamPrimary,
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (language == Language.BANGLA) item.productTitleBn else item.productTitleEn,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "৳${item.price.toInt()}",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = SiamSecondary
                                )
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            // Quantity Increment/Decrement
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                IconButton(
                                    onClick = { onQuantityChange(item.id, item.quantity - 1) },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(imageVector = Icons.Filled.Remove, contentDescription = "Decrease", modifier = Modifier.size(14.dp))
                                }
                                Text(
                                    text = item.quantity.toString(),
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 8.dp)
                                )
                                IconButton(
                                    onClick = { onQuantityChange(item.id, item.quantity + 1) },
                                    modifier = Modifier.size(26.dp)
                                ) {
                                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Increase", modifier = Modifier.size(14.dp))
                                }
                            }
                        }

                        IconButton(
                            onClick = { onRemoveItem(item.id) },
                            modifier = Modifier.testTag("remove_cart_item_${item.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Filled.DeleteOutline,
                                contentDescription = "Remove",
                                tint = Color(0xFFEF4444)
                            )
                        }
                    }
                }
            }

            // Coupon Code Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = AppStrings.get("apply_coupon", language),
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (cartSummary.appliedCoupon != null) {
                            Surface(
                                color = Color(0xFF10B981).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Filled.Discount, contentDescription = "Coupon", tint = Color(0xFF059669))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${cartSummary.appliedCoupon.code} applied (-৳${cartSummary.discount.toInt()})",
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF059669)
                                        )
                                    }
                                    TextButton(onClick = onRemoveCoupon) {
                                        Text("Remove", color = Color(0xFFDC2626))
                                    }
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = couponInput,
                                    onValueChange = { couponInput = it },
                                    placeholder = { Text("SIAM10, EID2026") },
                                    singleLine = true,
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("coupon_input_field")
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Button(
                                    onClick = {
                                        if (couponInput.isNotBlank()) {
                                            onApplyCoupon(couponInput)
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = SiamPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("apply_coupon_btn")
                                ) {
                                    Text(if (language == Language.BANGLA) "প্রয়োগ" else "Apply")
                                }
                            }

                            if (couponMessage != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = couponMessage,
                                    color = if (couponMessage.contains("সফল") || couponMessage.contains("success")) Color(0xFF059669) else Color(0xFFDC2626),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }

            // Order Price Breakdown Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (language == Language.BANGLA) "অর্ডার সারাংশ" else "Price Details",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Subtotal
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = AppStrings.get("subtotal", language), color = Color.Gray)
                            Text(text = "৳${cartSummary.subtotal.toInt()}", fontWeight = FontWeight.Bold)
                        }

                        // Discount
                        if (cartSummary.discount > 0) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = AppStrings.get("discount", language), color = Color(0xFF059669))
                                Text(text = "-৳${cartSummary.discount.toInt()}", fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                            }
                        }

                        // Delivery Fee
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = AppStrings.get("delivery_fee", language), color = Color.Gray)
                            Text(
                                text = if (cartSummary.deliveryFee == 0.0) {
                                    if (language == Language.BANGLA) "বিনামূল্যে (Free)" else "FREE"
                                } else {
                                    "৳${cartSummary.deliveryFee.toInt()}"
                                },
                                fontWeight = FontWeight.Bold,
                                color = if (cartSummary.deliveryFee == 0.0) Color(0xFF059669) else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 10.dp))

                        // Final Total
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = AppStrings.get("total", language),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "৳${cartSummary.finalTotal.toInt()}",
                                style = MaterialTheme.typography.headlineSmall.copy(
                                    fontWeight = FontWeight.Black,
                                    color = SiamSecondary
                                )
                            )
                        }
                    }
                }
            }
        }

        // Sticky Checkout Button
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            shadowElevation = 12.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = AppStrings.get("total", language),
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                    )
                    Text(
                        text = "৳${cartSummary.finalTotal.toInt()}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Black,
                            color = SiamSecondary
                        )
                    )
                }

                Button(
                    onClick = onProceedToCheckout,
                    colors = ButtonDefaults.buttonColors(containerColor = SiamPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("proceed_to_checkout_button")
                ) {
                    Text(
                        text = AppStrings.get("checkout", language),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(imageVector = Icons.Filled.ArrowForward, contentDescription = "Next")
                }
            }
        }
    }
}
