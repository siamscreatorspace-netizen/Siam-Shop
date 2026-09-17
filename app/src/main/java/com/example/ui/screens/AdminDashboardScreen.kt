package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.example.data.local.entity.*
import com.example.model.Language
import com.example.model.OrderStatus
import com.example.ui.components.*
import com.example.ui.theme.SiamPrimary
import com.example.ui.theme.SiamSecondary

enum class AdminTab {
    OVERVIEW,
    ORDERS,
    PRODUCTS,
    SELLERS,
    COUPONS
}

@Composable
fun AdminDashboardScreen(
    products: List<ProductEntity>,
    categories: List<CategoryEntity>,
    orders: List<OrderEntity>,
    sellers: List<SellerEntity>,
    coupons: List<CouponEntity>,
    language: Language,
    onAddProduct: (ProductEntity) -> Unit,
    onUpdatePriceStock: (Long, Double, Int) -> Unit,
    onDeleteProduct: (Long) -> Unit,
    onAddCategory: (CategoryEntity) -> Unit,
    onUpdateOrderStatus: (Long, OrderStatus, String) -> Unit,
    onApproveSeller: (Long, Boolean) -> Unit,
    onAddCoupon: (CouponEntity) -> Unit,
    onDeleteCoupon: (Long) -> Unit,
    onClose: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(AdminTab.OVERVIEW) }
    var showAddProductDialog by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showAddCouponDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<ProductEntity?>(null) }

    val totalRevenue = remember(orders) { orders.sumOf { it.finalAmount } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_dashboard_screen")
    ) {
        // Dashboard Tab Selector
        ScrollableTabRow(
            selectedTabIndex = selectedTab.ordinal,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            AdminTab.values().forEach { tab ->
                Tab(
                    selected = selectedTab == tab,
                    onClick = { selectedTab = tab },
                    text = {
                        Text(
                            text = when (tab) {
                                AdminTab.OVERVIEW -> if (language == Language.BANGLA) "সারসংক্ষেপ" else "Overview"
                                AdminTab.ORDERS -> if (language == Language.BANGLA) "অর্ডারসমূহ (${orders.size})" else "Orders (${orders.size})"
                                AdminTab.PRODUCTS -> if (language == Language.BANGLA) "পণ্য (${products.size})" else "Products (${products.size})"
                                AdminTab.SELLERS -> if (language == Language.BANGLA) "সেলার (${sellers.size})" else "Sellers (${sellers.size})"
                                AdminTab.COUPONS -> if (language == Language.BANGLA) "কুপন (${coupons.size})" else "Coupons (${coupons.size})"
                            },
                            fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Tab Content
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            when (selectedTab) {
                AdminTab.OVERVIEW -> {
                    // 1. Metric Cards Grid
                    item {
                        Text(
                            text = if (language == Language.BANGLA) "মার্কেটপ্লেস পরিসংখ্যান" else "Marketplace Analytics",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Total Sales Card
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = SiamPrimary.copy(alpha = 0.1f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = if (language == Language.BANGLA) "মোট বিক্রি" else "Total Revenue",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                                    )
                                    Text(
                                        text = "৳${totalRevenue.toInt()}",
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            color = SiamPrimary
                                        )
                                    )
                                }
                            }

                            // Total Orders Card
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = SiamSecondary.copy(alpha = 0.1f)),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = if (language == Language.BANGLA) "মোট অর্ডার" else "Total Orders",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                                    )
                                    Text(
                                        text = "${orders.size}",
                                        style = MaterialTheme.typography.headlineSmall.copy(
                                            fontWeight = FontWeight.Black,
                                            color = SiamSecondary
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Products Count
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = if (language == Language.BANGLA) "সক্রিয় পণ্য" else "Active Products",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                                    )
                                    Text(
                                        text = "${products.size}",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }

                            // Sellers Count
                            Card(
                                modifier = Modifier.weight(1f),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = if (language == Language.BANGLA) "রেজিস্টার্ড বিক্রেতা" else "Partners / Sellers",
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                                    )
                                    Text(
                                        text = "${sellers.size}",
                                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                    )
                                }
                            }
                        }
                    }

                    // 2. Fast Creation Shortcuts
                    item {
                        Text(
                            text = if (language == Language.BANGLA) "কুইক অ্যাকশন" else "Quick Actions",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { showAddProductDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = SiamPrimary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (language == Language.BANGLA) "পণ্য যোগ" else "+ Product", fontSize = 12.sp)
                            }

                            Button(
                                onClick = { showAddCategoryDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = SiamSecondary),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Filled.Category, contentDescription = "Category")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (language == Language.BANGLA) "ক্যাটাগরি" else "+ Category", fontSize = 12.sp)
                            }

                            Button(
                                onClick = { showAddCouponDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF059669)),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(imageVector = Icons.Filled.Discount, contentDescription = "Coupon")
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (language == Language.BANGLA) "কুপন" else "+ Coupon", fontSize = 12.sp)
                            }
                        }
                    }

                    // 3. Recent Orders preview
                    item {
                        Text(
                            text = if (language == Language.BANGLA) "সাম্প্রতিক অর্ডারসমূহ" else "Recent Orders",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    items(orders.take(5)) { ord ->
                        AdminOrderCard(ord, language, onUpdateOrderStatus)
                    }
                }

                AdminTab.ORDERS -> {
                    items(orders) { ord ->
                        AdminOrderCard(ord, language, onUpdateOrderStatus)
                    }
                }

                AdminTab.PRODUCTS -> {
                    item {
                        Button(
                            onClick = { showAddProductDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = SiamPrimary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (language == Language.BANGLA) "+ নতুন পণ্য যোগ করুন" else "+ Add New Product")
                        }
                    }

                    items(products) { prod ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (language == Language.BANGLA) prod.titleBn else prod.titleEn,
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = "${prod.categoryNameEn} • Stock: ${prod.stock} • ৳${prod.price.toInt()}",
                                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                                    )
                                }

                                IconButton(onClick = { editingProduct = prod }) {
                                    Icon(imageVector = Icons.Filled.Edit, contentDescription = "Edit", tint = SiamPrimary)
                                }

                                IconButton(onClick = { onDeleteProduct(prod.id) }) {
                                    Icon(imageVector = Icons.Filled.DeleteOutline, contentDescription = "Delete", tint = Color(0xFFEF4444))
                                }
                            }
                        }
                    }
                }

                AdminTab.SELLERS -> {
                    items(sellers) { sel ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(text = sel.storeName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                        Text(text = "Owner: ${sel.ownerName} (${sel.phone})", style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
                                    }
                                    Switch(
                                        checked = sel.isApproved,
                                        onCheckedChange = { onApproveSeller(sel.id, it) }
                                    )
                                }

                                Divider(modifier = Modifier.padding(vertical = 8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Total Sales: ৳${sel.totalSalesAmount.toInt()}", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Text(text = "Commission: ${sel.commissionPercent}%", fontSize = 12.sp, color = SiamPrimary, fontWeight = FontWeight.Bold)
                                    Text(text = "Orders: ${sel.totalOrdersCount}", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }

                AdminTab.COUPONS -> {
                    item {
                        Button(
                            onClick = { showAddCouponDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = SiamPrimary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(if (language == Language.BANGLA) "+ নতুন কুপন তৈরি করুন" else "+ Create New Coupon")
                        }
                    }

                    items(coupons) { cp ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = cp.code, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium, color = SiamPrimary)
                                    Text(text = cp.descriptionEn, style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
                                    Text(text = "Used: ${cp.usageCount} times", fontSize = 11.sp, color = SiamSecondary)
                                }
                                IconButton(onClick = { onDeleteCoupon(cp.id) }) {
                                    Icon(imageVector = Icons.Filled.DeleteOutline, contentDescription = "Delete", tint = Color(0xFFEF4444))
                                }
                            }
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
            onDismiss = { showAddProductDialog = false },
            onConfirm = { prod ->
                onAddProduct(prod)
                showAddProductDialog = false
            }
        )
    }

    if (showAddCategoryDialog) {
        AddCategoryDialog(
            language = language,
            onDismiss = { showAddCategoryDialog = false },
            onConfirm = { cat ->
                onAddCategory(cat)
                showAddCategoryDialog = false
            }
        )
    }

    if (showAddCouponDialog) {
        AddCouponDialog(
            language = language,
            onDismiss = { showAddCouponDialog = false },
            onConfirm = { cp ->
                onAddCoupon(cp)
                showAddCouponDialog = false
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

@Composable
fun AdminOrderCard(
    order: OrderEntity,
    language: Language,
    onUpdateOrderStatus: (Long, OrderStatus, String) -> Unit
) {
    var expandedStatusMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = order.orderNumber, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                StatusBadge(status = order.orderStatus, language = language)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Customer: ${order.customerName} (${order.customerPhone})", style = MaterialTheme.typography.bodySmall)
            Text(text = "Items: ${order.itemsSummary}", style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
            Text(text = "Amount: ৳${order.finalAmount.toInt()} (${order.paymentMethod.name})", style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold))

            Spacer(modifier = Modifier.height(8.dp))

            // Status Changer Dropdown
            Box {
                OutlinedButton(
                    onClick = { expandedStatusMenu = true },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Text("Change Status: ${order.orderStatus.titleEn}", fontSize = 11.sp)
                }

                DropdownMenu(
                    expanded = expandedStatusMenu,
                    onDismissRequest = { expandedStatusMenu = false }
                ) {
                    OrderStatus.values().forEach { st ->
                        DropdownMenuItem(
                            text = { Text(st.titleEn) },
                            onClick = {
                                onUpdateOrderStatus(order.id, st, "Status updated to ${st.titleEn} by Admin.")
                                expandedStatusMenu = false
                            }
                        )
                    }
                }
            }
        }
    }
}
