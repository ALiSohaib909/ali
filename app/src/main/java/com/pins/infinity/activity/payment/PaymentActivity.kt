package com.pins.infinity.activity.payment

import com.pins.infinity.R
import com.pins.infinity.activity.base.BaseActivity
import com.pins.infinity.viewModels.PaymentViewModel
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Created by Pavlo Melnyk on 27.11.2018.
 */

class PaymentActivity : BaseActivity<PaymentViewModel>() {

    override val layout: Int = R.layout.activity_payment

    override val viewModel: PaymentViewModel  by viewModel()

    override fun onBackPressed() {
        super.onBackPressed()
        moveTaskToBack(true)
    }
}