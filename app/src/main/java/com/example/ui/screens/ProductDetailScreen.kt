package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ProductEntity
import com.example.data.local.entity.ReviewEntity
import com.example.model.AppStrings
import com.example.model.Language
import com.example.ui.components.ReviewDialog
import com.example.ui.components.getCategoryVector
import com.example.ui.theme.SiamAccent
import com.example.ui.theme.SiamPrimary
import com.example.ui.theme.SiamSecondary

@Composable
fun ProductDetailScreen(
    product: ProductEntity?,
    reviews: List<ReviewEntity>,
    isWishlisted: Boolean,
    language: Language,
    onAddToCart: (ProductEntity, Int) -> Unit,
    onBuyNow: (ProductEntity, Int) -> Unit,
    onToggleWishlist: (ProductEntity) -> Unit,
    onSubmitReview: (Long, String, Int, String) -> Unit,
    onBack: () -> Unit
) {
    if (product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = SiamPrimary)
        }
        return
    }

    var selectedQuantity by remember { mutableStateOf(1) }
    var showReviewDialog by remember { mutableStateOf(false) }
    var selectedImageIndex by remember { mutableStateOf(0) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .testTag("product_detail_screen"),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            // 1. Image Gallery / Hero Box
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
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
                    val icon = getCategoryVector(product.categoryId)
                    Icon(
                        imageVector = icon,
                        contentDescription = product.titleEn,
                        modifier = Modifier.size(140.dp),
                        tint = SiamPrimary.copy(alpha = 0.85f)
                    )

                    // Discount badge
                    if (product.discountPercent > 0) {
                        Surface(
                            color = SiamSecondary,
                            shape = RoundedCornerShape(bottomEnd = 12.dp),
                            modifier = Modifier.align(Alignment.TopStart)
                        ) {
                            Text(
                                text = "-${product.discountPercent}% OFF",
                                color = Color.White,
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }

                    // Wishlist floating button
                    IconButton(
                        onClick = { onToggleWishlist(product) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(16.dp)
                            .size(42.dp)
                            .background(Color.White.copy(alpha = 0.9f), CircleShape)
                            .testTag("detail_wishlist_button")
                    ) {
                        Icon(
                            imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isWishlisted) Color(0xFFEF4444) else Color.Gray
                        )
                    }
                }
            }

            // 2. Product Gallery Thumbnails
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    (0..2).forEach { index ->
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = if (selectedImageIndex == index) 2.dp else 1.dp,
                                    color = if (selectedImageIndex == index) SiamPrimary else Color.LightGray,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedImageIndex = index }
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = getCategoryVector(product.categoryId),
                                contentDescription = "Thumb $index",
                                modifier = Modifier.size(24.dp),
                                tint = SiamPrimary
                            )
                        }
                    }
                }
            }

            // 3. Title, Category, Rating & Price Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Category Pill
                        Surface(
                            color = SiamPrimary.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = if (language == Language.BANGLA) product.categoryNameBn else product.categoryNameEn,
                                color = SiamPrimary,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Title
                        Text(
                            text = if (language == Language.BANGLA) product.titleBn else product.titleEn,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Rating & Reviews row
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = "Rating",
                                tint = SiamAccent,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "${product.rating}",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "• ${product.reviewCount} ${if (language == Language.BANGLA) "রিভিউ" else "reviews"}",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            // Stock Status
                            Surface(
                                color = if (product.stock > 0) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFEF4444).copy(alpha = 0.15f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = if (product.stock > 0) {
                                        if (language == Language.BANGLA) "স্টকে আছে (${product.stock} টি)" else "In Stock (${product.stock} left)"
                                    } else {
                                        if (language == Language.BANGLA) "স্টক শেষ" else "Out of Stock"
                                    },
                                    color = if (product.stock > 0) Color(0xFF059669) else Color(0xFFDC2626),
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(12.dp))

                        // Price and Quantity Selector Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "৳${product.price.toInt()}",
                                        style = MaterialTheme.typography.headlineMedium.copy(
                                            fontWeight = FontWeight.Black,
                                            color = SiamSecondary
                                        )
                                    )
                                    if (product.originalPrice > product.price) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "৳${product.originalPrice.toInt()}",
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                color = Color.Gray,
                                                textDecoration = TextDecoration.LineThrough
                                            )
                                        )
                                    }
                                }
                            }

                            // Quantity Selector
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                IconButton(
                                    onClick = { if (selectedQuantity > 1) selectedQuantity-- },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(imageVector = Icons.Filled.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                                }
                                Text(
                                    text = selectedQuantity.toString(),
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    modifier = Modifier.padding(horizontal = 12.dp)
                                )
                                IconButton(
                                    onClick = { if (selectedQuantity < product.stock) selectedQuantity++ },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(imageVector = Icons.Filled.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }

            // 4. Seller Information Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(SiamPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Filled.Storefront, contentDescription = "Store", tint = SiamPrimary)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (language == Language.BANGLA) "বিক্রেতা" else "Sold by",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray)
                            )
                            Text(
                                text = product.sellerName,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                        }
                        Surface(
                            color = SiamPrimary,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Filled.CheckCircle, contentDescription = "Verified", tint = Color.White, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = "Verified", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 5. Specifications & Description
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = AppStrings.get("specifications", language),
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (product.specifications.isNotBlank()) {
                            Text(
                                text = product.specifications,
                                style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        Text(
                            text = if (language == Language.BANGLA) "বিস্তারিত বিবরণ" else "Full Description",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (language == Language.BANGLA) product.descriptionBn else product.descriptionEn,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                lineHeight = 22.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }
                }
            }

            // 6. Customer Reviews Section
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
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
                                text = AppStrings.get("reviews", language),
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            TextButton(
                                onClick = { showReviewDialog = true },
                                modifier = Modifier.testTag("write_review_button")
                            ) {
                                Icon(imageVector = Icons.Outlined.RateReview, contentDescription = "Write", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (language == Language.BANGLA) "মতামত দিন" else "Write Review")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (reviews.isEmpty()) {
                            Text(
                                text = if (language == Language.BANGLA) "এখনও কোনো রিভিউ দেওয়া হয়নি। প্রথম রিভিউটি দিন!" else "No reviews yet. Be the first to share your feedback!",
                                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray),
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                        } else {
                            reviews.forEach { rev ->
                                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = rev.customerName,
                                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                        )
                                        Row {
                                            (1..rev.rating).forEach {
                                                Icon(
                                                    imageVector = Icons.Filled.Star,
                                                    contentDescription = "Star",
                                                    tint = SiamAccent,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = rev.comment,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                    Text(
                                        text = rev.dateString,
                                        style = MaterialTheme.typography.labelSmall.copy(color = Color.Gray, fontSize = 10.sp)
                                    )
                                    Divider(modifier = Modifier.padding(top = 8.dp))
                                }
                            }
                        }
                    }
                }
            }
        }

        // Sticky Bottom CTA Bar (Add to Cart & Buy Now)
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
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Add to Cart Button
                OutlinedButton(
                    onClick = { onAddToCart(product, selectedQuantity) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = SiamPrimary),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("detail_add_to_cart_button")
                ) {
                    Icon(imageVector = Icons.Filled.AddShoppingCart, contentDescription = "Cart", modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = AppStrings.get("add_to_cart", language),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }

                // Buy Now Button
                Button(
                    onClick = { onBuyNow(product, selectedQuantity) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SiamSecondary),
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp)
                        .testTag("detail_buy_now_button")
                ) {
                    Icon(imageVector = Icons.Filled.FlashOn, contentDescription = "Buy Now", modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = AppStrings.get("buy_now", language),
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }
    }

    if (showReviewDialog) {
        ReviewDialog(
            language = language,
            onDismiss = { showReviewDialog = false },
            onSubmit = { name, rating, comment ->
                onSubmitReview(product.id, name, rating, comment)
                showReviewDialog = false
            }
        )
    }
}
