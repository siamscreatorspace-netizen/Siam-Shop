package com.example.data.repository

import com.example.data.local.SiamShopDatabase
import com.example.data.local.entity.*
import com.example.model.OrderStatus
import kotlinx.coroutines.flow.Flow

class SiamShopRepository(private val db: SiamShopDatabase) {

    // Products
    val allProducts: Flow<List<ProductEntity>> = db.productDao().getAllProducts()
    val flashSaleProducts: Flow<List<ProductEntity>> = db.productDao().getFlashSaleProducts()
    val featuredProducts: Flow<List<ProductEntity>> = db.productDao().getFeaturedProducts()

    fun getProductById(id: Long): Flow<ProductEntity?> = db.productDao().getProductById(id)
    suspend fun getProductByIdDirect(id: Long): ProductEntity? = db.productDao().getProductByIdDirect(id)
    fun getProductsByCategory(categoryId: Long): Flow<List<ProductEntity>> = db.productDao().getProductsByCategory(categoryId)
    fun getProductsBySeller(sellerId: Long): Flow<List<ProductEntity>> = db.productDao().getProductsBySeller(sellerId)
    fun searchProducts(query: String): Flow<List<ProductEntity>> = db.productDao().searchProducts(query)

    suspend fun insertProduct(product: ProductEntity): Long = db.productDao().insertProduct(product)
    suspend fun updateProduct(product: ProductEntity) = db.productDao().updateProduct(product)
    suspend fun updatePriceAndStock(id: Long, newPrice: Double, newStock: Int) = db.productDao().updatePriceAndStock(id, newPrice, newStock)
    suspend fun deleteProductById(id: Long) = db.productDao().deleteProductById(id)

    // Categories
    val allCategories: Flow<List<CategoryEntity>> = db.categoryDao().getAllCategories()
    suspend fun insertCategory(category: CategoryEntity): Long = db.categoryDao().insertCategory(category)
    suspend fun deleteCategoryById(id: Long) = db.categoryDao().deleteCategoryById(id)

    // Cart
    val allCartItems: Flow<List<CartItemEntity>> = db.cartDao().getAllCartItems()

    suspend fun addToCart(product: ProductEntity, quantity: Int = 1) {
        val existing = db.cartDao().getCartItemByProductId(product.id)
        if (existing != null) {
            db.cartDao().updateQuantity(existing.id, existing.quantity + quantity)
        } else {
            db.cartDao().insertCartItem(
                CartItemEntity(
                    productId = product.id,
                    productTitleEn = product.titleEn,
                    productTitleBn = product.titleBn,
                    price = product.price,
                    originalPrice = product.originalPrice,
                    imageUrl = product.imageUrl,
                    quantity = quantity,
                    sellerName = product.sellerName
                )
            )
        }
    }

    suspend fun updateCartQuantity(id: Long, quantity: Int) {
        if (quantity <= 0) {
            db.cartDao().deleteCartItemById(id)
        } else {
            db.cartDao().updateQuantity(id, quantity)
        }
    }

    suspend fun removeCartItem(id: Long) = db.cartDao().deleteCartItemById(id)
    suspend fun clearCart() = db.cartDao().clearCart()

    // Wishlist
    val allWishlistItems: Flow<List<WishlistEntity>> = db.wishlistDao().getAllWishlistItems()
    fun isWishlisted(productId: Long): Flow<Boolean> = db.wishlistDao().isWishlisted(productId)

    suspend fun toggleWishlist(product: ProductEntity) {
        val isWish = db.wishlistDao().isWishlisted(product.id)
        // Check current status
        db.wishlistDao().deleteByProductId(product.id)
    }

    suspend fun addToWishlist(product: ProductEntity) {
        db.wishlistDao().insertWishlist(
            WishlistEntity(
                productId = product.id,
                titleEn = product.titleEn,
                titleBn = product.titleBn,
                price = product.price,
                originalPrice = product.originalPrice,
                imageUrl = product.imageUrl,
                rating = product.rating
            )
        )
    }

    suspend fun removeFromWishlist(productId: Long) {
        db.wishlistDao().deleteByProductId(productId)
    }

