package com.example.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.*
import com.example.model.AppRole
import com.example.model.Language
import com.example.ui.theme.SiamAccent
import com.example.ui.theme.SiamPrimary

@Composable
fun AddProductDialog(
    categories: List<CategoryEntity>,
    language: Language,
    sellerId: Long = 1,
    sellerName: String = "Siam Official Flagship Store",
    onDismiss: () -> Unit,
    onConfirm: (ProductEntity) -> Unit
) {
    var titleEn by remember { mutableStateOf("") }
    var titleBn by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var originalPriceText by remember { mutableStateOf("") }
    var stockText by remember { mutableStateOf("25") }
    var descriptionEn by remember { mutableStateOf("") }
    var descriptionBn by remember { mutableStateOf("") }
    var specs by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(categories.firstOrNull()) }
    var isFlashSale by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = if (language == Language.BANGLA) "নতুন পণ্য যোগ করুন" else "Add New Product",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = titleEn,
                    onValueChange = { titleEn = it },
                    label = { Text("Product Title (English)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_product_title_en")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = titleBn,
                    onValueChange = { titleBn = it },
                    label = { Text("পণ্যের নাম (বাংলা)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_product_title_bn")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category selector
                Text(
                    text = if (language == Language.BANGLA) "ক্যাটাগরি নির্বাচন করুন" else "Select Category",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.take(4).forEach { cat ->
                        FilterChip(
                            selected = selectedCategory?.id == cat.id,
                            onClick = { selectedCategory = cat },
                            label = { Text(if (language == Language.BANGLA) cat.nameBn else cat.nameEn) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = priceText,
                        onValueChange = { priceText = it },
                        label = { Text(if (language == Language.BANGLA) "বিক্রয় মূল্য (৳)" else "Sale Price (৳)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_product_price")
                    )
                    OutlinedTextField(
                        value = originalPriceText,
                        onValueChange = { originalPriceText = it },
                        label = { Text(if (language == Language.BANGLA) "আসল মূল্য (৳)" else "Regular Price (৳)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_product_orig_price")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = stockText,
                    onValueChange = { stockText = it },
                    label = { Text(if (language == Language.BANGLA) "স্টক সংখ্যা" else "Stock Quantity") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_product_stock")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = descriptionBn,
                    onValueChange = { descriptionBn = it },
                    label = { Text(if (language == Language.BANGLA) "পণ্যের বিবরণ (বাংলা)" else "Description (Bengali)") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = specs,
                    onValueChange = { specs = it },
                    label = { Text("Specifications (e.g. Brand: Siam, Warranty: 1 Yr)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isFlashSale,
                        onCheckedChange = { isFlashSale = it }
                    )
                    Text(text = if (language == Language.BANGLA) "ফ্ল্যাশ সেলে অন্তর্ভুক্ত করুন" else "Feature in Flash Sale")
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(if (language == Language.BANGLA) "বাতিল" else "Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val price = priceText.toDoubleOrNull() ?: 100.0
                            val origPrice = originalPriceText.toDoubleOrNull() ?: price
                            val stock = stockText.toIntOrNull() ?: 10
                            val cat = selectedCategory ?: categories.firstOrNull()
                            val discountPct = if (origPrice > price) (((origPrice - price) / origPrice) * 100).toInt() else 0

                            val product = ProductEntity(
                                titleEn = titleEn.ifBlank { "New Product" },
                                titleBn = titleBn.ifBlank { titleEn.ifBlank { "নতুন পণ্য" } },
                                descriptionEn = descriptionEn.ifBlank { "High quality product from Siam Shop." },
                                descriptionBn = descriptionBn.ifBlank { "সিয়াম শপের সেরা মানের পণ্য।" },
                                categoryId = cat?.id ?: 1L,
                                categoryNameEn = cat?.nameEn ?: "General",
                                categoryNameBn = cat?.nameBn ?: "সাধারণ",
                                price = price,
                                originalPrice = origPrice,
                                discountPercent = discountPct,
                                stock = stock,
                                sellerId = sellerId,
                                sellerName = sellerName,
                                isFlashSale = isFlashSale,
                                isFeatured = true,
                                specifications = specs
                            )
                            onConfirm(product)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SiamPrimary),
                        modifier = Modifier.testTag("submit_product_button")
                    ) {
                        Text(if (language == Language.BANGLA) "পণ্য যোগ করুন" else "Add Product")
                    }
                }
            }
        }
    }
}

@Composable
fun AddCategoryDialog(
    language: Language,
    onDismiss: () -> Unit,
    onConfirm: (CategoryEntity) -> Unit
) {
    var nameEn by remember { mutableStateOf("") }
    var nameBn by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (language == Language.BANGLA) "নতুন ক্যাটাগরি যোগ করুন" else "Add New Category")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = nameEn,
                    onValueChange = { nameEn = it },
                    label = { Text("Category Name (English)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = nameBn,
                    onValueChange = { nameBn = it },
                    label = { Text("ক্যাটাগরির নাম (বাংলা)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (nameEn.isNotBlank() || nameBn.isNotBlank()) {
                        onConfirm(
                            CategoryEntity(
                                nameEn = nameEn.ifBlank { nameBn },
                                nameBn = nameBn.ifBlank { nameEn },
                                iconKey = "category"
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SiamPrimary)
            ) {
                Text(if (language == Language.BANGLA) "যোগ করুন" else "Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (language == Language.BANGLA) "বাতিল" else "Cancel")
            }
        }
    )
}

@Composable
fun AddCouponDialog(
    language: Language,
    onDismiss: () -> Unit,
    onConfirm: (CouponEntity) -> Unit
) {
    var code by remember { mutableStateOf("") }
    var discountPercentText by remember { mutableStateOf("10") }
    var minSpendText by remember { mutableStateOf("500") }
    var maxDiscountText by remember { mutableStateOf("500") }
    var descBn by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (language == Language.BANGLA) "নতুন কুপন কোড তৈরি করুন" else "Create New Coupon")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it.uppercase() },
                    label = { Text("Coupon Code (e.g. MEGA20)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = discountPercentText,
                    onValueChange = { discountPercentText = it },
                    label = { Text("Discount Percentage (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = minSpendText,
                    onValueChange = { minSpendText = it },
                    label = { Text("Minimum Spend (৳)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = maxDiscountText,
                    onValueChange = { maxDiscountText = it },
                    label = { Text("Maximum Discount Limit (৳)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (code.isNotBlank()) {
                        onConfirm(
                            CouponEntity(
                                code = code.trim(),
                                discountPercent = discountPercentText.toIntOrNull() ?: 10,
                                minSpend = minSpendText.toDoubleOrNull() ?: 500.0,
                                maxDiscount = maxDiscountText.toDoubleOrNull() ?: 500.0,
                                descriptionEn = "${discountPercentText}% discount above ৳${minSpendText}",
                                descriptionBn = "৳${minSpendText} টাকার অর্ডারে ${discountPercentText}% ছাড়"
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SiamPrimary)
            ) {
                Text(if (language == Language.BANGLA) "সংরক্ষণ করুন" else "Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (language == Language.BANGLA) "বাতিল" else "Cancel")
            }
        }
    )
}

@Composable
fun UpdateStockPriceDialog(
    currentPrice: Double,
    currentStock: Int,
    language: Language,
    onDismiss: () -> Unit,
    onConfirm: (Double, Int) -> Unit
) {
    var priceText by remember { mutableStateOf(currentPrice.toInt().toString()) }
    var stockText by remember { mutableStateOf(currentStock.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (language == Language.BANGLA) "মূল্য ও স্টক পরিবর্তন" else "Update Price & Stock")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text(if (language == Language.BANGLA) "নতুন মূল্য (৳)" else "New Price (৳)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = stockText,
                    onValueChange = { stockText = it },
                    label = { Text(if (language == Language.BANGLA) "বর্তমান স্টক" else "Current Stock") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val p = priceText.toDoubleOrNull() ?: currentPrice
                    val s = stockText.toIntOrNull() ?: currentStock
                    onConfirm(p, s)
                },
                colors = ButtonDefaults.buttonColors(containerColor = SiamPrimary)
            ) {
                Text(if (language == Language.BANGLA) "আপডেট করুন" else "Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (language == Language.BANGLA) "বাতিল" else "Cancel")
            }
        }
    )
}

@Composable
fun AddressDialog(
    language: Language,
    onDismiss: () -> Unit,
    onConfirm: (AddressEntity) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var district by remember { mutableStateOf("Dhaka") }
    var fullAddress by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("Home") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (language == Language.BANGLA) "নতুন ডেলিভারি ঠিকানা" else "Add Delivery Address")
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (language == Language.BANGLA) "প্রাপকের নাম" else "Recipient Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text(if (language == Language.BANGLA) "মোবাইল নম্বর" else "Phone Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = district,
                    onValueChange = { district = it },
                    label = { Text(if (language == Language.BANGLA) "জেলা / শহর" else "District / City") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = fullAddress,
                    onValueChange = { fullAddress = it },
                    label = { Text(if (language == Language.BANGLA) "সম্পূর্ণ ঠিকানা (বাসা, রোড, এলাকা)" else "Full Address (House, Road, Area)") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank() && fullAddress.isNotBlank()) {
                        onConfirm(
                            AddressEntity(
                                recipientName = name,
                                phone = phone,
                                district = district,
                                fullAddress = fullAddress,
                                tag = tag,
                                isDefault = true
                            )
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SiamPrimary)
            ) {
                Text(if (language == Language.BANGLA) "সংরক্ষণ করুন" else "Save Address")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (language == Language.BANGLA) "বাতিল" else "Cancel")
            }
        }
    )
}

@Composable
fun ReviewDialog(
    language: Language,
    onDismiss: () -> Unit,
    onSubmit: (String, Int, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (language == Language.BANGLA) "পণ্য সম্পর্কে মতামত দিন" else "Write a Review")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Star rating selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    (1..5).forEach { star ->
                        IconButton(onClick = { rating = star }) {
                            Icon(
                                imageVector = if (star <= rating) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                contentDescription = "$star Stars",
                                tint = SiamAccent,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (language == Language.BANGLA) "আপনার নাম" else "Your Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text(if (language == Language.BANGLA) "আপনার অভিজ্ঞতা বা মন্তব্য লিখুন" else "Your Feedback / Experience") },
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (comment.isNotBlank()) {
                        onSubmit(name, rating, comment)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = SiamPrimary)
            ) {
                Text(if (language == Language.BANGLA) "রিভিউ জমা দিন" else "Submit Review")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (language == Language.BANGLA) "বাতিল" else "Cancel")
            }
        }
    )
}

@Composable
fun RoleSwitcherDialog(
    currentRole: AppRole,
    language: Language,
    onDismiss: () -> Unit,
    onSelectRole: (AppRole) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(if (language == Language.BANGLA) "অ্যাপ মোড নির্বাচন করুন" else "Switch Active View Mode")
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AppRole.values().forEach { role ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectRole(role) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (currentRole == role) SiamPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = if (language == Language.BANGLA) role.titleBn else role.titleEn,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (currentRole == role) SiamPrimary else MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Text(
                                    text = when (role) {
                                        AppRole.CUSTOMER -> if (language == Language.BANGLA) "কেনাকাটা, কার্ট, অর্ডার ট্র্যাকিং" else "Browse, Add to Cart, Buy & Track Orders"
                                        AppRole.SELLER -> if (language == Language.BANGLA) "পণ্য আপলোড, স্টক ও সেলস ড্যাশবোর্ড" else "Manage Products, Stock, Sales & Commission"
                                        AppRole.ADMIN -> if (language == Language.BANGLA) "মার্কেটপ্লেস কন্ট্রোল, অর্ডার, কুপন ও রিপোর্ট" else "Full Marketplace Control, Analytics & Banners"
                                    },
                                    style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                                )
                            }
                            RadioButton(
                                selected = currentRole == role,
                                onClick = { onSelectRole(role) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(if (language == Language.BANGLA) "বন্ধ করুন" else "Close")
            }
        }
    )
}
