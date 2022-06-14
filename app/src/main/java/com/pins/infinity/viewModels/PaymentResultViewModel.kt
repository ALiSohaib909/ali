package com.pins.infinity.viewModels

import android.app.Application
import android.content.Intent
import androidx.databinding.ObservableBoolean
import com.pins.infinity.activity.MainActivity
import com.pins.infinity.activity.payment.PaymentActivity
import com.pins.infinity.viewModels.base.BaseViewModel

/**
 * Created Pavlo Melnyk on 2018-12-07.
 */
class PaymentResultViewModel(application: Application,
                             isSuccessParameter: Boolean)
    : BasePaymentViewModel(application) {

    var isSuccess = ObservableBoolean()
    init {
        isSuccess.set(isSuccessParameter)
    }

    fun continueCommand() {
        if(isSuccess.get()){
            val intent = Intent(context(), MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity.value  = intent
        } else {
            val intent = Intent(context(), PaymentActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity.value  = intent
        }

    }
}