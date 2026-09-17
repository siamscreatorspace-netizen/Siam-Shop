package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.AddressEntity
import com.example.data.local.entity.OrderEntity
import com.example.model.AppRole
import com.example.model.AppStrings
import com.example.model.Language
import com.example.ui.components.AddressDialog
import com.example.ui.components.RoleSwitcherDialog
import com.example.ui.components.StatusBadge
import com.example.ui.theme.SiamPrimary
import com.example.ui.theme.SiamSecondary

@Composable
fun AccountScreen(
    orders: List<OrderEntity>,
    addresses: List<AddressEntity>,
    currentRole: AppRole,
    language: Language,
    onRoleChange: (AppRole) -> Unit,
    onLanguageToggle: () -> Unit,
    onOrderClick: (Long) -> Unit,
    onAddNewAddress: (AddressEntity) -> Unit,
    onDeleteAddress: (Long) -> Unit,
    onOpenAdminPanel: () -> Unit,
    onOpenSellerPanel: () -> Unit,
    onOpenNotifications: () -> Unit
) {
    var showAddressDialog by remember { mutableStateOf(false) }
    var showRoleDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("account_screen"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. User Profile Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(SiamPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "User Avatar",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (language == Language.BANGLA) "সম্মানিত কাস্টমার" else "Valued Customer",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "customer@siamshop.com",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            color = SiamSecondary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "VIP Member • Siam Shop",
                                color = SiamSecondary,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    IconButton(onClick = { showRoleDialog = true }) {
                        Icon(imageVector = Icons.Filled.SwapHoriz, contentDescription = "Switch Mode", tint = SiamPrimary)
                    }
                }
            }
        }

        // 2. Multi-Role Navigation Shortcuts
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SiamPrimary.copy(alpha = 0.06f)
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (language == Language.BANGLA) "ম্যানেজমেন্ট ও ড্যাশবোর্ড" else "Portals & Management",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Admin Dashboard Card
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onOpenAdminPanel() }
                                .testTag("open_admin_panel_button"),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Icon(
                                    imageVector = Icons.Filled.AdminPanelSettings,
                                    contentDescription = "Admin",
                                    tint = Color(0xFFDC2626)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (language == Language.BANGLA) "এডমিন কন্ট্রোল" else "Admin Panel",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = if (language == Language.BANGLA) "সম্পূর্ণ মার্কেটপ্লেস" else "Marketplace Admin",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontSize = 10.sp)
                                )
                            }
                        }

                        // Seller Dashboard Card
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onOpenSellerPanel() }
                                .testTag("open_seller_panel_button"),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Icon(
                                    imageVector = Icons.Filled.Storefront,
                                    contentDescription = "Seller",
                                    tint = SiamPrimary
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (language == Language.BANGLA) "সেলার পার্টনার" else "Seller Hub",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = if (language == Language.BANGLA) "পণ্য ও সেলস" else "Products & Sales",
                                    style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontSize = 10.sp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 3. Order History Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${AppStrings.get("order_history", language)} (${orders.size})",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
        }

        if (orders.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(modifier = Modifier.padding(24.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = if (language == Language.BANGLA) "আপনার এখনও কোনো অর্ডার নেই। পছন্দের পণ্যটি এখনই অর্ডার করুন!" else "No orders placed yet. Start shopping your favorite products!",
                            style = MaterialTheme.typography.bodyMedium.copy(color = Color.Gray)
                        )
                    }
                }
            }
        } else {
            items(orders, key = { it.id }) { order ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOrderClick(order.id) }
                        .testTag("order_history_item_${order.id}"),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = order.orderNumber,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            StatusBadge(status = order.orderStatus, language = language)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = order.itemsSummary,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "৳${order.finalAmount.toInt()}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Black,
                                    color = SiamSecondary
                                )
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = AppStrings.get("track_order", language),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = SiamPrimary
                                    )
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                    contentDescription = "Track",
                                    tint = SiamPrimary,
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Saved Addresses Section
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = AppStrings.get("saved_addresses", language),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        TextButton(onClick = { showAddressDialog = true }) {
                            Text(text = "+ ${AppStrings.get("add_address", language)}", color = SiamPrimary, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    addresses.forEach { addr ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "${addr.recipientName} (${addr.tag})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                Text(text = "${addr.fullAddress}, ${addr.district}", style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray))
                            }
                            IconButton(onClick = { onDeleteAddress(addr.id) }) {
                                Icon(imageVector = Icons.Outlined.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }
        }

        // 5. App Settings & Information
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (language == Language.BANGLA) "অ্যাপ সেটিংস ও সহায়তা" else "Settings & Support",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Language Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onLanguageToggle() }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Outlined.Language, contentDescription = "Language", tint = SiamPrimary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = AppStrings.get("language", language))
                        }
                        Text(
                            text = if (language == Language.BANGLA) "বাংলা" else "English",
                            fontWeight = FontWeight.Bold,
                            color = SiamPrimary
                        )
                    }

                    Divider()

                    // Notifications Center
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onOpenNotifications() }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Outlined.Notifications, contentDescription = "Notifications", tint = SiamPrimary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = AppStrings.get("notifications", language))
                        }
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos, contentDescription = "Open", modifier = Modifier.size(14.dp), tint = Color.Gray)
                    }

                    Divider()

                    // Customer Service Hotline
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Outlined.SupportAgent, contentDescription = "Support", tint = SiamPrimary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(text = if (language == Language.BANGLA) "গ্রাহক সেবা হটলাইন" else "Customer Support")
                        }
                        Text(text = "16247 / 09612-000000", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    if (showAddressDialog) {
        AddressDialog(
            language = language,
            onDismiss = { showAddressDialog = false },
            onConfirm = { addr ->
                onAddNewAddress(addr)
                showAddressDialog = false
            }
        )
    }

    if (showRoleDialog) {
        RoleSwitcherDialog(
            currentRole = currentRole,
            language = language,
            onDismiss = { showRoleDialog = false },
            onSelectRole = { role ->
                onRoleChange(role)
                showRoleDialog = false
            }
        )
    }
}
