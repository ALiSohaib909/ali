package com.pins.infinity.activity.payment

import com.pins.infinity.R
import com.pins.infinity.activity.base.BaseActivity
import com.pins.infinity.viewModels.PaymentUssdProgressViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * Created by Pavlo Melnyk on 2018-12-09.
 */
class PaymentUssdProgressActivity : BaseActivity<PaymentUssdProgressViewModel>() {

    override val layout: Int = R.layout.activity_payment_ussd_progress

    override val viewModel: PaymentUssdProgressViewModel  by viewModel {
        val isDial = intent.getBooleanExtra(IS_DIAL, false)
        parametersOf(isDial)
    }

    override fun onResume() {
        super.onResume()
        viewModel.onBackToAppCheckPaymentStatus()
    }

    companion object {
        const val IS_DIAL = "is_dial"
    }
}