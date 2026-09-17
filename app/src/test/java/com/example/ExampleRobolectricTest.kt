package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.AppRole
import com.example.model.AppStrings
import com.example.model.Language
import com.example.model.OrderStatus
import com.example.model.PaymentMethod
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Siam Shop", appName)
  }

  @Test
  fun `verify localization strings`() {
    val bnTitle = AppStrings.get("app_name", Language.BANGLA)
    val enTitle = AppStrings.get("app_name", Language.ENGLISH)
    assertEquals("সিয়াম শপ", bnTitle)
    assertEquals("Siam Shop", enTitle)
  }

  @Test
  fun `verify order status and payment method definitions`() {
    assertEquals(6, OrderStatus.values().size)
    assertEquals(4, PaymentMethod.values().size)
    assertEquals(3, AppRole.values().size)
    assertNotNull(PaymentMethod.COD)
    assertNotNull(PaymentMethod.BKASH)
  }
}
