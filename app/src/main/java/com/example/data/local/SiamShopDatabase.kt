package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.local.dao.*
import com.example.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        ProductEntity::class,
        CategoryEntity::class,
        CartItemEntity::class,
        WishlistEntity::class,
        OrderEntity::class,
        AddressEntity::class,
        CouponEntity::class,
        NotificationEntity::class,
        SellerEntity::class,
        BannerEntity::class,
        ReviewEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SiamShopDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
    abstract fun cartDao(): CartDao
    abstract fun wishlistDao(): WishlistDao
    abstract fun orderDao(): OrderDao
    abstract fun addressDao(): AddressDao
    abstract fun couponDao(): CouponDao
    abstract fun notificationDao(): NotificationDao
    abstract fun sellerDao(): SellerDao
    abstract fun bannerDao(): BannerDao
    abstract fun reviewDao(): ReviewDao

    companion object {
        @Volatile
        private var INSTANCE: SiamShopDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): SiamShopDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SiamShopDatabase::class.java,
                    "siam_shop_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(db: SiamShopDatabase) {
            // 1. Initial Categories (10 required categories)
            val categories = listOf(
                CategoryEntity(id = 1, nameEn = "Mobiles & Electronics", nameBn = "মোবাইল ও ইলেকট্রনিক্স", iconKey = "smartphone", sortOrder = 1),
                CategoryEntity(id = 2, nameEn = "Clothing & Fashion", nameBn = "পোশাক", iconKey = "checkroom", sortOrder = 2),
                CategoryEntity(id = 3, nameEn = "Footwear & Shoes", nameBn = "জুতা", iconKey = "footwear", sortOrder = 3),
                CategoryEntity(id = 4, nameEn = "Watches", nameBn = "ঘড়ি", iconKey = "watch", sortOrder = 4),
                CategoryEntity(id = 5, nameEn = "Home & Living", nameBn = "গৃহস্থালি পণ্য", iconKey = "home", sortOrder = 5),
                CategoryEntity(id = 6, nameEn = "Cosmetics & Beauty", nameBn = "কসমেটিকস", iconKey = "spa", sortOrder = 6),
                CategoryEntity(id = 7, nameEn = "Kitchen & Dining", nameBn = "কিচেন পণ্য", iconKey = "kitchen", sortOrder = 7),
                CategoryEntity(id = 8, nameEn = "Computers & Accessories", nameBn = "কম্পিউটার ও অ্যাক্সেসরিজ", iconKey = "computer", sortOrder = 8),
                CategoryEntity(id = 9, nameEn = "Baby & Kids", nameBn = "শিশুদের পণ্য", iconKey = "child", sortOrder = 9),
                CategoryEntity(id = 10, nameEn = "Groceries & Essentials", nameBn = "খাবার ও অন্যান্য দৈনন্দিন পণ্য", iconKey = "shopping_basket", sortOrder = 10)
            )
            db.categoryDao().insertCategories(categories)

            // 2. Initial Sellers
            val sellers = listOf(
                SellerEntity(
                    id = 1,
                    storeName = "Siam Official Flagship Store",
                    ownerName = "Siam Shop Manager",
                    phone = "+880 1700-112233",
                    email = "store@siamshop.com",
                    categorySpecialty = "All Categories",
                    commissionPercent = 5.0,
                    isApproved = true,
                    totalSalesAmount = 145000.0,
                    totalOrdersCount = 42,
                    rating = 4.9
                ),
                SellerEntity(
                    id = 2,
                    storeName = "Dhaka Gadget Hub",
                    ownerName = "Rahim Ahmed",
                    phone = "+880 1811-223344",
                    email = "gadgethub@siamshop.bd",
                    categorySpecialty = "Mobiles & Computers",
                    commissionPercent = 6.0,
                    isApproved = true,
                    totalSalesAmount = 85000.0,
                    totalOrdersCount = 28,
                    rating = 4.8
                ),
                SellerEntity(
                    id = 3,
                    storeName = "Artisan Fashion & Lifestyle",
                    ownerName = "Nusrat Jahan",
                    phone = "+880 1922-334455",
                    email = "artisan@siamshop.bd",
                    categorySpecialty = "Fashion & Cosmetics",
                    commissionPercent = 5.5,
                    isApproved = true,
                    totalSalesAmount = 62000.0,
                    totalOrdersCount = 35,
                    rating = 4.7
                )
            )
            db.sellerDao().insertSellers(sellers)

            // 3. Rich Initial Products across all categories
            val products = listOf(
                // Mobiles & Electronics
                ProductEntity(
                    id = 1,
                    titleEn = "Ultra Pro 5G Smartphone (12GB/256GB)",
                    titleBn = "আল্ট্রা প্রো ৫জি স্মার্টফোন (১২জিবি/২৫৬জিবি)",
                    descriptionEn = "Flagship AMOLED 120Hz display, 108MP OIS camera, 5000mAh battery with 67W Turbo charging. 1 Year Official Brand Warranty.",
                    descriptionBn = "ফ্ল্যাগশিপ অ্যামোলেড ১২০হার্টজ ডিসপ্লে, ১০৮মেগাপিক্সেল ট্রিপল ক্যামেরা, ৫০০০ মিলিঅ্যাম্পিয়ার ব্যাটারি এবং ৬৭ওয়াট ফাস্ট চার্জিং। ১ বছরের অফিসিয়াল ওয়ারেন্টি।",
                    categoryId = 1,
                    categoryNameEn = "Mobiles & Electronics",
                    categoryNameBn = "মোবাইল ও ইলেকট্রনিক্স",
                    price = 32990.0,
                    originalPrice = 36990.0,
                    discountPercent = 11,
                    rating = 4.9,
                    reviewCount = 54,
                    stock = 35,
                    sellerId = 1,
                    sellerName = "Siam Official Flagship Store",
                    isFlashSale = true,
                    isFeatured = true,
                    specifications = "Display: 6.67\" FHD+ AMOLED, RAM: 12GB, ROM: 256GB, Battery: 5000mAh"
                ),
                ProductEntity(
                    id = 2,
                    titleEn = "Wireless ANC Noise Cancelling Earbuds",
                    titleBn = "ওয়্যারলেস নয়েজ ক্যানসেলিং এয়ারবাডস",
                    descriptionEn = "Active Noise Cancellation up to 35dB, Low Latency Gaming Mode, 32 Hours total battery playback, IPX5 water resistance.",
                    descriptionBn = "৩৫ ডেসিবেল অ্যাক্টিভ নয়েজ ক্যান্সেলেশন, গেমিং মোড, ৩২ ঘণ্টা ব্যাকআপ এবং ওয়াটার রেজিস্ট্যান্ট।",
                    categoryId = 1,
                    categoryNameEn = "Mobiles & Electronics",
                    categoryNameBn = "মোবাইল ও ইলেকট্রনিক্স",
                    price = 2450.0,
                    originalPrice = 3200.0,
                    discountPercent = 23,
                    rating = 4.7,
                    reviewCount = 88,
                    stock = 60,
                    sellerId = 2,
                    sellerName = "Dhaka Gadget Hub",
                    isFlashSale = true,
                    isFeatured = true,
                    specifications = "Bluetooth: 5.3, Battery: 32h case, Charging: Type-C Fast"
                ),

                // Clothing & Fashion
                ProductEntity(
                    id = 3,
                    titleEn = "Premium Organic Cotton Men's Polo Shirt",
                    titleBn = "প্রিমিয়াম অর্গানিক সুতি পোলো শার্ট",
                    descriptionEn = "100% combed compact cotton, breathable pique knit, anti-pilling fabric finish. Perfect for casual and smart daily wear.",
                    descriptionBn = "১০০% প্রিমিয়াম সুতি কাপড়, অত্যন্ত আরামদায়ক ও দীর্ঘস্থায়ী। দৈনন্দিন ও ক্যাজুয়াল ব্যবহারের জন্য চমৎকার।",
                    categoryId = 2,
                    categoryNameEn = "Clothing & Fashion",
                    categoryNameBn = "পোশাক",
                    price = 850.0,
                    originalPrice = 1200.0,
                    discountPercent = 29,
                    rating = 4.8,
                    reviewCount = 112,
                    stock = 90,
                    sellerId = 3,
                    sellerName = "Artisan Fashion & Lifestyle",
                    isFlashSale = false,
                    isFeatured = true,
                    specifications = "Material: 100% Organic Pique Cotton, GSM: 220, Fit: Regular Slim"
                ),
                ProductEntity(
                    id = 4,
                    titleEn = "Embroidered Traditional Festive Kurti",
                    titleBn = "এমব্রয়ডারি করা এক্সক্লুসিভ কুর্তি",
                    descriptionEn = "Elegant handcrafted neckline embroidery with soft georgette lining. Ideal for Eid, festive occasions, and parties.",
                    descriptionBn = "হাতে তৈরি নকশাদার গলার কাজ, আরামদায়ক ও দৃষ্টিনন্দন। উৎসব ও যেকোনো অনুষ্ঠানে পরার উপযোগী।",
                    categoryId = 2,
                    categoryNameEn = "Clothing & Fashion",
                    categoryNameBn = "পোশাক",
                    price = 1750.0,
                    originalPrice = 2400.0,
                    discountPercent = 27,
                    rating = 4.9,
                    reviewCount = 43,
                    stock = 40,
                    sellerId = 3,
                    sellerName = "Artisan Fashion & Lifestyle",
                    isFlashSale = true,
                    isFeatured = true,
                    specifications = "Fabric: Premium Rayon Silk Blend, Work: Multi-thread Needle Embroidery"
                ),

                // Footwear & Shoes
                ProductEntity(
                    id = 5,
                    titleEn = "Ultralight Breathable Running Sneakers",
                    titleBn = "আল্ট্রালাইট রানিং স্পোর্টস স্নিকার্স",
                    descriptionEn = "Air cushion foam sole, breathable flyknit mesh upper, anti-slip traction rubber. Great for jogging, gym, and street fashion.",
                    descriptionBn = "অত্যন্ত হালকা ও আরামদায়ক এয়ার কুশন সোল, মেমরি ফোম ইনসোল এবং অ্যান্টি-স্লিপ গ্রিপ।",
                    categoryId = 3,
                    categoryNameEn = "Footwear & Shoes",
                    categoryNameBn = "জুতা",
                    price = 1950.0,
                    originalPrice = 2800.0,
                    discountPercent = 30,
                    rating = 4.6,
                    reviewCount = 67,
                    stock = 55,
                    sellerId = 1,
                    sellerName = "Siam Official Flagship Store",
                    isFlashSale = false,
                    isFeatured = true,
                    specifications = "Sole: EVA + High Grip Rubber, Upper: Breathable Mesh, Weight: 310g"
                ),

                // Watches
                ProductEntity(
                    id = 6,
                    titleEn = "Amoled Smartwatch with Bluetooth Calling",
                    titleBn = "অ্যামোলেড কলিং স্মার্টওয়াচ",
                    descriptionEn = "1.43\" Retina AMOLED, Always-On Display, HD Mic & Speaker, 120+ Sports modes, Heart Rate & SpO2 tracking, 10 days battery.",
                    descriptionBn = "১.৪৩\" স্পষ্ট অ্যামোলেড ডিসপ্লে, সরাসরি ফোন কল রিসিভ ও করার সুবিধা, হার্টরেট ও রক্তে অক্সিজেন মনিটরিং এবং ১০ দিনের ব্যাটারি ব্যাকআপ।",
                    categoryId = 4,
                    categoryNameEn = "Watches",
                    categoryNameBn = "ঘড়ি",
                    price = 3450.0,
                    originalPrice = 4500.0,
                    discountPercent = 23,
                    rating = 4.9,
                    reviewCount = 95,
                    stock = 70,
                    sellerId = 1,
                    sellerName = "Siam Official Flagship Store",
                    isFlashSale = true,
                    isFeatured = true,
                    specifications = "Screen: 1.43\" AMOLED 466x466, Waterproof: IP68, Battery: 380mAh"
                ),

                // Home & Living
                ProductEntity(
                    id = 7,
                    titleEn = "Smart Ultrasonic Air Humidifier & Diffuser",
                    titleBn = "আল্ট্রাসনিক এয়ার হিউমিডিফায়ার ও ডিফিউজার",
                    descriptionEn = "4L large water tank, whisper-quiet operation, auto shut-off, aroma essential oil tray, ambient LED night light.",
                    descriptionBn = "৪ লিটার ধারণক্ষমতা, ঘরের বাতাস আর্দ্র ও স্নিগ্ধ রাখে, সুগন্ধি এসেন্সিয়াল অয়েল ব্যবহারের সুবিধা এবং শান্ত নাইট লাইট।",
                    categoryId = 5,
                    categoryNameEn = "Home & Living",
                    categoryNameBn = "গৃহস্থালি পণ্য",
                    price = 1850.0,
                    originalPrice = 2500.0,
                    discountPercent = 26,
                    rating = 4.7,
                    reviewCount = 38,
                    stock = 45,
                    sellerId = 1,
                    sellerName = "Siam Official Flagship Store",
                    isFlashSale = false,
                    isFeatured = false,
                    specifications = "Capacity: 4 Liters, Mist Output: 300ml/h, Noise: <28dB"
                ),

                // Cosmetics & Beauty
                ProductEntity(
                    id = 8,
                    titleEn = "Deep Hydration Hyaluronic Acid Serum (50ml)",
                    titleBn = "ডিপ হাইড্রেশন হায়ালুরোনিক অ্যাসিড সিরাম",
                    descriptionEn = "Intense multi-layer skin hydration, restores skin barrier, dermatologically tested, cruelty-free and non-greasy formula.",
                    descriptionBn = "ত্বকের গভীর থেকে আর্দ্রতা ফিরিয়ে আনে, ত্বক করে মসৃণ ও উজ্জ্বল। ডার্মাটোলজিক্যালি টেস্টেড।",
                    categoryId = 6,
                    categoryNameEn = "Cosmetics & Beauty",
                    categoryNameBn = "কসমেটিকস",
                    price = 1250.0,
                    originalPrice = 1650.0,
                    discountPercent = 24,
                    rating = 4.8,
                    reviewCount = 76,
                    stock = 80,
                    sellerId = 3,
                    sellerName = "Artisan Fashion & Lifestyle",
                    isFlashSale = false,
                    isFeatured = true,
                    specifications = "Volume: 50ml, Skin Type: All, Origin: Dermatological Formula"
                ),

                // Kitchen & Dining
                ProductEntity(
                    id = 9,
                    titleEn = "Multi-Function Rapid Electric Air Fryer 5.5L",
                    titleBn = "মাল্টি-ফাংশন ডিজিটাল এয়ার ফ্রায়ার ৫.৫ লিটার",
                    descriptionEn = "85% less oil frying, 360-degree rapid heat air circulation, digital touch presets for chicken, fries, fish and baking.",
                    descriptionBn = "তেল ছাড়া বা সামান্য তেলে স্বাস্থ্যকর ফ্রাই ও বেকিং। ৫.৫ লিটার ক্যাপাসিটি এবং ডিজিটাল টাচ কন্ট্রোল প্যানেল।",
                    categoryId = 7,
                    categoryNameEn = "Kitchen & Dining",
                    categoryNameBn = "কিচেন পণ্য",
                    price = 5890.0,
                    originalPrice = 7500.0,
                    discountPercent = 21,
                    rating = 4.8,
                    reviewCount = 52,
                    stock = 25,
                    sellerId = 1,
                    sellerName = "Siam Official Flagship Store",
                    isFlashSale = true,
                    isFeatured = true,
                    specifications = "Capacity: 5.5L, Power: 1700W, Basket: Non-stick Food Grade"
                ),

                // Computers & Accessories
                ProductEntity(
                    id = 10,
                    titleEn = "Mechanical RGB Gaming Keyboard (Blue Switch)",
                    titleBn = "মেকানিক্যাল আরজিবি গেমিং কীবোর্ড",
                    descriptionEn = "Tactile clicky switches, 18 dynamic RGB lighting presets, durable aluminum chassis, full anti-ghosting keys.",
                    descriptionBn = "টেকটাইলে ব্লু সুইচ, ১৮টি ডায়নামিক আরজিবি ব্যাকলাইট মোড এবং মজবুত অ্যালুমিনিয়াম বডি।",
                    categoryId = 8,
                    categoryNameEn = "Computers & Accessories",
                    categoryNameBn = "কম্পিউটার ও অ্যাক্সেসরিজ",
                    price = 2850.0,
                    originalPrice = 3600.0,
                    discountPercent = 20,
                    rating = 4.7,
                    reviewCount = 61,
                    stock = 30,
                    sellerId = 2,
                    sellerName = "Dhaka Gadget Hub",
                    isFlashSale = false,
                    isFeatured = true,
                    specifications = "Switches: Outemu Blue, Layout: 87 Keys Tenkeyless, Cable: Braided Type-C"
                ),

                // Baby & Kids
                ProductEntity(
                    id = 11,
                    titleEn = "Soft Organic Baby Romper & Cap Set (Pack of 3)",
                    titleBn = "শিশুদের অর্গানিক সুতি রম্পার ও ক্যাপ সেট",
                    descriptionEn = "100% safe hypoallergenic cotton, smooth snap buttons for quick diaper changes, ultra-gentle on baby sensitive skin.",
                    descriptionBn = "১০০% নিরাপদ ও কোমল অর্গানিক কটন। শিশুর সংবেদনশীল ত্বকের জন্য অত্যন্ত আরামদায়ক ও নিরাপদ।",
                    categoryId = 9,
                    categoryNameEn = "Baby & Kids",
                    categoryNameBn = "শিশুদের পণ্য",
                    price = 990.0,
                    originalPrice = 1450.0,
                    discountPercent = 31,
                    rating = 4.9,
                    reviewCount = 34,
                    stock = 65,
                    sellerId = 3,
                    sellerName = "Artisan Fashion & Lifestyle",
                    isFlashSale = false,
                    isFeatured = false,
                    specifications = "Age: 0-12 Months, Pack: 3 Rompers + 1 Cap, Fabric: 100% Organic Cotton"
                ),

                // Groceries & Essentials
                ProductEntity(
                    id = 12,
                    titleEn = "Pure Natural Sundarban Raw Honey (500g)",
                    titleBn = "সুন্দরবনের খাঁটি প্রাকৃতিক মধু (৫০০ গ্রাম)",
                    descriptionEn = "100% pure unfiltered organic wildflower honey harvested directly from the Sundarbans. Certified quality guarantee.",
                    descriptionBn = "১০০% খাঁটি ও অপরিশোধিত সুন্দরবনের প্রাকৃতিক চাকের মধু। কোনো প্রকার রাসায়নিক বা চিনি মুক্ত।",
                    categoryId = 10,
                    categoryNameEn = "Groceries & Essentials",
                    categoryNameBn = "খাবার ও অন্যান্য দৈনন্দিন পণ্য",
                    price = 650.0,
                    originalPrice = 850.0,
                    discountPercent = 23,
                    rating = 4.9,
                    reviewCount = 118,
                    stock = 120,
                    sellerId = 1,
                    sellerName = "Siam Official Flagship Store",
                    isFlashSale = true,
                    isFeatured = true,
                    specifications = "Weight: 500g, Source: Sundarbans Forest, Purity: 100% Organic"
                )
            )
            db.productDao().insertProducts(products)

            // 4. Initial Coupons
            val coupons = listOf(
                CouponEntity(
                    id = 1,
                    code = "SIAM10",
                    discountPercent = 10,
                    minSpend = 500.0,
                    maxDiscount = 500.0,
                    descriptionEn = "Flat 10% discount on orders above ৳500",
                    descriptionBn = "৳৫০০ এর বেশি অর্ডারে ১০% বিশেষ ছাড়",
                    isActive = true
                ),
                CouponEntity(
                    id = 2,
                    code = "EID2026",
                    discountPercent = 15,
                    minSpend = 1500.0,
                    maxDiscount = 1000.0,
                    descriptionEn = "Mega 15% discount for festival celebrations",
                    descriptionBn = "উৎসবের আনন্দে ১৫% পর্যন্ত মেগা ছাড়",
                    isActive = true
                ),
                CouponEntity(
                    id = 3,
                    code = "WELCOME50",
                    discountPercent = 0,
                    flatDiscount = 50.0,
                    minSpend = 300.0,
                    maxDiscount = 50.0,
                    descriptionEn = "৳50 off on your first order at Siam Shop",
                    descriptionBn = "সিয়াম শপে প্রথম অর্ডারে ৳৫০ ফ্ল্যাট ছাড়",
                    isActive = true
                )
            )
            db.couponDao().insertCoupons(coupons)

            // 5. Initial Address
            val defaultAddress = AddressEntity(
                id = 1,
                recipientName = "Customer",
                phone = "01700-000000",
                division = "Dhaka",
                district = "Mirpur, Dhaka",
                fullAddress = "House 12, Road 4, Block C, Mirpur-10, Dhaka 1216",
                tag = "Home",
                isDefault = true
            )
            db.addressDao().insertAddress(defaultAddress)

            // 6. Initial Banners
            val banners = listOf(
                BannerEntity(
                    id = 1,
                    titleEn = "Mega Marketplace Launch Festival",
                    titleBn = "সিয়াম শপ মেগা উদ্বোধনী অফার",
                    subtitleEn = "Up to 50% Off across all categories with Free Fast Shipping!",
                    subtitleBn = "সকল ক্যাটাগরিতে ৫০% পর্যন্ত ছাড় এবং ফ্রি হোম ডেলিভারি!",
                    badgeEn = "HOT DEALS",
                    badgeBn = "সেরা অফার",
                    drawableResName = "banner_hero_promo",
                    isActive = true
                ),
                BannerEntity(
                    id = 2,
                    titleEn = "Smart Tech & Gadgets Week",
                    titleBn = "স্মার্ট গ্যাজেট ও ইলেকট্রনিক্স ফেস্ট",
                    subtitleEn = "Upgrade your gear with official 1 Year Warranty!",
                    subtitleBn = "অফিসিয়াল ১ বছরের ওয়ারেন্টি সহ সেরা দামে স্মার্টফোন ও গ্যাজেট কিনুন!",
                    badgeEn = "TOP TECH",
                    badgeBn = "সেরা গ্যাজেট",
                    drawableResName = "banner_hero_promo",
                    isActive = true
                )
            )
            db.bannerDao().insertBanners(banners)

            // 7. Initial Reviews
            val reviews = listOf(
                ReviewEntity(
                    productId = 1,
                    customerName = "Tanvir Hasan",
                    rating = 5,
                    comment = "Phone performance is super smooth! Fast delivery within 24 hours.",
                    dateString = "2 days ago"
                ),
                ReviewEntity(
                    productId = 1,
                    customerName = "Farzana Akhter",
                    rating = 5,
                    comment = "Great camera quality and long-lasting battery. Authentic product!",
                    dateString = "5 days ago"
                ),
                ReviewEntity(
                    productId = 6,
                    customerName = "Mahmudul Karim",
                    rating = 5,
                    comment = "The AMOLED display and Bluetooth calling works flawlessly.",
                    dateString = "1 week ago"
                ),
                ReviewEntity(
                    productId = 12,
                    customerName = "Salma Begum",
                    rating = 5,
                    comment = "100% original Sundarban honey, natural aroma. Highly recommended!",
                    dateString = "3 days ago"
                )
            )
            db.reviewDao().insertReviews(reviews)

            // 8. Welcome Notification
            val notifications = listOf(
                NotificationEntity(
                    titleEn = "Welcome to Siam Shop! 🎉",
                    titleBn = "সিয়াম শপে আপনাকে স্বাগতম! 🎉",
                    messageEn = "Enjoy exclusive discounts and authentic products delivered to your doorstep. Use code SIAM10 for 10% off.",
                    messageBn = "সেরা মূল্যে আসল পণ্য অর্ডার করুন সরাসরি আপনার ঠিকানায়। ১০% ছাড়ে কুপন কোড SIAM10 ব্যবহার করুন।",
                    type = "PROMO"
                ),
                NotificationEntity(
                    titleEn = "⚡ Flash Deals are Live!",
                    titleBn = "⚡ আজকের ফ্ল্যাশ ডিল শুরু হয়েছে!",
                    messageEn = "Limited time discounts on top gadgets, fashion, and kitchen appliances.",
                    messageBn = "সীমিত সময়ের জন্য ইলেকট্রনিক্স, পোশাক ও কিচেন পণ্যে বিশেষ মূল্যছাড়।",
                    type = "PROMO"
                )
            )
            db.notificationDao().insertNotifications(notifications)
        }
    }
}
