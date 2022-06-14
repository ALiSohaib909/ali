package com.pins.infinity.activity.payment

import android.os.Bundle
import com.android.billingclient.api.SkuDetails
import com.pins.infinity.R
import com.pins.infinity.activity.base.BaseActivity
import com.pins.infinity.services.billing.BillingDetailsModel
import com.pins.infinity.services.billing.BillingManager
import com.pins.infinity.viewModels.PaymentPlanViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * Created by Pavlo Melnyk on 29.11.2018.
 */
class PaymentPlanActivity: BillingManager.BillingSkuDetailsListener, BaseActivity<PaymentPlanViewModel>() {

    private lateinit var paymentOption: String
    override val layout: Int = R.layout.activity_payment_plan


    private lateinit var billingDetailsModel: BillingDetailsModel
    private var billingManager: BillingManager? = null

    override val viewModel: PaymentPlanViewModel  by viewModel {
        paymentOption = intent.getStringExtra(PAYMENT_OPTION)
        parametersOf(paymentOption)
    }

    companion object {
        const val PAYMENT_OPTION = "payment_option"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(paymentOption == PaymentPlanViewModel.PaymentOptions.GOOGLE_PAY.payment) {
            billingManager = BillingManager(this, this)
            billingDetailsModel = BillingDetailsModel()
        }
    }

    override fun onSkuDetailsReceived(details: List<SkuDetails>) {
        isProgressDialog(false)
        viewModel.getGooglePayPrices()
        billingManager?.destroy()
        billingManager = null
    }

    override fun onSkuDetailsError() {
        isProgressDialog(false)
        viewModel.isError.set(true)
        viewModel.onErrorGettingGooglePayPrices()
        billingManager?.destroy()
    }
}