package com.pins.infinity.viewModels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.pins.infinity.R
import com.pins.infinity.extensions.default
import com.pins.infinity.repositories.PaymentRepository
import com.pins.infinity.utility.callUssd


/**
 * Created by Pavlo Melnyk on 2018-12-07.
 */
class PaymentUssdViewModel(application: Application,
                           private val paymentRepository: PaymentRepository)
    : BasePaymentViewModel(application) {

    var ussdCode = MutableLiveData<String>().default("")
    var isDialing = MutableLiveData<Boolean>().default(false)

    init {
        isProgressDialog.value = true
        paymentRepository.getUssdCode().subscribeAndDispose({ ussd ->
            isProgressDialog.value = false
            ussdCode.value = ussd
        }, { throwable ->
            isProgressDialog.value = false
            showError(throwable, R.string.error_title)
        })
    }

    fun continueCommand() {
        ussdCode.value?.run {
            isDialing.value = context().callUssd(this)
        }
    }


    companion object {
        const val TEL_TAG = "tel:"
    }
}