    // Orders
    val allOrders: Flow<List<OrderEntity>> = db.orderDao().getAllOrders()
    val ordersCount: Flow<Int> = db.orderDao().getOrdersCount()
    val totalSales: Flow<Double?> = db.orderDao().getTotalSales()

    fun getOrderById(id: Long): Flow<OrderEntity?> = db.orderDao().getOrderById(id)

    suspend fun placeOrder(order: OrderEntity): Long {
        val orderId = db.orderDao().insertOrder(order)
        // Automatically create order confirmation notification
        db.notificationDao().insertNotification(
            NotificationEntity(
                titleEn = "Order Placed #${order.orderNumber}",
                titleBn = "অর্ডার সম্পন্ন #${order.orderNumber}",
                messageEn = "Your order of ৳${order.finalAmount.toInt()} has been placed. Payment Method: ${order.paymentMethod.titleEn}.",
                messageBn = "আপনার ৳${order.finalAmount.toInt()}-এর অর্ডারটি গ্রহণ করা হয়েছে। পেমেন্ট পদ্ধতি: ${order.paymentMethod.titleBn}।",
                type = "ORDER"
            )
        )
        return orderId
    }

    suspend fun updateOrderStatus(orderId: Long, status: OrderStatus, note: String) {
        db.orderDao().updateOrderStatus(orderId, status, note)
        db.notificationDao().insertNotification(
            NotificationEntity(
                titleEn = "Order Status: ${status.titleEn}",
                titleBn = "অর্ডার স্ট্যাটাস: ${status.titleBn}",
                messageEn = note,
                messageBn = note,
                type = "ORDER"
            )
        )
    }

    // Addresses
    val allAddresses: Flow<List<AddressEntity>> = db.addressDao().getAllAddresses()
    val defaultAddress: Flow<AddressEntity?> = db.addressDao().getDefaultAddress()

    suspend fun insertAddress(address: AddressEntity): Long {
        if (address.isDefault) {
            db.addressDao().resetDefaults()
        }
        return db.addressDao().insertAddress(address)
    }

    suspend fun setDefaultAddress(id: Long) {
        db.addressDao().resetDefaults()
        db.addressDao().setDefaultAddress(id)
    }

    suspend fun deleteAddress(id: Long) = db.addressDao().deleteAddressById(id)

    // Coupons
    val allCoupons: Flow<List<CouponEntity>> = db.couponDao().getAllCoupons()
    suspend fun getCouponByCode(code: String): CouponEntity? = db.couponDao().getCouponByCode(code)
    suspend fun insertCoupon(coupon: CouponEntity): Long = db.couponDao().insertCoupon(coupon)
    suspend fun deleteCoupon(id: Long) = db.couponDao().deleteCouponById(id)
    suspend fun recordCouponUsage(id: Long) = db.couponDao().incrementUsage(id)

    // Notifications
    val allNotifications: Flow<List<NotificationEntity>> = db.notificationDao().getAllNotifications()
    val unreadNotificationsCount: Flow<Int> = db.notificationDao().getUnreadCount()
    suspend fun markNotificationRead(id: Long) = db.notificationDao().markAsRead(id)
    suspend fun markAllNotificationsRead() = db.notificationDao().markAllAsRead()

    // Sellers
    val allSellers: Flow<List<SellerEntity>> = db.sellerDao().getAllSellers()
    fun getSellerById(id: Long): Flow<SellerEntity?> = db.sellerDao().getSellerById(id)
    suspend fun insertSeller(seller: SellerEntity): Long = db.sellerDao().insertSeller(seller)
    suspend fun updateSellerApproval(id: Long, isApproved: Boolean) = db.sellerDao().updateApproval(id, isApproved)

    // Banners
    val activeBanners: Flow<List<BannerEntity>> = db.bannerDao().getActiveBanners()
    val allBanners: Flow<List<BannerEntity>> = db.bannerDao().getAllBanners()
    suspend fun insertBanner(banner: BannerEntity): Long = db.bannerDao().insertBanner(banner)
    suspend fun deleteBanner(id: Long) = db.bannerDao().deleteBannerById(id)

    // Reviews
    fun getReviewsForProduct(productId: Long): Flow<List<ReviewEntity>> = db.reviewDao().getReviewsForProduct(productId)
    suspend fun addReview(review: ReviewEntity): Long = db.reviewDao().insertReview(review)
}
