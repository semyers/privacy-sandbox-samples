package com.example.client.data

data class MenuItem(
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val imageUrl: String? = null, // For image loading (e.g., from network or local resources)
    val drawableResId: Int? = null // For local placeholder drawables (e.g., R.drawable.pizza)
)

data class OrderItem(
    val menuItem: MenuItem,
    var quantity: Int
)