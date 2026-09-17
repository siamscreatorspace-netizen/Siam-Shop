package com.example.data.payment

import com.example.model.PaymentMethod
import kotlinx.coroutines.delay

/**
 * Payment Gateway Service Architecture for Siam Shop
 *
 * This service provides production-ready structure and contracts for integrating
 * Bangladeshi and International payment gateways:
 * 1. bKash Checkout URL/Tokenized API (Merchant Sandbox & Live)
 * 2. Nagad PGW (Direct Merchant Checkout)
 * 3. SSLCommerz / Shurjopay Gateway
 * 4. Stripe / Visa / Mastercard
 *
 * CONFIGURATION & SETUP INSTRUCTIONS:
 * -------------------------------------------------------------
 * 1. bKash:
 *    - Add `BKASH_APP_KEY`, `BKASH_APP_SECRET`, `BKASH_USERNAME`, `BKASH_PASSWORD`
 *      into the AI Studio Secrets panel / BuildConfig / server backend.
 *    - Base URL Sandbox: https://tokenized.sandbox.bka.sh/v2/tokenized/checkout
 *    - Base URL Live: https://tokenized.pay.bka.sh/v2/tokenized/checkout
 *
 * 2. Nagad:
 *    - Add `NAGAD_MERCHANT_ID`, `NAGAD_PUBLIC_KEY`, `NAGAD_PRIVATE_KEY`
 *    - Sandbox: http://sandbox.mynagad.com:10080/remote-payment-gateway-1.0/api/dfs/
 *
 * 3. SSLCommerz:
 *    - Add `SSL_STORE_ID`, `SSL_STORE_PASSWORD`
 *    - Sandbox: https://sandbox.sslcommerz.com/gwprocess/v4/api.php
 */
sealed class PaymentResult {
    data class Success(val transactionId: String, val method: PaymentMethod, val amount: Double) : PaymentResult()
    data class Failed(val errorMessage: String) : PaymentResult()
    object Cancelled : PaymentResult()
}

data class PaymentInitRequest(
    val orderNumber: String,
    val amount: Double,
    val customerName: String,
    val customerPhone: String,
    val paymentMethod: PaymentMethod
)

interface PaymentGatewayProvider {
    suspend fun processPayment(request: PaymentInitRequest): PaymentResult
}

class DefaultPaymentGatewayService : PaymentGatewayProvider {

    override suspend fun processPayment(request: PaymentInitRequest): PaymentResult {
        // Simulate real gateway verification and network handshake
        delay(1200)

        return when (request.paymentMethod) {
            PaymentMethod.COD -> {
                PaymentResult.Success(
                    transactionId = "COD-" + System.currentTimeMillis().toString().takeLast(6),
                    method = PaymentMethod.COD,
                    amount = request.amount
                )
            }
            PaymentMethod.BKASH -> {
                // Production integration point:
                // Call bKash CreatePayment API -> Redirect or Tokenized WebView -> ExecutePayment
                PaymentResult.Success(
                    transactionId = "BKASH-" + (100000..999999).random(),
                    method = PaymentMethod.BKASH,
                    amount = request.amount
                )
            }
            PaymentMethod.NAGAD -> {
                // Production integration point:
                // Initialize Nagad payment order -> Decrypt PGW Callback -> Verify
                PaymentResult.Success(
                    transactionId = "NGD-" + (100000..999999).random(),
                    method = PaymentMethod.NAGAD,
                    amount = request.amount
                )
            }
            PaymentMethod.CARD -> {
                // Production integration point:
                // SSLCommerz session init -> 3D Secure Card Verification
                PaymentResult.Success(
                    transactionId = "TXN-CARD-" + (100000..999999).random(),
                    method = PaymentMethod.CARD,
                    amount = request.amount
                )
            }
        }
    }
}
