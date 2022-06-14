/*
 * Copyright 2017 Google Inc. All Rights Reserved.
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
package com.pins.infinity.services.billing

import android.app.Activity
import android.util.Log

import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponse
import com.android.billingclient.api.BillingClient.FeatureType
import com.android.billingclient.api.BillingClient.SkuType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.Purchase.PurchasesResult
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsParams
import com.android.billingclient.api.SkuDetailsResponseListener
import com.pins.infinity.BuildConfig
import com.pins.infinity.database.daos.BillingDao
import com.pins.infinity.database.daos.PaymentDao
import com.pins.infinity.database.models.SkuDetailsItem
import com.pins.infinity.viewModels.PaymentPlanViewModel
import org.koin.core.KoinComponent
import org.koin.core.inject


import java.io.IOException
import java.util.ArrayList


/**
 * Created by Pavlo Melnyk on 31.08.2018.
 */

/**
 * Handles all the interactions with Play Store (via Billing library), maintains connection to
 * it through BillingClient and caches temporary states/data if needed
 */
class BillingManager : PurchasesUpdatedListener, SkuDetailsResponseListener, KoinComponent {

    /**
     * A reference to BillingClient
     */
    private var billingClient: BillingClient? = null

    /**
     * True if billing service is connected now.
     */
    private var isServiceConnected: Boolean = false

    private val billingUpdatesListener: BillingUpdatesListener?

    private val billingSkuDetailsListener: BillingSkuDetailsListener?

    private val activity: Activity

    private val purchases = ArrayList<Purchase>()

    private val skuDetailsItems = ArrayList<SkuDetailsItem>()

    private var billingClientResponseCode = BILLING_MANAGER_NOT_INITIALIZED

    private val billingDao: BillingDao by inject()
    private val paymentDao: PaymentDao by inject()

    /**
     * Listener to the updates that happen when purchases list was updated or consumption of the
     * item was finished
     */
    interface BillingUpdatesListener {
        fun onBillingClientSetupFinished()

        fun onPurchasesUpdated(purchases: List<Purchase>)

        fun onUserCancels()

        fun onPurchaseError()
    }

    /**
     * Listener to the updates that happen when sku list was synced or error while syncing
     * occurred
     */
    interface BillingSkuDetailsListener {
        fun onSkuDetailsReceived(details: List<SkuDetails>)

        fun onSkuDetailsError()
    }

    constructor(activity: Activity, updatesListener: BillingUpdatesListener) {
        this.activity = activity
        billingUpdatesListener = updatesListener
        billingSkuDetailsListener = null
        billingClient = BillingClient.newBuilder(this.activity).setListener(this).build()

        startServiceConnection(Runnable {
            // Notifying the listener that billing client is ready
            billingUpdatesListener.onBillingClientSetupFinished()
            queryPurchases()
        })
    }

    constructor(activity: Activity, detailsListener: BillingSkuDetailsListener) {
        this.activity = activity
        billingUpdatesListener = null
        billingSkuDetailsListener = detailsListener
        billingClient = BillingClient.newBuilder(this.activity).setListener(this).build()

        startServiceConnection(Runnable {
            querySkuDetailsAsync(SkuType.SUBS, skuSubscriptionList, this)
        })
    }

    override fun onSkuDetailsResponse(responseCode: Int, skuDetailsList: List<SkuDetails>) {
        if (skuDetailsList.count() < 1) {
            billingSkuDetailsListener!!.onSkuDetailsError()
        } else {
            skuDetailsList.forEach {
                skuDetailsItems.add(SkuDetailsItem(sku = it.sku, price = it.price))
            }
            updateWithGooglePayPrices(skuDetailsList)
            billingDao.clearSkuDetails()
            billingDao.insertSkuDetails(skuDetailsItems)

            billingSkuDetailsListener!!.onSkuDetailsReceived(skuDetailsList)
        }
    }

    private fun updateWithGooglePayPrices(skuDetailsList: List<SkuDetails>) {
        val plans = paymentDao.getPlanItems().blockingGet()

        plans.forEach { plan ->
            plan.monthPrice = skuDetailsList.single { it.sku == getSkuForChosenOption(plan.plan, PaymentPlanViewModel.DurationOption.MONTH.duration) }.getPriceWithoutCurrency()
            plan.yearPrice = skuDetailsList.single { it.sku == getSkuForChosenOption(plan.plan, PaymentPlanViewModel.DurationOption.YEAR.duration) }.getPriceWithoutCurrency()
            plan.currency = skuDetailsList[0].priceCurrencyCode
        }

        paymentDao.insertPlans(plans)
    }

    private fun SkuDetails.getPriceWithoutCurrency(): String = this.price.replace(this.priceCurrencyCode, "").replace("\\s", "")


    /**
     * Handle a callback that purchases were updated from the Billing library
     */
    override fun onPurchasesUpdated(resultCode: Int, purchases: List<Purchase>?) {
        when (resultCode) {
            BillingResponse.OK -> {
                for (purchase in purchases!!) {
                    handlePurchase(purchase)
                }
                billingUpdatesListener!!.onPurchasesUpdated(this.purchases)
            }
            BillingResponse.USER_CANCELED -> {
                Log.i(TAG, "onPurchasesUpdated() - user cancelled the purchase flow - skipping")
                billingUpdatesListener!!.onUserCancels()
            }
            else -> {
                billingUpdatesListener!!.onPurchaseError()
                Log.w(TAG, "onPurchasesUpdated() got unknown resultCode: $resultCode")
            }
        }
    }

