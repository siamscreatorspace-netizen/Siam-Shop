package com.example.model

enum class PaymentMethod(val titleEn: String, val titleBn: String, val descriptionEn: String, val descriptionBn: String) {
    COD(
        "Cash on Delivery",
        "ক্যাশ অন ডেলিভারি (পণ্য পেয়ে মূল্য পরিশোধ)",
        "Pay with cash when your package is delivered",
        "পণ্য হাতে পেয়ে নগদ টাকা পরিশোধ করুন"
    ),
    BKASH(
        "bKash Online Payment",
        "বিকাশ অনলাইন পেমেন্ট",
        "Instant payment via bKash personal or merchant account",
        "বিকাশ ওয়ালেট থেকে সরাসরি সুরক্ষিত পেমেন্ট"
    ),
    NAGAD(
        "Nagad Online Payment",
        "নগদ অনলাইন পেমেন্ট",
        "Fast & secure payment via Nagad gateway",
        "নগদের মাধ্যমে দ্রুত ও সাশ্রয়ী পেমেন্ট"
    ),
    CARD(
        "Debit / Credit Card",
        "ডেবিট / ক্রেডিট কার্ড",
        "Visa, Mastercard, Amex with 3D Secure verification",
        "ভিসা, মাস্টারকার্ডের মাধ্যমে সুরক্ষিত অনলাইন পেমেন্ট"
    )
}
