/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.client

import android.os.Bundle
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.lifecycleScope
import androidx.privacysandbox.ui.client.view.SandboxedSdkUi
import androidx.privacysandbox.ui.core.SandboxedUiAdapter
import com.example.client.ui.theme.ComposeTutorialTheme
import com.example.privacysandbox.client.R
import com.runtimeaware.sdk.BannerAd
import com.runtimeaware.sdk.ExistingSdk
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    /** Container for rendering content from the SDK. */
    private lateinit var bannerAd: BannerAd
    private lateinit var paymentSdkAdapter: SandboxedUiAdapter

    private val runtimeAwareSdk = ExistingSdk(this)

//    /** A spinner for selecting the size of the file created in the sandbox. */
//    private lateinit var fileSizeSpinner: Spinner
//
//    /** Represents a file size that can be selected in the UI. */
//    private data class FileSize(val sizeInMb: Int) {
//        /** Called when FileSize is shown in the spinner. */
//        override fun toString() = "$sizeInMb MB"
//    }
//

    /** Spinners for selecting food. */
    private lateinit var pizzaSpinner: Spinner
    private lateinit var saladSpinner: Spinner
    private lateinit var cookieSpinner: Spinner
    private val orderCount = listOf(
        "0",
        "1",
        "2",
        "3",
    )

    private var total = 0.0

    // Sample Data (In a real app, this would come from a ViewModel or repository)
    val sampleMenuItems = listOf(
        MenuItem(
            "1",
            "Margherita Pizza",
            "Classic cheese and tomato pizza",
            12.99,
            drawableResId = null /* R.drawable.margherita_pizza */
        ),
        MenuItem(
            "2",
            "Pepperoni Pizza",
            "Pizza with spicy pepperoni slices",
            14.99,
            drawableResId = null /* R.drawable.pepperoni_pizza */
        ),
        MenuItem(
            "3",
            "Caesar Salad",
            "Fresh romaine lettuce with Caesar dressing",
            8.50,
            drawableResId = null /* R.drawable.caesar_salad */
        ),
        MenuItem(
            "4",
            "Cheeseburger",
            "Beef patty with cheese, lettuce, and tomato",
            10.75,
            drawableResId = null /* R.drawable.cheeseburger */
        ),
        MenuItem(
            "5",
            "Fries",
            "Crispy golden french fries",
            4.00,
            drawableResId = null /* R.drawable.fries */
        ),
        MenuItem(
            "6",
            "Cola",
            "Refreshing cola drink",
            2.50,
            drawableResId = null /* R.drawable.cola */
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//        setContent {
//            ComposeTutorialTheme {
//                Conversation(SampleData.conversationSample)
//            }
//        }
        lifecycleScope.launch {
            if (!runtimeAwareSdk.initialize()) {
                makeToast("Failed to initialize SDK")
            } else {
                makeToast("Initialized SDK!")
            }
            paymentSdkAdapter = runtimeAwareSdk.getSandboxedUiAdapter(
                APP_DISPLAY_NAME,
                total,
                onPaymentComplete(),
                this@MainActivity
            )
        }
        setContent {
            ComposeTutorialTheme {
                RestaurantMenuScreen(sampleMenuItems)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun RestaurantMenuScreen(menuItems: List<MenuItem>) {
        var orderItems by remember { mutableStateOf(mapOf<String, OrderItem>()) }
        var showPinDialog by remember { mutableStateOf(false) }
        //var paymentSdkAdapter by remember { mutableStateOf<SandboxedUiAdapter?>(null) }
        val context = LocalContext.current

        val totalAmount = orderItems.values.sumOf { it.menuItem.price * it.quantity }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("The Great Eatery") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            },
            bottomBar = {
                if (totalAmount > 0) {
                    BottomAppBar(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total: $${String.format("%.2f", totalAmount)}",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Button(
                                onClick = {
                                    lifecycleScope.launch {
                                        paymentSdkAdapter = runtimeAwareSdk.getSandboxedUiAdapter(
                                            APP_DISPLAY_NAME,
                                            totalAmount,
                                            {
                                                showPinDialog = false
                                                // In a real app, you would validate the PIN and process the payment
                                                Toast.makeText(
                                                    context,
                                                    "Payment processing for $${
                                                        String.format(
                                                            "%.2f",
                                                            totalAmount
                                                        )
                                                    }",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                // Reset order after "payment"
                                                orderItems = emptyMap()
                                            },
                                            this@MainActivity
                                        )
                                        showPinDialog = true
                                    }
                                  },
                                enabled = totalAmount > 0
                            ) {
                                Text("Pay Now")
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            LazyColumn(
                contentPadding = paddingValues,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { // For a little spacing at the top
                    Spacer(modifier = Modifier.height(8.dp))
                }
                items(menuItems) { menuItem ->
                    MenuItemRow(
                        menuItem = menuItem,
                        orderItem = orderItems[menuItem.id],
                        onQuantityChange = { newItem ->
                            orderItems = orderItems.toMutableMap().apply {
                                if (newItem.quantity > 0) {
                                    put(newItem.menuItem.id, newItem)
                                } else {
                                    remove(newItem.menuItem.id)
                                }
                            }
                        }
                    )
                }
                item { // For a little spacing at the bottom, before the bottom bar
                    Spacer(modifier = Modifier.height(if (totalAmount > 0) 70.dp else 8.dp))
                }
            }
        }

        if (showPinDialog) {
            SandboxedSdkUi(
                sandboxedUiAdapter = paymentSdkAdapter,
                providerUiOnTop = true
            )
//            PinEntryDialog(
//                totalAmount = totalAmount,
//                onDismiss = { showPinDialog = false },
//                onConfirm = { pin ->
//                    showPinDialog = false
//                    // In a real app, you would validate the PIN and process the payment
//                    Toast.makeText(
//                        context,
//                        "Payment processing with PIN: $pin for $${
//                            String.format(
//                                "%.2f",
//                                totalAmount
//                            )
//                        }",
//                        Toast.LENGTH_LONG
//                    ).show()
//                    // Reset order after "payment"
//                    orderItems = emptyMap()
//                }
//            )
        }
    }

    @Composable
    fun PinEntryDialog(
        totalAmount: Double,
        onDismiss: () -> Unit,
        onConfirm: (String) -> Unit
    ) {
        var pin by rememberSaveable { mutableStateOf("") }
        val maxPinLength = 4 // Or your desired PIN length

        Dialog(onDismissRequest = onDismiss) {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Enter PIN to Pay",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Amount: $${String.format("%.2f", totalAmount)}",
                        style = MaterialTheme.typography.titleMedium
                    )

                    OutlinedTextField(
                        value = pin,
                        onValueChange = {
                            if (it.length <= maxPinLength && it.all { char -> char.isDigit() }) {
                                pin = it
                            }
                        },
                        label = { Text("PIN") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        modifier = Modifier.fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(
                            textAlign = TextAlign.Center,
                            fontSize = 20.sp
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Button(
                            onClick = onDismiss,
                            colors = ButtonDefaults.outlinedButtonColors(),
                            border = ButtonDefaults.outlinedButtonBorder
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            onClick = {
                                if (pin.length == maxPinLength) { // Or any other validation
                                    onConfirm(pin)
                                }
                            },
                            enabled = pin.length == maxPinLength // Enable only when PIN has sufficient length
                        ) {
                            Text("Confirm Payment")
                        }
                    }
                }
            }
        }
    }


    @Composable
    fun MenuItemRow(
        menuItem: MenuItem,
        orderItem: OrderItem?,
        onQuantityChange: (OrderItem) -> Unit
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Placeholder for Image - In a real app, use Coil or Glide
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    if (menuItem.drawableResId != null) {
                        Image(
                            painter = painterResource(id = menuItem.drawableResId),
                            contentDescription = menuItem.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        // Display first letter or a generic icon if no image
                        Text(
                            text = menuItem.name.firstOrNull()?.toString() ?: "?",
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = menuItem.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = menuItem.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$${String.format("%.2f", menuItem.price)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                QuantitySelector(
                    quantity = orderItem?.quantity ?: 0,
                    onIncrease = {
                        val currentQuantity = orderItem?.quantity ?: 0
                        onQuantityChange(OrderItem(menuItem, currentQuantity + 1))
                    },
                    onDecrease = {
                        val currentQuantity = orderItem?.quantity ?: 0
                        if (currentQuantity > 0) {
                            onQuantityChange(OrderItem(menuItem, currentQuantity - 1))
                        }
                    }
                )
            }
        }
    }

    @Composable
    fun QuantitySelector(
        quantity: Int,
        onIncrease: () -> Unit,
        onDecrease: () -> Unit
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onDecrease,
                enabled = quantity > 0,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Filled.KeyboardArrowDown, contentDescription = "Decrease quantity")
            }

            Text(
                text = quantity.toString(),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 8.dp),
                textAlign = TextAlign.Center
            )

            IconButton(
                onClick = onIncrease,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(Icons.Filled.KeyboardArrowUp, contentDescription = "Increase quantity")
            }
        }
    }

//        bannerAd = findViewById(R.id.banner_ad)
//
//        findViewById<Button>(R.id.request_banner_button).setOnClickListener {
//            onRequestBannerButtonPressed()
//        }
//
//        pizzaSpinner = findViewById<Spinner>(R.id.pizzas_spinner).apply {
//            adapter = ArrayAdapter(
//                this@MainActivity, android.R.layout.simple_spinner_dropdown_item, orderCount)
//        }
//        pizzaSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                updateTotal()
//            }
//
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//
//            }
//        }
//        saladSpinner = findViewById<Spinner>(R.id.salads_spinner).apply {
//            adapter = ArrayAdapter(
//                this@MainActivity, android.R.layout.simple_spinner_dropdown_item, orderCount)
//        }
//        saladSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                updateTotal()
//            }
//
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//
//            }
//        }
//        cookieSpinner = findViewById<Spinner>(R.id.cookies_spinner).apply {
//            adapter = ArrayAdapter(
//                this@MainActivity, android.R.layout.simple_spinner_dropdown_item, orderCount)
//        }
//        cookieSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
//                updateTotal()
//            }
//
//            override fun onNothingSelected(p0: AdapterView<*>?) {
//
//            }
//        }

//        lifecycleScope.launch {
//            if (!runtimeAwareSdk.initialize()) {
//                makeToast("Failed to initialize SDK")
//            } else {
//                makeToast("Initialized SDK!")
//            }
//        }
//    }

    data class Message(val author: String, val body: String)

    @Composable
    fun MessageCard(msg: Message) {
        // Add padding around our message
        Row(modifier = Modifier.padding(all = 8.dp)) {
            Image(
                painter = painterResource(R.drawable.cookie),
                contentDescription = "Contact profile picture",
                modifier = Modifier
                    // Set image size to 40 dp
                    .size(40.dp)
                    // Clip image to be shaped as a circle
                    .clip(CircleShape)
                    .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
            )

            // Add a horizontal space between the image and the column
            Spacer(modifier = Modifier.width(8.dp))

            // We keep track if the message is expanded or not in this
            // variable
            var isExpanded by remember { mutableStateOf(false) }
            // surfaceColor will be updated gradually from one color to the other
            val surfaceColor by animateColorAsState(
                if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
            )

            // We toggle the isExpanded variable when we click on this Column
            Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
                Text(
                    text = msg.author,
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleSmall
                )
                // Add a vertical space between the author and message texts
                Spacer(modifier = Modifier.height(4.dp))

                Surface(
                    shape = MaterialTheme.shapes.medium,
                    shadowElevation = 1.dp,
                    // surfaceColor color will be changing gradually from primary to surface
                    color = surfaceColor,
                    // animateContentSize will change the Surface size gradually
                    modifier = Modifier
                        .animateContentSize()
                        .padding(1.dp)
                ) {
                    Text(
                        text = msg.body,
                        modifier = Modifier.padding(all = 4.dp),
                        // If the message is expanded, we display all its content
                        // otherwise we only display the first line
                        maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }

    @Composable
    fun Conversation(messages: List<Message>) {
        LazyColumn {
            items(messages) { message ->
                MessageCard(message)
            }
        }
    }

    private fun onRequestBannerButtonPressed() = lifecycleScope.launch {
        bannerAd.loadAd(
            this@MainActivity,
            APP_DISPLAY_NAME,
            total,
            shouldStartActivityPredicate(),
            false,
            onPaymentComplete()
        )
    }

    private fun updateTotal() {
        total =
            9.99 * pizzaSpinner.selectedItemPosition + 7.5 * saladSpinner.selectedItemPosition + 2.75 * cookieSpinner.selectedItemPosition
        val textView = findViewById<TextView>(R.id.totalText)
        textView.text =
            textView.context.getString(R.string.total_text, "$total")
    }

    private fun onPaymentComplete(): () -> Unit {
        return {
            val textView = findViewById<TextView>(R.id.totalText)
            textView.text =
                textView.context.getString(R.string.total_text, "$total - Paid!")
            makeToast("Payment complete!")
        }
    }

    private fun shouldStartActivityPredicate(): () -> Boolean {
        return { true }
    }

    private fun makeToast(message: String) {
        runOnUiThread { Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show() }
    }

    companion object {
        private const val TAG = "SandboxClient"

        /**
         * Package name of this app. This is something that the SDK might use the identify this
         * particular app client.
         *
         * (Note that in this particular sample it's used to build the banner view label).
         */
        private const val PACKAGE_NAME = "com.example.privacysandbox.client"
        private const val APP_DISPLAY_NAME = "Food Flinger"
    }
}
