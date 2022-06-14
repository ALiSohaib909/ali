package com.pins.infinity.activity.payment

import com.pins.infinity.R
import com.pins.infinity.activity.base.BaseActivity
import com.pins.infinity.viewModels.PaymentResultViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * Created by Pavlo Melnyk on 2018-12-06.
 */
class PaymentResultActivity: BaseActivity<PaymentResultViewModel>() {

    override val layout: Int = R.layout.activity_payment_result
    override val viewModel: PaymentResultViewModel  by viewModel {
        val extra = intent.getBooleanExtra(IS_SUCCESS, false)
        parametersOf(extra)
    }

    companion object {
        const val IS_SUCCESS = "is_success"
    }

    override fun onBackPressed() {
        viewModel.continueCommand()
    }
}