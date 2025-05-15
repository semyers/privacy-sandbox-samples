package com.runtimeaware.sdk

import com.runtimeenabled.api.PaymentCallbackInterface

/**
 * Adapter class that implements the interface declared by the Mediator.
 *
 * This is loaded by and registered with Mediator from runtime-aware-sdk (RA_SDK).
 */
class PaymentCallback(private val callback: () -> Unit): PaymentCallbackInterface {

    override suspend fun onPaymentComplete() {
        callback()
    }
}