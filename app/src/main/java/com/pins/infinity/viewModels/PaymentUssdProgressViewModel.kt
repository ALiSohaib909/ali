package com.pins.infinity.viewModels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import android.content.Intent
import com.pins.infinity.R
import com.pins.infinity.activity.payment.PaymentResultActivity
import com.pins.infinity.extensions.default
import com.pins.infinity.repositories.PaymentRepository

/**
 * Created by Pavlo Melnyk on 2018-12-09.
 */
class PaymentUssdProgressViewModel(application: Application,
                                   private val paymentRepository: PaymentRepository,
                                   private val isDial: Boolean)
    : BasePaymentViewModel(application) {

    var isProgressVisible = MutableLiveData<Boolean>().default(true)
    var isSuccessfulPayment = MutableLiveData<Boolean>().default(false)

    fun onBackToAppCheckPaymentStatus() {
        if (isDial) {
            paymentRepository.isSubscriptionValid().subscribeAndDispose({ isValid ->
                isProgressVisible.value = false
                isSuccessfulPayment.value = isValid
                showResult()
            }, { throwable ->
                showError(throwable, R.string.error_title)
                isProgressVisible.value = false
            })
        } else {
            showError(errorRes = R.string.error_title)
        }
    }

    fun continueCommand() {
        showResult()
    }

    private fun showResult() {
        val intent = Intent(context(), PaymentResultActivity::class.java)
        intent.putExtra(PaymentResultActivity.IS_SUCCESS, isSuccessfulPayment.value!!)
        startActivity.value = intent
    }
}