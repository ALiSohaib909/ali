package com.pins.infinity.activity.payment

import android.os.Bundle
import android.util.Log
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import com.android.billingclient.api.SkuDetailsResponseListener
import com.pins.infinity.R
import com.pins.infinity.activity.base.BaseActivity
import com.pins.infinity.dialogs.DialogService
import com.pins.infinity.services.billing.BillingDetailsModel
import com.pins.infinity.services.billing.BillingManager
import com.pins.infinity.services.billing.skuSubscriptionList
import com.pins.infinity.viewModels.PaymentGoogleViewModel
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * Created by Pavlo Melnyk on 2018-12-06.
 */
class PaymentGoogleActivity :
        BaseActivity<PaymentGoogleViewModel>(),
        BillingManager.BillingUpdatesListener {

    override val layout: Int = R.layout.activity_payment_google
    override val viewModel: PaymentGoogleViewModel  by viewModel {
        parametersOf(intent.getParcelableExtra(PaymentSummaryActivity.CHOSEN_OPTION), intent.getStringExtra(SKU))
    }
    private val dialogService: DialogService  by inject()

    private val billingDetailsModel = BillingDetailsModel()
    private lateinit var billingManager: BillingManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        billingManager = BillingManager(this, this)
    }

    override fun onResume() {
        super.onResume()
        if(billingDetailsModel.shouldShowGooglePayment){
            Log.d(BillingManager.TAG, "mShowPaymentDialog: true")
            showGooglePayment()
        }
    }

    override fun onBillingClientSetupFinished() {
        billingManager.querySkuDetailsAsync(BillingClient.SkuType.SUBS, skuSubscriptionList, SkuDetailsResponseListener{

            responseCode, skuDetailsList ->
            onSkuDetailsResponse(responseCode, skuDetailsList, billingDetailsModel)
        })
    }

    override fun onPurchasesUpdated(purchases: List<Purchase>) {
        if (purchases.isEmpty()) {
            billingDetailsModel.shouldShowGooglePayment = true
            return
        }
        billingDetailsModel.isSubscriptionValid = true
        billingDetailsModel.shouldShowGooglePayment = false
        viewModel.onSuccessfulPayment(purchases[0].signature)
    }

    override fun onUserCancels() {
        viewModel.continueCommand(false)
    }

    override fun onPurchaseError() {
        viewModel.continueCommand(false)
    }

    private fun onSkuDetailsResponse(responseCode: Int, skuDetailsList: List<SkuDetails>?, billingDetailsModel: BillingDetailsModel) {
        when {
            responseCode != BillingClient.BillingResponse.OK -> {
                dialogService.showAlertOkDialog(this,
                        this.getString(R.string.error_title),
                        this.getString(R.string.error_cannot_get_prices))
                return
            }
            skuDetailsList == null -> return
            skuDetailsList.isNotEmpty() -> {
                billingDetailsModel.skuDetailsList = skuDetailsList
            }
        }

        if (!billingDetailsModel.isSubscriptionValid) {
            billingDetailsModel.shouldShowGooglePayment = false

            showGooglePayment()
        }
    }

    private fun showGooglePayment() = billingManager.initiatePurchaseFlow(viewModel.sku)

    companion object {
        const val SKU = "sku"
    }
}