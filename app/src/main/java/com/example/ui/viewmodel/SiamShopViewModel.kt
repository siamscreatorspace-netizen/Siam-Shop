package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.*
import com.example.data.payment.DefaultPaymentGatewayService
import com.example.data.payment.PaymentInitRequest
import com.example.data.payment.PaymentResult
import com.example.data.repository.SiamShopRepository
import com.example.model.AppRole
import com.example.model.Language
import com.example.model.OrderStatus
import com.example.model.PaymentMethod
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class ScreenRoute {
    HOME,
    CATEGORIES,
    CART,
    WISHLIST,
    ACCOUNT,
    PRODUCT_DETAIL,
    CHECKOUT,
    ORDER_TRACKING,
    NOTIFICATIONS,
    ADMIN_PANEL,
    SELLER_PANEL
}

data class CartSummary(
    val subtotal: Double = 0.0,
    val discount: Double = 0.0,
    val deliveryFee: Double = 60.0,
    val finalTotal: Double = 60.0,
    val appliedCoupon: CouponEntity? = null,
    val couponError: String? = null
)

class SiamShopViewModel(private val repository: SiamShopRepository) : ViewModel() {

    private val paymentGateway = DefaultPaymentGatewayService()

    // App Preferences State
    private val _currentRole = MutableStateFlow(AppRole.CUSTOMER)
    val currentRole: StateFlow<AppRole> = _currentRole.asStateFlow()

    private val _currentLanguage = MutableStateFlow(Language.BANGLA)
    val currentLanguage: StateFlow<Language> = _currentLanguage.asStateFlow()

    private val _currentRoute = MutableStateFlow(ScreenRoute.HOME)
    val currentRoute: StateFlow<ScreenRoute> = _currentRoute.asStateFlow()

    private val _previousRoute = MutableStateFlow(ScreenRoute.HOME)

    // Selection State
    private val _selectedProductId = MutableStateFlow<Long?>(null)
    val selectedProductId: StateFlow<Long?> = _selectedProductId.asStateFlow()

    private val _selectedOrderId = MutableStateFlow<Long?>(null)
    val selectedOrderId: StateFlow<Long?> = _selectedOrderId.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryId = MutableStateFlow<Long?>(null)
    val selectedCategoryId: StateFlow<Long?> = _selectedCategoryId.asStateFlow()

    private val _appliedCoupon = MutableStateFlow<CouponEntity?>(null)
    val appliedCoupon: StateFlow<CouponEntity?> = _appliedCoupon.asStateFlow()

    private val _couponMessage = MutableStateFlow<String?>(null)
    val couponMessage: StateFlow<String?> = _couponMessage.asStateFlow()

    private val _isPaymentProcessing = MutableStateFlow(false)
    val isPaymentProcessing: StateFlow<Boolean> = _isPaymentProcessing.asStateFlow()

    private val _lastPlacedOrder = MutableStateFlow<OrderEntity?>(null)
    val lastPlacedOrder: StateFlow<OrderEntity?> = _lastPlacedOrder.asStateFlow()

