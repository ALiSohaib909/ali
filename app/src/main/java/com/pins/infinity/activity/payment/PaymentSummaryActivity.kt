package com.pins.infinity.activity.payment

import com.pins.infinity.R
import com.pins.infinity.activity.base.BaseActivity
import com.pins.infinity.viewModels.PaymentSummaryViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * Created by Pavlo Melnyk on 2018-12-04.
 */
class PaymentSummaryActivity : BaseActivity<PaymentSummaryViewModel>()  {

    override val layout: Int = R.layout.activity_payment_summary
    override val viewModel: PaymentSummaryViewModel  by viewModel {
        parametersOf(intent.getParcelableExtra(CHOSEN_OPTION))
    }

    companion object {
        const val CHOSEN_OPTION = "ChosenOption"
    }
}