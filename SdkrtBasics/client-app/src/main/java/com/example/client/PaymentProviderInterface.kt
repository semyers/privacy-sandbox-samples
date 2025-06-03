package com.example.client

import androidx.compose.runtime.Composable

interface PaymentProviderInterface {
    suspend fun initialize(
        totalAmount: Double,
        onConfirm: () -> Unit
    )

    @Composable
    fun PaymentUi()
}