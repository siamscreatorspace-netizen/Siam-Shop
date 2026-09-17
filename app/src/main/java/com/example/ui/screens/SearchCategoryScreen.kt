package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
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
import com.example.model.AppStrings
import com.example.model.Language
import com.example.ui.components.ProductCard
import com.example.ui.theme.SiamPrimary

@Composable
fun SearchCategoryScreen(
    categories: List<CategoryEntity>,
    products: List<ProductEntity>,
    selectedCategoryId: Long?,
    searchQuery: String,
    language: Language,
    onCategorySelect: (Long?) -> Unit,
    onSearchChange: (String) -> Unit,
    onProductClick: (Long) -> Unit,
    onAddToCart: (ProductEntity) -> Unit,
    onToggleWishlist: (ProductEntity) -> Unit,
    isProductWishlisted: (Long) -> Boolean
) {
    var sortByPriceDesc by remember { mutableStateOf<Boolean?>(null) }

    val sortedProducts = remember(products, sortByPriceDesc) {
        when (sortByPriceDesc) {
            true -> products.sortedByDescending { it.price }
            false -> products.sortedBy { it.price }
            null -> products
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("search_category_screen")
    ) {
        // Search Input
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = {
                    Text(text = AppStrings.get("search_hint", language))
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = "Search", tint = SiamPrimary)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(imageVector = Icons.Filled.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("catalog_search_bar")
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category Filter Pills
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // "All" pill
                item {
                    FilterChip(
                        selected = selectedCategoryId == null,
                        onClick = { onCategorySelect(null) },
                        label = {
                            Text(if (language == Language.BANGLA) "সকল পণ্য" else "All Products")
                        }
                    )
                }

                items(categories) { cat ->
                    FilterChip(
                        selected = selectedCategoryId == cat.id,
                        onClick = { onCategorySelect(if (selectedCategoryId == cat.id) null else cat.id) },
                        label = {
                            Text(if (language == Language.BANGLA) cat.nameBn else cat.nameEn)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Sort By Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${sortedProducts.size} ${if (language == Language.BANGLA) "টি পণ্য পাওয়া গেছে" else "products found"}",
                    style = MaterialTheme.typography.labelMedium.copy(color = Color.Gray)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AssistChip(
                        onClick = {
                            sortByPriceDesc = when (sortByPriceDesc) {
                                null -> false // low to high
                                false -> true // high to low
                                true -> null // reset
                            }
                        },
                        label = {
                            Text(
                                text = when (sortByPriceDesc) {
                                    false -> if (language == Language.BANGLA) "মূল্য: কম থেকে বেশি" else "Price: Low to High"
                                    true -> if (language == Language.BANGLA) "মূল্য: বেশি থেকে কম" else "Price: High to Low"
                                    null -> if (language == Language.BANGLA) "মূল্য সাজান" else "Sort by Price"
                                },
                                fontSize = 11.sp
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.FilterList,
                                contentDescription = "Sort",
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    )
                }
            }
        }

        // Product List / Grid
        if (sortedProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Not found",
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (language == Language.BANGLA) "কোনো পণ্য খুঁজে পাওয়া যায়নি!" else "No products found matching your search!",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (language == Language.BANGLA) "অন্য কি-ওয়ার্ড বা ক্যাটাগরি বেছে নিন" else "Try searching with different keywords or reset category",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                    )
                }
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(sortedProducts.chunked(2)) { pair ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        for (product in pair) {
                            ProductCard(
                                product = product,
                                language = language,
                                isWishlisted = isProductWishlisted(product.id),
                                onProductClick = { onProductClick(product.id) },
                                onAddToCart = { onAddToCart(product) },
                                onToggleWishlist = { onToggleWishlist(product) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (pair.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
