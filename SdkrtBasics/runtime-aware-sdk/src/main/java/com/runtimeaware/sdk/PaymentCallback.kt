package com.runtimeaware.sdk

import com.runtimeenabled.api.PaymentCallbackInterface

/**
 * Class that implements the interface declared by the runtime-enabled SDK.
 */
class PaymentCallback(private val callback: () -> Unit): PaymentCallbackInterface {

    override suspend fun onPaymentComplete() {
        callback()
    }
}