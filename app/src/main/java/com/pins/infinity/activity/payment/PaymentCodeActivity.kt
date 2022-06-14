package com.pins.infinity.activity.payment

import com.pins.infinity.R
import com.pins.infinity.activity.base.BaseActivity
import com.pins.infinity.viewModels.PaymentCodeViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * Created by Pavlo Melnyk on 2018-12-04.
 */
class PaymentCodeActivity : BaseActivity<PaymentCodeViewModel>() {

    override val layout: Int = R.layout.activity_payment_code

    override val viewModel: PaymentCodeViewModel  by viewModel{
        parametersOf(intent.getParcelableExtra(PaymentSummaryActivity.CHOSEN_OPTION))
    }
}