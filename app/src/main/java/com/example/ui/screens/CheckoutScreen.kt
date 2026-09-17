package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.local.entity.AddressEntity
import com.example.data.local.entity.CartItemEntity
import com.example.model.AppStrings
import com.example.model.Language
import com.example.model.PaymentMethod
import com.example.ui.components.AddressDialog
import com.example.ui.theme.SiamPrimary
import com.example.ui.theme.SiamSecondary
import com.example.ui.viewmodel.CartSummary

@Composable
fun CheckoutScreen(
    cartItems: List<CartItemEntity>,
    cartSummary: CartSummary,
    addresses: List<AddressEntity>,
    isPaymentProcessing: Boolean,
    language: Language,
    onAddNewAddress: (AddressEntity) -> Unit,
    onPlaceOrder: (String, String, String, String, PaymentMethod) -> Unit,
    onBack: () -> Unit
) {
    var selectedAddressId by remember {
        mutableStateOf(addresses.firstOrNull { it.isDefault }?.id ?: addresses.firstOrNull()?.id ?: 1L)
    }
    var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.COD) }
    var showAddressDialog by remember { mutableStateOf(false) }

    // Fallback direct input if no address exists
    var manualName by remember { mutableStateOf("") }
    var manualPhone by remember { mutableStateOf("") }
    var manualAddress by remember { mutableStateOf("") }
    var manualDistrict by remember { mutableStateOf("") }

    val activeAddress = addresses.firstOrNull { it.id == selectedAddressId }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("checkout_screen"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Screen Header
            item {
                Text(
                    text = AppStrings.get("checkout", language),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                )
            }

            // 1. Delivery Address Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Filled.LocationOn, contentDescription = "Address", tint = SiamPrimary)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = AppStrings.get("delivery_address", language),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                            TextButton(
                                onClick = { showAddressDialog = true },
                                modifier = Modifier.testTag("add_address_button")
                            ) {
                                Text(
                                    text = "+ ${AppStrings.get("add_address", language)}",
                                    color = SiamPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (addresses.isNotEmpty()) {
                            addresses.forEach { addr ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { selectedAddressId = addr.id }
                                        .background(
                                            if (selectedAddressId == addr.id) SiamPrimary.copy(alpha = 0.08f) else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = selectedAddressId == addr.id,
                                        onClick = { selectedAddressId = addr.id }
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(
                                            text = "${addr.recipientName} (${addr.phone})",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium
                                        )
                                        Text(
                                            text = "${addr.fullAddress}, ${addr.district}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                                        )
                                    }
                                }
                            }
                        } else {
                            // Direct address form
                            OutlinedTextField(
                                value = manualName,
                                onValueChange = { manualName = it },
                                label = { Text("Name") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = manualPhone,
                                onValueChange = { manualPhone = it },
                                label = { Text("Phone") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            OutlinedTextField(
                                value = manualAddress,
                                onValueChange = { manualAddress = it },
                                label = { Text("Delivery Address") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            // 2. Payment Method Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.Payment, contentDescription = "Payment", tint = SiamSecondary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = AppStrings.get("payment_method", language),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        PaymentMethod.values().forEach { method ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { selectedPaymentMethod = method }
                                    .testTag("payment_method_${method.name}"),
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (selectedPaymentMethod == method) SiamPrimary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(
                                            selected = selectedPaymentMethod == method,
                                            onClick = { selectedPaymentMethod = method }
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = if (language == Language.BANGLA) method.titleBn else method.titleEn,
                                                fontWeight = FontWeight.Bold,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Text(
                                                text = if (method == PaymentMethod.COD) {
                                                    if (language == Language.BANGLA) "পণ্য হাতে পেয়ে নগদ টাকা পরিশোধ করুন" else "Pay in cash upon doorstep delivery"
                                                } else {
                                                    if (language == Language.BANGLA) "নিরাপদ ডিজিটাল পেমেন্ট গেটওয়ে" else "Instant secure automated digital gateway"
                                                },
                                                style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                                            )
                                        }
                                    }

                                    // Badge for Digital / COD
                                    Surface(
                                        color = if (method == PaymentMethod.COD) Color(0xFF10B981).copy(alpha = 0.15f) else SiamSecondary.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(4.dp)
                                    ) {
                                        Text(
                                            text = if (method == PaymentMethod.COD) "CASH" else "ONLINE",
                                            color = if (method == PaymentMethod.COD) Color(0xFF059669) else SiamSecondary,
                                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. Order Items Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "${AppStrings.get("order_summary", language)} (${cartItems.size} items)",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        cartItems.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "${if (language == Language.BANGLA) item.productTitleBn else item.productTitleEn} (x${item.quantity})",
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "৳${(item.price * item.quantity).toInt()}",
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                                )
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 10.dp))

                        // Price math
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = AppStrings.get("subtotal", language), style = MaterialTheme.typography.bodySmall)
                            Text(text = "৳${cartSummary.subtotal.toInt()}", style = MaterialTheme.typography.bodySmall)
                        }

                        if (cartSummary.discount > 0) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = AppStrings.get("discount", language), color = Color(0xFF059669), style = MaterialTheme.typography.bodySmall)
                                Text(text = "-৳${cartSummary.discount.toInt()}", color = Color(0xFF059669), style = MaterialTheme.typography.bodySmall)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = AppStrings.get("delivery_fee", language), style = MaterialTheme.typography.bodySmall)
                            Text(
                                text = if (cartSummary.deliveryFee == 0.0) "FREE" else "৳${cartSummary.deliveryFee.toInt()}",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 10.dp))

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

        // Sticky Place Order CTA Button
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            shadowElevation = 12.dp,
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Button(
                    onClick = {
                        val name = activeAddress?.recipientName ?: manualName
                        val phone = activeAddress?.phone ?: manualPhone
                        val address = activeAddress?.fullAddress ?: manualAddress
                        val district = activeAddress?.district ?: manualDistrict
                        onPlaceOrder(name, phone, address, district, selectedPaymentMethod)
                    },
                    enabled = !isPaymentProcessing,
                    colors = ButtonDefaults.buttonColors(containerColor = SiamPrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("confirm_order_button")
                ) {
                    if (isPaymentProcessing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (language == Language.BANGLA) "অর্ডার প্রক্রিয়াকরণ হচ্ছে..." else "Processing Order...",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    } else {
                        Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = "Confirm")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "${AppStrings.get("place_order", language)} • ৳${cartSummary.finalTotal.toInt()}",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }
    }

    if (showAddressDialog) {
        AddressDialog(
            language = language,
            onDismiss = { showAddressDialog = false },
            onConfirm = { newAddr ->
                onAddNewAddress(newAddr)
                showAddressDialog = false
            }
        )
    }
}
