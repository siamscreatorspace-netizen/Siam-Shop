package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.data.local.SiamShopDatabase
import com.example.data.repository.SiamShopRepository
import com.example.model.AppRole
import com.example.model.AppStrings
import com.example.model.Language
import com.example.ui.components.RoleSwitcherDialog
import com.example.ui.components.SiamBottomBar
import com.example.ui.components.SiamTopBar
import com.example.ui.screens.*
import com.example.ui.theme.SiamShopTheme
import com.example.ui.viewmodel.ScreenRoute
import com.example.ui.viewmodel.SiamShopViewModel
import com.example.ui.viewmodel.SiamShopViewModelFactory

class MainActivity : ComponentActivity() {

    private val database by lazy { SiamShopDatabase.getDatabase(this, lifecycleScope) }
    private val repository by lazy { SiamShopRepository(database) }
    private val viewModel: SiamShopViewModel by viewModels { SiamShopViewModelFactory(repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SiamShopTheme {
                SiamShopApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SiamShopApp(viewModel: SiamShopViewModel) {
    val currentRole by viewModel.currentRole.collectAsState()
    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val currentRoute by viewModel.currentRoute.collectAsState()

    val banners by viewModel.allBanners.collectAsState()
    val categories by viewModel.allCategories.collectAsState()
    val flashSaleProducts by viewModel.flashSaleProducts.collectAsState()
    val featuredProducts by viewModel.featuredProducts.collectAsState()
    val displayedProducts by viewModel.displayedProducts.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()

    val cartItems by viewModel.cartItems.collectAsState()
    val cartSummary by viewModel.cartSummary.collectAsState()
    val couponMessage by viewModel.couponMessage.collectAsState()

    val wishlistItems by viewModel.wishlistItems.collectAsState()
    val orders by viewModel.allOrders.collectAsState()
    val addresses by viewModel.allAddresses.collectAsState()
    val coupons by viewModel.allCoupons.collectAsState()
    val sellers by viewModel.allSellers.collectAsState()

    val notifications by viewModel.notifications.collectAsState()
    val unreadNotificationCount by viewModel.unreadNotificationsCount.collectAsState()

    val selectedProduct by viewModel.currentProduct.collectAsState()
    val productReviews by viewModel.currentProductReviews.collectAsState()
    val selectedOrder by viewModel.currentOrder.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsState()
    val isPaymentProcessing by viewModel.isPaymentProcessing.collectAsState()

    var showRoleModal by remember { mutableStateOf(false) }

    // Intercept back button if not on HOME
    BackHandler(enabled = currentRoute != ScreenRoute.HOME) {
        viewModel.navigateBack()
    }

    val topBarTitle = when (currentRoute) {
        ScreenRoute.HOME -> AppStrings.get("app_name", currentLanguage)
        ScreenRoute.CATEGORIES -> AppStrings.get("all_categories", currentLanguage)
        ScreenRoute.CART -> AppStrings.get("cart_title", currentLanguage)
        ScreenRoute.WISHLIST -> AppStrings.get("wishlist_title", currentLanguage)
        ScreenRoute.ACCOUNT -> AppStrings.get("account", currentLanguage)
        ScreenRoute.PRODUCT_DETAIL -> if (currentLanguage == Language.BANGLA) "পণ্যের বিবরণ" else "Product Details"
        ScreenRoute.CHECKOUT -> AppStrings.get("checkout", currentLanguage)
        ScreenRoute.ORDER_TRACKING -> AppStrings.get("track_order", currentLanguage)
        ScreenRoute.NOTIFICATIONS -> AppStrings.get("notifications", currentLanguage)
        ScreenRoute.ADMIN_PANEL -> if (currentLanguage == Language.BANGLA) "এডমিন ড্যাশবোর্ড" else "Admin Dashboard"
        ScreenRoute.SELLER_PANEL -> if (currentLanguage == Language.BANGLA) "সেলার ড্যাশবোর্ড" else "Seller Hub"
    }

    val showBackButton = currentRoute in listOf(
        ScreenRoute.PRODUCT_DETAIL,
        ScreenRoute.CHECKOUT,
        ScreenRoute.ORDER_TRACKING,
        ScreenRoute.NOTIFICATIONS,
        ScreenRoute.ADMIN_PANEL,
        ScreenRoute.SELLER_PANEL
    )

    val showBottomBar = currentRoute in listOf(
        ScreenRoute.HOME,
        ScreenRoute.CATEGORIES,
        ScreenRoute.CART,
        ScreenRoute.WISHLIST,
        ScreenRoute.ACCOUNT
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            SiamTopBar(
                title = topBarTitle,
                language = currentLanguage,
                role = currentRole,
                unreadNotificationCount = unreadNotificationCount,
                showBackButton = showBackButton,
                onBackClick = { viewModel.navigateBack() },
                onNotificationClick = { viewModel.navigateTo(ScreenRoute.NOTIFICATIONS) },
                onLanguageToggle = { viewModel.toggleLanguage() },
                onRoleClick = { showRoleModal = true }
            )
        },
        bottomBar = {
            if (showBottomBar) {
                SiamBottomBar(
                    currentRoute = currentRoute,
                    cartItemCount = cartItems.sumOf { it.quantity },
                    wishlistItemCount = wishlistItems.size,
                    language = currentLanguage,
                    onNavigate = { route -> viewModel.navigateTo(route) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentRoute) {
                ScreenRoute.HOME -> {
                    HomeScreen(
                        banners = banners,
                        categories = categories,
                        flashSaleProducts = flashSaleProducts,
                        featuredProducts = featuredProducts,
                        searchQuery = searchQuery,
                        language = currentLanguage,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onSearchSubmit = { viewModel.navigateTo(ScreenRoute.CATEGORIES) },
                        onCategoryClick = { catId ->
                            viewModel.selectCategory(catId)
                            viewModel.navigateTo(ScreenRoute.CATEGORIES)
                        },
                        onProductClick = { prodId -> viewModel.selectProduct(prodId) },
                        onAddToCart = { prod -> viewModel.addToCart(prod) },
                        onToggleWishlist = { prod -> viewModel.toggleWishlist(prod) },
                        isProductWishlisted = { id -> viewModel.isProductWishlisted(id) },
                        onViewAllProducts = {
                            viewModel.selectCategory(null)
                            viewModel.navigateTo(ScreenRoute.CATEGORIES)
                        }
                    )
                }

                ScreenRoute.CATEGORIES -> {
                    SearchCategoryScreen(
                        categories = categories,
                        products = displayedProducts,
                        selectedCategoryId = selectedCategoryId,
                        searchQuery = searchQuery,
                        language = currentLanguage,
                        onCategorySelect = { viewModel.selectCategory(it) },
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        onProductClick = { prodId -> viewModel.selectProduct(prodId) },
                        onAddToCart = { prod -> viewModel.addToCart(prod) },
                        onToggleWishlist = { prod -> viewModel.toggleWishlist(prod) },
                        isProductWishlisted = { id -> viewModel.isProductWishlisted(id) }
                    )
                }

                ScreenRoute.PRODUCT_DETAIL -> {
                    ProductDetailScreen(
                        product = selectedProduct,
                        reviews = productReviews,
                        isWishlisted = selectedProduct?.let { viewModel.isProductWishlisted(it.id) } ?: false,
                        language = currentLanguage,
                        onAddToCart = { prod, qty -> viewModel.addToCart(prod, qty) },
                        onBuyNow = { prod, qty ->
                            viewModel.addToCart(prod, qty)
                            viewModel.navigateTo(ScreenRoute.CHECKOUT)
                        },
                        onToggleWishlist = { prod -> viewModel.toggleWishlist(prod) },
                        onSubmitReview = { id, name, rating, comment ->
                            viewModel.submitReview(id, name, rating, comment)
                        },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                ScreenRoute.CART -> {
                    CartScreen(
                        cartItems = cartItems,
                        cartSummary = cartSummary,
                        couponMessage = couponMessage,
                        language = currentLanguage,
                        onQuantityChange = { id, qty -> viewModel.updateCartQuantity(id, qty) },
                        onRemoveItem = { id -> viewModel.removeCartItem(id) },
                        onApplyCoupon = { code -> viewModel.applyCoupon(code) },
                        onRemoveCoupon = { viewModel.removeCoupon() },
                        onProceedToCheckout = { viewModel.navigateTo(ScreenRoute.CHECKOUT) },
                        onStartShopping = { viewModel.navigateTo(ScreenRoute.HOME) }
                    )
                }

                ScreenRoute.CHECKOUT -> {
                    CheckoutScreen(
                        cartItems = cartItems,
                        cartSummary = cartSummary,
                        addresses = addresses,
                        isPaymentProcessing = isPaymentProcessing,
                        language = currentLanguage,
                        onAddNewAddress = { addr -> viewModel.addNewAddress(addr) },
                        onPlaceOrder = { name, phone, address, district, method ->
                            viewModel.placeOrder(name, phone, address, district, method) { orderId ->
                                viewModel.selectOrder(orderId)
                            }
                        },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                ScreenRoute.ORDER_TRACKING -> {
                    OrderTrackingScreen(
                        order = selectedOrder ?: orders.firstOrNull(),
                        language = currentLanguage,
                        onBackToHome = { viewModel.navigateTo(ScreenRoute.HOME) }
                    )
                }

                ScreenRoute.WISHLIST -> {
                    WishlistScreen(
                        wishlistItems = wishlistItems,
                        language = currentLanguage,
                        onProductClick = { prodId -> viewModel.selectProduct(prodId) },
                        onRemoveFromWishlist = { prodId -> viewModel.toggleWishlist(allProducts.first { it.id == prodId }) },
                        onStartShopping = { viewModel.navigateTo(ScreenRoute.HOME) }
                    )
                }

                ScreenRoute.ACCOUNT -> {
                    AccountScreen(
                        orders = orders,
                        addresses = addresses,
                        currentRole = currentRole,
                        language = currentLanguage,
                        onRoleChange = { role -> viewModel.setRole(role) },
                        onLanguageToggle = { viewModel.toggleLanguage() },
                        onOrderClick = { id -> viewModel.selectOrder(id) },
                        onAddNewAddress = { addr -> viewModel.addNewAddress(addr) },
                        onDeleteAddress = { id -> viewModel.deleteAddress(id) },
                        onOpenAdminPanel = { viewModel.navigateTo(ScreenRoute.ADMIN_PANEL) },
                        onOpenSellerPanel = { viewModel.navigateTo(ScreenRoute.SELLER_PANEL) },
                        onOpenNotifications = { viewModel.navigateTo(ScreenRoute.NOTIFICATIONS) }
                    )
                }

                ScreenRoute.NOTIFICATIONS -> {
                    NotificationsScreen(
                        notifications = notifications,
                        language = currentLanguage,
                        onMarkRead = { id -> viewModel.markNotificationRead(id) },
                        onMarkAllRead = { viewModel.markAllNotificationsRead() }
                    )
                }

                ScreenRoute.ADMIN_PANEL -> {
                    AdminDashboardScreen(
                        products = allProducts,
                        categories = categories,
                        orders = orders,
                        sellers = sellers,
                        coupons = coupons,
                        language = currentLanguage,
                        onAddProduct = { prod -> viewModel.adminAddProduct(prod) },
                        onUpdatePriceStock = { id, p, s -> viewModel.adminUpdatePriceAndStock(id, p, s) },
                        onDeleteProduct = { id -> viewModel.adminDeleteProduct(id) },
                        onAddCategory = { cat -> viewModel.adminAddCategory(cat) },
                        onUpdateOrderStatus = { id, st, note -> viewModel.adminUpdateOrderStatus(id, st, note) },
                        onApproveSeller = { id, approved -> viewModel.adminApproveSeller(id, approved) },
                        onAddCoupon = { cp -> viewModel.adminAddCoupon(cp) },
                        onDeleteCoupon = { id -> viewModel.adminDeleteCoupon(id) },
                        onClose = { viewModel.navigateBack() }
                    )
                }

                ScreenRoute.SELLER_PANEL -> {
                    val currentSeller = sellers.firstOrNull { it.id == 1L }
                    val myProducts = allProducts.filter { it.sellerId == 1L }
                    SellerDashboardScreen(
                        currentSeller = currentSeller,
                        myProducts = myProducts,
                        categories = categories,
                        language = currentLanguage,
                        onAddProduct = { prod -> viewModel.sellerAddProduct(prod) },
                        onUpdatePriceStock = { id, p, s -> viewModel.adminUpdatePriceAndStock(id, p, s) },
                        onClose = { viewModel.navigateBack() }
                    )
                }
            }
        }
    }

    if (showRoleModal) {
        RoleSwitcherDialog(
            currentRole = currentRole,
            language = currentLanguage,
            onDismiss = { showRoleModal = false },
            onSelectRole = { role ->
                viewModel.setRole(role)
                showRoleModal = false
            }
        )
    }
}
