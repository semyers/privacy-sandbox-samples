package com.example.client

import android.content.Context
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.privacysandbox.ui.client.view.SandboxedSdkUi
import androidx.privacysandbox.ui.core.SandboxedUiAdapter
import com.runtimeaware.sdk.ExistingSdk

class PaymentProvider(
    private val runtimeAwareSdk: ExistingSdk,
    private val appDisplayName: String,
    private val context: Context
) : PaymentProviderInterface {

    private lateinit var paymentSdkAdapter: SandboxedUiAdapter

    override suspend fun initialize(
        totalAmount: Double,
        onPaymentSuccess: () -> Unit
    ) {
        paymentSdkAdapter = runtimeAwareSdk.getSandboxedUiAdapter(
            appDisplayName,
            totalAmount,
            onPaymentSuccess,
            context
        )
    }

    @Composable
    override fun PaymentUi() {
        SandboxedSdkUi(
            sandboxedUiAdapter = paymentSdkAdapter,
            providerUiOnTop = true,
            modifier = Modifier.height(175.dp)
        )
    }
}