    // Data Streams
    val allProducts: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val flashSaleProducts: StateFlow<List<ProductEntity>> = repository.flashSaleProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val featuredProducts: StateFlow<List<ProductEntity>> = repository.featuredProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCategories: StateFlow<List<CategoryEntity>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartItems: StateFlow<List<CartItemEntity>> = repository.allCartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wishlistItems: StateFlow<List<WishlistEntity>> = repository.allWishlistItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allOrders: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAddresses: StateFlow<List<AddressEntity>> = repository.allAddresses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allCoupons: StateFlow<List<CouponEntity>> = repository.allCoupons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allSellers: StateFlow<List<SellerEntity>> = repository.allSellers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allBanners: StateFlow<List<BannerEntity>> = repository.activeBanners
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationEntity>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadNotificationsCount: StateFlow<Int> = repository.unreadNotificationsCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalSalesAmount: StateFlow<Double?> = repository.totalSales
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Filtered / Searched Products
    val displayedProducts: StateFlow<List<ProductEntity>> = combine(
        allProducts,
        _searchQuery,
        _selectedCategoryId
    ) { products, query, catId ->
        var list = products
        if (catId != null) {
            list = list.filter { it.categoryId == catId }
        }
        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.titleEn.lowercase().contains(q) ||
                it.titleBn.lowercase().contains(q) ||
                it.categoryNameEn.lowercase().contains(q) ||
                it.categoryNameBn.lowercase().contains(q) ||
                it.descriptionEn.lowercase().contains(q) ||
                it.descriptionBn.lowercase().contains(q)
            }
        }
        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart calculations
    val cartSummary: StateFlow<CartSummary> = combine(
        cartItems,
        _appliedCoupon
    ) { items, coupon ->
        val subtotal = items.sumOf { it.price * it.quantity }
        val deliveryFee = if (subtotal >= 1000.0 || items.isEmpty()) 0.0 else 60.0

        var discount = 0.0
        if (coupon != null && subtotal >= coupon.minSpend) {
            discount = if (coupon.discountPercent > 0) {
                val calculated = (subtotal * coupon.discountPercent) / 100.0
                minOf(calculated, coupon.maxDiscount)
            } else {
                coupon.flatDiscount
            }
        }

        val finalTotal = maxOf(0.0, subtotal - discount + (if (items.isEmpty()) 0.0 else deliveryFee))

        CartSummary(
            subtotal = subtotal,
            discount = discount,
            deliveryFee = if (items.isEmpty()) 0.0 else deliveryFee,
            finalTotal = finalTotal,
            appliedCoupon = coupon
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CartSummary())

    // Currently Selected Product
    val currentProduct: StateFlow<ProductEntity?> = _selectedProductId.flatMapLatest { id ->
        if (id == null) flowOf(null) else repository.getProductById(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Current Product Reviews
    val currentProductReviews: StateFlow<List<ReviewEntity>> = _selectedProductId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.getReviewsForProduct(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Currently Selected Order
    val currentOrder: StateFlow<OrderEntity?> = _selectedOrderId.flatMapLatest { id ->
        if (id == null) flowOf(null) else repository.getOrderById(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Navigation and Mode Controls
    fun setRole(role: AppRole) {
        _currentRole.value = role
        if (role == AppRole.ADMIN) {
            _currentRoute.value = ScreenRoute.ADMIN_PANEL
        } else if (role == AppRole.SELLER) {
            _currentRoute.value = ScreenRoute.SELLER_PANEL
        } else {
            _currentRoute.value = ScreenRoute.HOME
        }
    }

    fun toggleLanguage() {
        _currentLanguage.value = if (_currentLanguage.value == Language.BANGLA) Language.ENGLISH else Language.BANGLA
    }

    fun navigateTo(route: ScreenRoute) {
        _previousRoute.value = _currentRoute.value
        _currentRoute.value = route
    }

    fun navigateBack() {
        _currentRoute.value = when (_currentRoute.value) {
            ScreenRoute.PRODUCT_DETAIL -> _previousRoute.value
            ScreenRoute.CHECKOUT -> ScreenRoute.CART
            ScreenRoute.ORDER_TRACKING -> ScreenRoute.ACCOUNT
            ScreenRoute.NOTIFICATIONS -> ScreenRoute.HOME
            ScreenRoute.ADMIN_PANEL -> ScreenRoute.ACCOUNT
            ScreenRoute.SELLER_PANEL -> ScreenRoute.ACCOUNT
            else -> ScreenRoute.HOME
        }
    }

    fun selectProduct(productId: Long) {
        _selectedProductId.value = productId
        _previousRoute.value = _currentRoute.value
        _currentRoute.value = ScreenRoute.PRODUCT_DETAIL
    }

    fun selectOrder(orderId: Long) {
        _selectedOrderId.value = orderId
        _previousRoute.value = _currentRoute.value
        _currentRoute.value = ScreenRoute.ORDER_TRACKING
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectCategory(categoryId: Long?) {
        _selectedCategoryId.value = categoryId
        if (_currentRoute.value != ScreenRoute.CATEGORIES && _currentRoute.value != ScreenRoute.HOME) {
            _currentRoute.value = ScreenRoute.CATEGORIES
        }
    }

    // Cart Operations
    fun addToCart(product: ProductEntity, quantity: Int = 1) {
        viewModelScope.launch {
            repository.addToCart(product, quantity)
        }
    }

    fun updateCartQuantity(cartItemId: Long, quantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(cartItemId, quantity)
        }
    }

    fun removeCartItem(cartItemId: Long) {
        viewModelScope.launch {
            repository.removeCartItem(cartItemId)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }

    // Wishlist Operations
    fun toggleWishlist(product: ProductEntity) {
        viewModelScope.launch {
            val exists = wishlistItems.value.any { it.productId == product.id }
            if (exists) {
                repository.removeFromWishlist(product.id)
            } else {
                repository.addToWishlist(product)
            }
        }
    }

    fun isProductWishlisted(productId: Long): Boolean {
        return wishlistItems.value.any { it.productId == productId }
    }

    // Coupon Operations
    fun applyCoupon(code: String) {
        viewModelScope.launch {
            val coupon = repository.getCouponByCode(code.trim())
            val subtotal = cartSummary.value.subtotal
            if (coupon == null) {
                _couponMessage.value = if (_currentLanguage.value == Language.BANGLA) "কুপন কোডটি সঠিক নয়!" else "Invalid coupon code!"
            } else if (subtotal < coupon.minSpend) {
                _couponMessage.value = if (_currentLanguage.value == Language.BANGLA)
                    "এই কুপন ব্যবহারের জন্য ন্যূনতম ৳${coupon.minSpend.toInt()} টাকার অর্ডার প্রয়োজন!"
                else
                    "Minimum order of ৳${coupon.minSpend.toInt()} required for this coupon!"
            } else {
                _appliedCoupon.value = coupon
                _couponMessage.value = if (_currentLanguage.value == Language.BANGLA) "কুপন প্রয়োগ সফল হয়েছে!" else "Coupon applied successfully!"
            }
        }
    }

    fun removeCoupon() {
        _appliedCoupon.value = null
        _couponMessage.value = null
    }

    // Checkout and Order Placement
    fun placeOrder(
        customerName: String,
        customerPhone: String,
        deliveryAddress: String,
        district: String,
        paymentMethod: PaymentMethod,
        onSuccess: (Long) -> Unit
    ) {
        viewModelScope.launch {
            _isPaymentProcessing.value = true
            val items = cartItems.value
            val summary = cartSummary.value

            val itemsSummary = items.joinToString(", ") { "${it.productTitleBn.ifBlank { it.productTitleEn }} (x${it.quantity})" }
            val orderNumber = "SM-${(100000..999999).random()}"

            // Simulate payment processing via gateway
            val result = paymentGateway.processPayment(
                PaymentInitRequest(
                    orderNumber = orderNumber,
                    amount = summary.finalTotal,
                    customerName = customerName,
                    customerPhone = customerPhone,
                    paymentMethod = paymentMethod
                )
            )

            if (result is PaymentResult.Success) {
                val order = OrderEntity(
                    orderNumber = orderNumber,
                    customerName = customerName,
                    customerPhone = customerPhone,
                    deliveryAddress = deliveryAddress,
                    district = district,
                    itemsSummary = itemsSummary,
                    itemCount = items.sumOf { it.quantity },
                    totalAmount = summary.subtotal,
                    discountAmount = summary.discount,
                    deliveryFee = summary.deliveryFee,
                    finalAmount = summary.finalTotal,
                    paymentMethod = paymentMethod,
                    orderStatus = OrderStatus.PENDING,
                    couponCode = summary.appliedCoupon?.code ?: "",
                    trackingNote = "Order Placed. Payment verified via ${paymentMethod.titleEn} (Txn: ${result.transactionId}). Awaiting packing."
                )

                val orderId = repository.placeOrder(order)
                summary.appliedCoupon?.let { repository.recordCouponUsage(it.id) }
                repository.clearCart()
                _appliedCoupon.value = null

                _lastPlacedOrder.value = order.copy(id = orderId)
                _selectedOrderId.value = orderId
                _isPaymentProcessing.value = false
                onSuccess(orderId)
            } else {
                _isPaymentProcessing.value = false
            }
        }
    }

    // Address Management
    fun addNewAddress(address: AddressEntity) {
        viewModelScope.launch {
            repository.insertAddress(address)
        }
    }

    fun setDefaultAddress(id: Long) {
        viewModelScope.launch {
            repository.setDefaultAddress(id)
        }
    }

    fun deleteAddress(id: Long) {
        viewModelScope.launch {
            repository.deleteAddress(id)
        }
    }

    // Reviews
    fun submitReview(productId: Long, customerName: String, rating: Int, comment: String) {
        viewModelScope.launch {
            repository.addReview(
                ReviewEntity(
                    productId = productId,
                    customerName = customerName.ifBlank { "Siam Shop Customer" },
                    rating = rating,
                    comment = comment,
                    dateString = "Just now"
                )
            )
        }
    }

    // Notifications
    fun markNotificationRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationRead(id)
        }
    }

    fun markAllNotificationsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsRead()
        }
    }

    // ADMIN ACTIONS
    fun adminAddProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.insertProduct(product)
        }
    }

    fun adminUpdatePriceAndStock(productId: Long, newPrice: Double, newStock: Int) {
        viewModelScope.launch {
            repository.updatePriceAndStock(productId, newPrice, newStock)
        }
    }

    fun adminDeleteProduct(productId: Long) {
        viewModelScope.launch {
            repository.deleteProductById(productId)
        }
    }

    fun adminAddCategory(category: CategoryEntity) {
        viewModelScope.launch {
            repository.insertCategory(category)
        }
    }

    fun adminDeleteCategory(categoryId: Long) {
        viewModelScope.launch {
            repository.deleteCategoryById(categoryId)
        }
    }

    fun adminUpdateOrderStatus(orderId: Long, status: OrderStatus, note: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status, note)
        }
    }

    fun adminAddCoupon(coupon: CouponEntity) {
        viewModelScope.launch {
            repository.insertCoupon(coupon)
        }
    }

    fun adminDeleteCoupon(couponId: Long) {
        viewModelScope.launch {
            repository.deleteCoupon(couponId)
        }
    }

    fun adminAddBanner(banner: BannerEntity) {
        viewModelScope.launch {
            repository.insertBanner(banner)
        }
    }

    fun adminDeleteBanner(bannerId: Long) {
        viewModelScope.launch {
            repository.deleteBanner(bannerId)
        }
    }

    fun adminApproveSeller(sellerId: Long, approved: Boolean) {
        viewModelScope.launch {
            repository.updateSellerApproval(sellerId, approved)
        }
    }

    // SELLER ACTIONS
    fun sellerAddProduct(product: ProductEntity) {
        viewModelScope.launch {
            repository.insertProduct(product)
        }
    }

    fun sellerRegister(seller: SellerEntity) {
        viewModelScope.launch {
            repository.insertSeller(seller)
        }
    }
}

class SiamShopViewModelFactory(private val repository: SiamShopRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SiamShopViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SiamShopViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
