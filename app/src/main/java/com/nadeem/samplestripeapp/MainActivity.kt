package com.nadeem.samplestripeapp

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import com.nadeem.samplestripeapp.databinding.ActivityMainBinding
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult

const val STRIPE_TEST =
    "pk_test_51PIPzQI0DGZ9CkPIzfQUAihgsOIoTlIiVSY4gFmSsGqA31ZaKTK21mXpn4jDEkPWNewclCIfHZupA014aLTKe73D00RjRtPzar"
const val PI_TEST = "pi_3PaE5xI0DGZ9CkPI0Hy5r6t8_secret_tcGeyUVDRkJ09eaoVOBzof3TK"

class MainActivity : AppCompatActivity() {
    private val MERCHANT_COUNTRY_CODE: String = "US"
    private val MERCHANT_CURRENCY_CODE: String = "USD"
    private val MERCHANT_NAME: String = "Example, Inc."

    private var paymentSheet: PaymentSheet? = null

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        PaymentConfiguration.init(
            applicationContext,
            STRIPE_TEST
        )


        paymentSheet = PaymentSheet(
            this
        ) { paymentResult: PaymentSheetResult ->
            if (paymentResult is PaymentSheetResult.Completed) {
                showToast("Payment complete!")
            } else if (paymentResult is PaymentSheetResult.Canceled) {
                showToast("Payment canceled!")
            } else if (paymentResult is PaymentSheetResult.Failed) {
                showToast("Error!!!")
                paymentResult.error.localizedMessage?.let { showErrorDialog(it) }
            }
        }


        binding.testStripeTestGpay.setOnClickListener {
            onPayClicked(paymentSheet!!, PI_TEST, false)
        }

        binding.testStripeLiveGpay.setOnClickListener {
            onPayClicked(paymentSheet!!, PI_TEST, true)
        }
    }

    private fun onPayClicked(
        paymentSheet: PaymentSheet, paymentIntentClientSecret: String, isLive: Boolean = false
    ) {
        val configuration: PaymentSheet.Configuration.Builder =
            PaymentSheet.Configuration.Builder(MERCHANT_NAME)

        configuration.appearance(
            PaymentSheet.Appearance.Builder().primaryButton(
                PaymentSheet.PrimaryButton()
            ).build()
        )

        configuration.googlePay(
            PaymentSheet.GooglePayConfiguration(
                environment = if (isLive) PaymentSheet.GooglePayConfiguration.Environment.Production else PaymentSheet.GooglePayConfiguration.Environment.Test,
                countryCode = MERCHANT_COUNTRY_CODE,
                amount = 0L,
                label = "Testing Payment",
                currencyCode = MERCHANT_CURRENCY_CODE,
                buttonType = PaymentSheet.GooglePayConfiguration.ButtonType.Checkout
            )
        )

        paymentSheet.presentWithPaymentIntent(paymentIntentClientSecret, configuration.build())
    }


    // common
    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(
                this, message, Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun showErrorDialog(errorMessage: String) {
        runOnUiThread {
            AlertDialog.Builder(this).setTitle("Error occurred during checkout")
                .setMessage(errorMessage).setPositiveButton("Ok", null).show()
        }
    }

}