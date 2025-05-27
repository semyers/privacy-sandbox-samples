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
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.privacysandbox.client.R
import com.runtimeaware.sdk.BannerAd
import com.runtimeaware.sdk.ExistingSdk
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    /** Container for rendering content from the SDK. */
    private lateinit var bannerAd: BannerAd

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bannerAd = findViewById(R.id.banner_ad)

        findViewById<Button>(R.id.request_banner_button).setOnClickListener {
            onRequestBannerButtonPressed()
        }

        pizzaSpinner = findViewById<Spinner>(R.id.pizzas_spinner).apply {
            adapter = ArrayAdapter(
                this@MainActivity, android.R.layout.simple_spinner_dropdown_item, orderCount)
        }
        pizzaSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateTotal()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
        saladSpinner = findViewById<Spinner>(R.id.salads_spinner).apply {
            adapter = ArrayAdapter(
                this@MainActivity, android.R.layout.simple_spinner_dropdown_item, orderCount)
        }
        saladSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateTotal()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }
        cookieSpinner = findViewById<Spinner>(R.id.cookies_spinner).apply {
            adapter = ArrayAdapter(
                this@MainActivity, android.R.layout.simple_spinner_dropdown_item, orderCount)
        }
        cookieSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                updateTotal()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        lifecycleScope.launch {
            if (!runtimeAwareSdk.initialize()) {
                makeToast("Failed to initialize SDK")
            } else {
                makeToast("Initialized SDK!")
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
        total = 9.99 * pizzaSpinner.selectedItemPosition + 7.5 * saladSpinner.selectedItemPosition + 2.75 * cookieSpinner.selectedItemPosition
        val textView = findViewById<TextView>(R.id.totalText)
        textView.text =
            textView.context.getString(R.string.total_text, "$total")
    }

    private fun onPaymentComplete() : () -> Unit {
        return {
            val textView = findViewById<TextView>(R.id.totalText)
            textView.text =
                textView.context.getString(R.string.total_text, "$total - Paid!")
            makeToast("Payment complete!")
        }
    }

    private fun shouldStartActivityPredicate() : () -> Boolean {
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
