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
import com.example.data.local.entity.CategoryEntity
import com.example.data.local.entity.ProductEntity
import com.example.data.local.entity.SellerEntity
import com.example.model.Language
import com.example.ui.components.AddProductDialog
import com.example.ui.components.UpdateStockPriceDialog
import com.example.ui.theme.SiamPrimary
import com.example.ui.theme.SiamSecondary

@Composable
fun SellerDashboardScreen(
    currentSeller: SellerEntity?,
    myProducts: List<ProductEntity>,
    categories: List<CategoryEntity>,
    language: Language,
    onAddProduct: (ProductEntity) -> Unit,
    onUpdatePriceStock: (Long, Double, Int) -> Unit,
    onClose: () -> Unit
) {
    var showAddProductDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }

    val sellerName = currentSeller?.storeName ?: "Siam Official Flagship Store"
    val sellerId = currentSeller?.id ?: 1L
    val commissionPercent = currentSeller?.commissionPercent ?: 5.0
    val totalSales = currentSeller?.totalSalesAmount ?: 145000.0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("seller_dashboard_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Store Header Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SiamPrimary.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = sellerName,
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = if (language == Language.BANGLA) "অনুমোদিত সেলার পার্টনার" else "Verified Seller Partner",
                                style = MaterialTheme.typography.bodySmall.copy(color = SiamPrimary, fontWeight = FontWeight.SemiBold)
                            )
                        }

                        Surface(
                            color = Color(0xFF10B981).copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "ACTIVE",
                                color = Color(0xFF059669),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Earnings Card
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = if (language == Language.BANGLA) "মোট বিক্রি" else "Gross Sales", fontSize = 11.sp, color = Color.Gray)
                                Text(text = "৳${totalSales.toInt()}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = SiamSecondary)
                            }
                        }

                        // Commission Rate
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = if (language == Language.BANGLA) "কমিশন হার" else "Commission", fontSize = 11.sp, color = Color.Gray)
                                Text(text = "$commissionPercent%", fontWeight = FontWeight.Black, fontSize = 18.sp, color = SiamPrimary)
                            }
                        }

                        // Products Count
                        Card(
                            modifier = Modifier.weight(1f),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(text = if (language == Language.BANGLA) "মোট পণ্য" else "My Items", fontSize = 11.sp, color = Color.Gray)
                                Text(text = "${myProducts.size}", fontWeight = FontWeight.Black, fontSize = 18.sp)
                            }
                        }
                    }
                }
            }
        }

        // 2. Add Product Button
        item {
            Button(
                onClick = { showAddProductDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = SiamPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("seller_add_product_btn")
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (language == Language.BANGLA) "+ নতুন পণ্য আপলোড করুন" else "+ Upload New Product",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        // 3. Listed Products List
        item {
            Text(
                text = if (language == Language.BANGLA) "আমার স্টোরের পণ্য ও স্টক তালিকা" else "My Store Products & Stock",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }

        if (myProducts.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (language == Language.BANGLA) "আপনার এখনও কোনো পণ্য তালিকাভুক্ত নেই। উপরে বোতামে ক্লিক করে নতুন পণ্য আপলোড করুন।" else "No products listed yet. Click above to upload your first product.",
                            color = Color.Gray
                        )
                    }
                }
            }
        } else {
            items(myProducts) { prod ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (language == Language.BANGLA) prod.titleBn else prod.titleEn,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleSmall
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Price: ৳${prod.price.toInt()} • In Stock: ${prod.stock}",
                                style = MaterialTheme.typography.bodySmall.copy(color = SiamSecondary, fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "Category: ${prod.categoryNameEn}",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                            )
                        }

                        Button(
                            onClick = { editingProduct = prod },
                            colors = ButtonDefaults.buttonColors(containerColor = SiamPrimary.copy(alpha = 0.15f), contentColor = SiamPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("Edit Stock", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    if (showAddProductDialog) {
        AddProductDialog(
            categories = categories,
            language = language,
            sellerId = sellerId,
            sellerName = sellerName,
            onDismiss = { showAddProductDialog = false },
            onConfirm = { prod ->
                onAddProduct(prod)
                showAddProductDialog = false
            }
        )
    }

    if (editingProduct != null) {
        UpdateStockPriceDialog(
            currentPrice = editingProduct!!.price,
            currentStock = editingProduct!!.stock,
            language = language,
            onDismiss = { editingProduct = null },
            onConfirm = { p, s ->
                onUpdatePriceStock(editingProduct!!.id, p, s)
                editingProduct = null
            }
        )
    }
}
