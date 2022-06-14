package com.pins.infinity.viewModels

import android.app.Application
import android.content.Intent
import com.pins.infinity.activity.payment.PaymentActivity
import com.pins.infinity.viewModels.base.BaseViewModel

/**
 * Created by Pavlo Melnyk on 24.02.2020.
 */

open class BasePaymentViewModel(application: Application) : BaseViewModel(application) {

    override fun homeCommand() {
        startActivity.value = Intent(context(), PaymentActivity::class.java)
    }
}