    /**
     * Start a purchase flow
     */
    fun initiatePurchaseFlow(skuId: String) {
        val purchaseFlowRequest = Runnable {
            val purchaseParams = BillingFlowParams.newBuilder()
                    .setSku(skuId)
                    .setType(BillingClient.SkuType.SUBS)
                    .build()
            billingClient!!.launchBillingFlow(activity, purchaseParams)
        }

        executeServiceRequest(purchaseFlowRequest)
    }

    /**
     * Clear the resources
     */
    fun destroy() {
        Log.d(TAG, "Destroying the manager.")
        billingClient?.isReady.let {
            billingClient!!.endConnection()
            billingClient = null
        }
    }

    fun querySkuDetailsAsync(@SkuType itemType: String, skuList: List<String>,
                             listener: SkuDetailsResponseListener) {
        val queryRequest = Runnable {
            val params = SkuDetailsParams.newBuilder()
            params.setSkusList(skuList).setType(itemType)
            billingClient!!.querySkuDetailsAsync(params.build(), listener)
        }

        executeServiceRequest(queryRequest)
    }

    /**
     * Handles the purchase
     *
     * Note: Notice that for each purchase, we check if signature is valid on the client.
     * It's recommended to move this check into your backend.
     * //     * See [BillingSecurity.verifyPurchase]
     *
     *
     * @param purchase Purchase to be handled
     */
    private fun handlePurchase(purchase: Purchase) {
        if (!verifyValidSignature(purchase.originalJson, purchase.signature)) {
            Log.i(TAG, "Got a purchase: $purchase; but signature is bad. Skipping...")
            return
        }

        Log.d(TAG, "Got a verified purchase: $purchase")

        purchases.add(purchase)
    }

    /**
     * Handle a result from querying of purchases and report an updated list to the listener
     */
    private fun onQueryPurchasesFinished(result: PurchasesResult) {
        // Have we been disposed of in the meantime? If so, or bad result code, then quit
        if (billingClient == null || result.responseCode != BillingResponse.OK) {
            Log.w(TAG, "Billing client was null or result code (" + result.responseCode
                    + ") was bad - quitting")
            return
        }

        Log.d(TAG, "Query inventory was successful.")

        // Update the UI and purchases inventory with new list of purchases
        purchases.clear()
        onPurchasesUpdated(BillingResponse.OK, result.purchasesList)
    }

    /**
     * Checks if subscriptions are supported for current client
     *
     * Note: This method does not automatically retry for RESULT_SERVICE_DISCONNECTED.
     * It is only used in unit tests and after queryPurchases execution, which already has
     * a retry-mechanism implemented.
     *
     */
    private fun areSubscriptionsSupported(): Boolean {
        val responseCode = billingClient!!.isFeatureSupported(FeatureType.SUBSCRIPTIONS)
        if (responseCode != BillingResponse.OK) {
            Log.w(TAG, "areSubscriptionsSupported() got an error response: $responseCode")
        }
        return responseCode == BillingResponse.OK
    }

    /**
     * Query purchases across various use cases and deliver the result in a formalized way through
     * a listener
     */
    private fun queryPurchases() {
        val queryToExecute = Runnable {
            val time = System.currentTimeMillis()
            val purchasesResult = billingClient!!.queryPurchases(SkuType.INAPP)
            Log.i(TAG, "Querying purchases elapsed time: " + (System.currentTimeMillis() - time)
                    + "ms")
            // If there are subscriptions supported, we add subscription rows as well
            if (areSubscriptionsSupported()) {
                val subscriptionResult = billingClient!!.queryPurchases(SkuType.SUBS)
                Log.i(TAG, "Querying purchases and subscriptions elapsed time: "
                        + (System.currentTimeMillis() - time) + "ms")
                Log.i(TAG, "Querying subscriptions result code: "
                        + subscriptionResult.responseCode
                        + " res: " + subscriptionResult.purchasesList.size)

                if (subscriptionResult.responseCode == BillingResponse.OK) {
                    purchasesResult.purchasesList.addAll(
                            subscriptionResult.purchasesList)
                } else {
                    Log.e(TAG, "Got an error response trying to query subscription purchases")
                }
            } else if (purchasesResult.responseCode == BillingResponse.OK) {
                Log.i(TAG, "Skipped subscription purchases query since they are not supported")
            } else {
                Log.w(TAG, "queryPurchases() got an error response code: " + purchasesResult.responseCode)
            }
            onQueryPurchasesFinished(purchasesResult)
        }

        executeServiceRequest(queryToExecute)
    }

    private fun startServiceConnection(executeOnSuccess: Runnable?) {
        billingClient!!.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(@BillingResponse billingResponseCode: Int) {
                Log.d(TAG, "Setup finished. Response code: $billingResponseCode")

                if (billingResponseCode == BillingResponse.OK) {
                    isServiceConnected = true
                    executeOnSuccess?.run()
                }
                billingClientResponseCode = billingResponseCode
            }

            override fun onBillingServiceDisconnected() {
                isServiceConnected = false
            }
        })
    }

    private fun executeServiceRequest(runnable: Runnable) {
        if (isServiceConnected) {
            runnable.run()
        } else {
            startServiceConnection(runnable)
        }
    }

    private fun verifyValidSignature(signedData: String, signature: String): Boolean {
        if (BuildConfig.BASE_64_ENCODED_PUBLIC_KEY.contains("CONSTRUCT_YOUR")) {
            throw RuntimeException("Please update your app's public key at: " + "BASE_64_ENCODED_PUBLIC_KEY")

        }

        return try {
            BillingSecurity.verifyPurchase(BuildConfig.BASE_64_ENCODED_PUBLIC_KEY, signedData, signature)
        } catch (e: IOException) {
            Log.e(TAG, "Got an exception trying to validate a purchase: $e")
            false
        }
    }

    companion object {
        private val BILLING_MANAGER_NOT_INITIALIZED = -1

        val TAG = "BillingManager"
    }
}

