package com.pins.infinity.viewModels

import android.app.Application
import android.content.Intent
import androidx.databinding.ObservableBoolean
import com.pins.infinity.R
import com.pins.infinity.activity.payment.PaymentResultActivity
import com.pins.infinity.repositories.PaymentRepository
import com.pins.infinity.repositories.models.StartTransactionModel

/**
 * Created Pavlo Melnyk on 2018-12-07.
 */
class PaymentGoogleViewModel(application: Application,
                             private val paymentRepository: PaymentRepository,
                             private val chosenOption: PaymentPlanViewModel.ChosenOption,
                             val sku: String)
    : BasePaymentViewModel(application) {

    var isSuccess = ObservableBoolean()

    fun continueCommand(isSuccess: Boolean) {
        val intent = Intent(context(), PaymentResultActivity::class.java)
        intent.putExtra(PaymentResultActivity.IS_SUCCESS, isSuccess)
        startActivity.value = intent
    }

    fun onSuccessfulPayment(signature: String) {
        isProgressDialog.value = true
        paymentRepository.payByActivationCode(StartTransactionModel(chosenOption, signature))
                .subscribeAndDispose({ complete ->
                    isProgressDialog.value = false
                    isSuccess.set(!complete.error)

                    continueCommand(true)
                }, {
                    isProgressDialog.value = false
                    showError(it, R.string.error_title)
                })
    }
}