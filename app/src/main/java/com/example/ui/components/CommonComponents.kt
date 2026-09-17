package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.entity.ProductEntity
import com.example.model.AppRole
import com.example.model.AppStrings
import com.example.model.Language
import com.example.model.OrderStatus
import com.example.ui.theme.*
import com.example.ui.viewmodel.ScreenRoute

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiamTopBar(
    title: String,
    language: Language,
    role: AppRole,
    unreadNotificationCount: Int,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onLanguageToggle: () -> Unit = {},
    onRoleClick: () -> Unit = {}
) {
    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Siam Shop Logo Badge
                Image(
                    painter = painterResource(id = R.drawable.ic_siam_logo),
                    contentDescription = "Siam Shop Logo",
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )

                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                    if (role != AppRole.CUSTOMER) {
                        Surface(
                            color = if (role == AppRole.ADMIN) Color(0xFFEF4444).copy(alpha = 0.15f) else SiamPrimary.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = if (language == Language.BANGLA) role.titleBn else role.titleEn,
                                color = if (role == AppRole.ADMIN) Color(0xFFDC2626) else SiamPrimary,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                ),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }
            }
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("top_bar_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            }
        },
        actions = {
            // Language Toggle Button (বাং / EN)
            OutlinedButton(
                onClick = onLanguageToggle,
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .padding(end = 4.dp)
                    .height(34.dp)
                    .testTag("language_toggle_button")
            ) {
                Text(
                    text = if (language == Language.BANGLA) "EN" else "বাং",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            // Notification Bell with Badge
            BadgedBox(
                badge = {
                    if (unreadNotificationCount > 0) {
                        Badge(
                            containerColor = SiamSecondary,
                            contentColor = Color.White
                        ) {
                            Text(unreadNotificationCount.toString())
                        }
                    }
                },
                modifier = Modifier.padding(end = 4.dp)
            ) {
                IconButton(
                    onClick = onNotificationClick,
                    modifier = Modifier.testTag("notification_button")
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Notifications,
                        contentDescription = "Notifications"
                    )
                }
            }

            // Switch Mode (Role) Icon
            IconButton(
                onClick = onRoleClick,
                modifier = Modifier.testTag("role_switch_button")
            ) {
                Icon(
                    imageVector = Icons.Outlined.AdminPanelSettings,
                    contentDescription = "Switch Mode",
                    tint = if (role == AppRole.ADMIN) Color(0xFFDC2626) else MaterialTheme.colorScheme.primary
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}

@Composable
fun SiamBottomBar(
    currentRoute: ScreenRoute,
    cartItemCount: Int,
    wishlistItemCount: Int,
    language: Language,
    onNavigate: (ScreenRoute) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        // Home
        NavigationBarItem(
            selected = currentRoute == ScreenRoute.HOME,
            onClick = { onNavigate(ScreenRoute.HOME) },
            icon = {
                Icon(
                    imageVector = if (currentRoute == ScreenRoute.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = {
                Text(
                    text = if (language == Language.BANGLA) "হোম" else "Home",
                    fontSize = 11.sp
                )
            },
            modifier = Modifier.testTag("nav_home")
        )

        // Categories
        NavigationBarItem(
            selected = currentRoute == ScreenRoute.CATEGORIES,
            onClick = { onNavigate(ScreenRoute.CATEGORIES) },
            icon = {
                Icon(
                    imageVector = if (currentRoute == ScreenRoute.CATEGORIES) Icons.Filled.Category else Icons.Outlined.Category,
                    contentDescription = "Categories"
                )
            },
            label = {
                Text(
                    text = if (language == Language.BANGLA) "ক্যাটাগরি" else "Categories",
                    fontSize = 11.sp
                )
            },
            modifier = Modifier.testTag("nav_categories")
        )

        // Cart with live badge
        NavigationBarItem(
            selected = currentRoute == ScreenRoute.CART,
            onClick = { onNavigate(ScreenRoute.CART) },
            icon = {
                BadgedBox(
                    badge = {
                        if (cartItemCount > 0) {
                            Badge(
                                containerColor = SiamSecondary,
                                contentColor = Color.White
                            ) {
                                Text(cartItemCount.toString())
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentRoute == ScreenRoute.CART) Icons.Filled.ShoppingCart else Icons.Outlined.ShoppingCart,
                        contentDescription = "Cart"
                    )
                }
            },
            label = {
                Text(
                    text = if (language == Language.BANGLA) "কার্ট" else "Cart",
                    fontSize = 11.sp
                )
            },
            modifier = Modifier.testTag("nav_cart")
        )

        // Wishlist
        NavigationBarItem(
            selected = currentRoute == ScreenRoute.WISHLIST,
            onClick = { onNavigate(ScreenRoute.WISHLIST) },
            icon = {
                BadgedBox(
                    badge = {
                        if (wishlistItemCount > 0) {
                            Badge(
                                containerColor = SiamAccent,
                                contentColor = Color.White
                            ) {
                                Text(wishlistItemCount.toString())
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = if (currentRoute == ScreenRoute.WISHLIST) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Wishlist"
                    )
                }
            },
            label = {
                Text(
                    text = if (language == Language.BANGLA) "পছন্দ" else "Wishlist",
                    fontSize = 11.sp
                )
            },
            modifier = Modifier.testTag("nav_wishlist")
        )

        // Account
        NavigationBarItem(
            selected = currentRoute == ScreenRoute.ACCOUNT,
            onClick = { onNavigate(ScreenRoute.ACCOUNT) },
            icon = {
                Icon(
                    imageVector = if (currentRoute == ScreenRoute.ACCOUNT) Icons.Filled.Person else Icons.Outlined.Person,
                    contentDescription = "Account"
                )
            },
            label = {
                Text(
                    text = if (language == Language.BANGLA) "অ্যাকাউন্ট" else "Account",
                    fontSize = 11.sp
                )
            },
            modifier = Modifier.testTag("nav_account")
        )
    }
}

@Composable
fun ProductCard(
    product: ProductEntity,
    language: Language,
    isWishlisted: Boolean,
    onProductClick: () -> Unit,
    onAddToCart: () -> Unit,
    onToggleWishlist: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onProductClick)
            .testTag("product_card_${product.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            // Product Visual Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                MaterialTheme.colorScheme.surfaceVariant,
                                MaterialTheme.colorScheme.surface
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Dynamic Visual Category Icon Silhouette
                val categoryIcon = getCategoryVector(product.categoryId)
                Icon(
                    imageVector = categoryIcon,
                    contentDescription = product.titleEn,
                    modifier = Modifier.size(64.dp),
                    tint = SiamPrimary.copy(alpha = 0.75f)
                )

                // Discount Badge (Top Left)
                if (product.discountPercent > 0) {
                    Surface(
                        color = SiamSecondary,
                        shape = RoundedCornerShape(topStart = 14.dp, bottomEnd = 10.dp),
                        modifier = Modifier.align(Alignment.TopStart)
                    ) {
                        Text(
                            text = "-${product.discountPercent}%",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Wishlist Icon (Top Right)
                IconButton(
                    onClick = onToggleWishlist,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(34.dp)
                        .background(Color.White.copy(alpha = 0.85f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isWishlisted) Color(0xFFEF4444) else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }

                // Flash Sale tag (Bottom Left)
                if (product.isFlashSale) {
                    Surface(
                        color = Color(0xFFDC2626),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(6.dp)
                    ) {
                        Text(
                            text = if (language == Language.BANGLA) "ফ্ল্যাশ ডিল" else "FLASH",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Info Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                // Category Name
                Text(
                    text = if (language == Language.BANGLA) product.categoryNameBn else product.categoryNameEn,
                    style = MaterialTheme.typography.labelSmall.copy(color = SiamPrimary, fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Title
                Text(
                    text = if (language == Language.BANGLA) product.titleBn else product.titleEn,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    minLines = 2
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Rating & Review Count
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Rating",
                        tint = SiamAccent,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = product.rating.toString(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "(${product.reviewCount})",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Price and Add to Cart Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "৳${product.price.toInt()}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Black,
                                color = SiamSecondary
                            )
                        )
                        if (product.originalPrice > product.price) {
                            Text(
                                text = "৳${product.originalPrice.toInt()}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.Gray,
                                    textDecoration = TextDecoration.LineThrough
                                )
                            )
                        }
                    }

                    // Quick Add to Cart Button
                    FilledIconButton(
                        onClick = onAddToCart,
                        shape = RoundedCornerShape(8.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = SiamPrimary
                        ),
                        modifier = Modifier
                            .size(34.dp)
                            .testTag("add_to_cart_${product.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AddShoppingCart,
                            contentDescription = "Add to Cart",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: OrderStatus, language: Language) {
    val (bgColor, textColor) = when (status) {
        OrderStatus.PENDING -> Pair(StatusPending.copy(alpha = 0.15f), StatusPending)
        OrderStatus.CONFIRMED -> Pair(StatusConfirmed.copy(alpha = 0.15f), StatusConfirmed)
        OrderStatus.SHIPPED -> Pair(StatusShipped.copy(alpha = 0.15f), StatusShipped)
        OrderStatus.OUT_FOR_DELIVERY -> Pair(Color(0xFF06B6D4).copy(alpha = 0.15f), Color(0xFF0891B2))
        OrderStatus.DELIVERED -> Pair(StatusDelivered.copy(alpha = 0.15f), StatusDelivered)
        OrderStatus.CANCELLED -> Pair(StatusCancelled.copy(alpha = 0.15f), StatusCancelled)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = if (language == Language.BANGLA) status.titleBn else status.titleEn,
            color = textColor,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            ),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

fun getCategoryVector(categoryId: Long): ImageVector {
    return when (categoryId) {
        1L -> Icons.Filled.Smartphone
        2L -> Icons.Filled.Checkroom
        3L -> Icons.Filled.DirectionsRun
        4L -> Icons.Filled.Watch
        5L -> Icons.Filled.Home
        6L -> Icons.Filled.Spa
        7L -> Icons.Filled.Kitchen
        8L -> Icons.Filled.Computer
        9L -> Icons.Filled.ChildCare
        10L -> Icons.Filled.LocalGroceryStore
        else -> Icons.Filled.ShoppingBag
    }
}
