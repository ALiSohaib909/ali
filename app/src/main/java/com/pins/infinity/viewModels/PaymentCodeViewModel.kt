package com.pins.infinity.viewModels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import android.content.Intent
import androidx.databinding.ObservableBoolean
import com.pins.infinity.R
import com.pins.infinity.activity.payment.PaymentResultActivity
import com.pins.infinity.repositories.PaymentRepository
import com.pins.infinity.repositories.models.StartTransactionModel

/**
 * Created by Pavlo Melnyk on 2018-12-04.
 */
class PaymentCodeViewModel(application: Application,
                           private val paymentRepository: PaymentRepository,
                           private val chosenOption: PaymentPlanViewModel.ChosenOption)
    : BasePaymentViewModel(application) {

    var isSuccess = ObservableBoolean()
    var activationCode = MutableLiveData<String>()

    fun continueCommand() {
        if (activationCode.value.isNullOrEmpty()) showError(errorRes = R.string.error_code_empty_message)

        val code = activationCode.value ?: return

        isProgressDialog.value = true
        paymentRepository.payByActivationCode(StartTransactionModel(chosenOption, code)).subscribeAndDispose({ complete ->
            isProgressDialog.value = false
            isSuccess.set(!complete.error)

            val intent = Intent(context(), PaymentResultActivity::class.java)
            intent.putExtra(PaymentResultActivity.IS_SUCCESS, isSuccess.get())
            startActivity.value = intent
        }, {
            isProgressDialog.value = false
            showError(it, R.string.error_title)
        })
    }
}