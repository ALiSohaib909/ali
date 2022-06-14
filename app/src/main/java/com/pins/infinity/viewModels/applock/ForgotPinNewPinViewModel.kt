package com.pins.infinity.viewModels.applock

import android.app.Application
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.pins.infinity.R
import com.pins.infinity.activity.applock.ForgotPinSumUpActivity
import com.pins.infinity.usecases.SetNewPinUseCase
import com.pins.infinity.utility.isCodeValid
import com.pins.infinity.viewModels.base.BaseViewModel
import com.pins.infinity.usecases.SetNewPinUseCase.SetNewPinParam

class ForgotPinNewPinViewModel(
        application: Application,
        private val setNewPinUseCase: SetNewPinUseCase,
        private val checkRecoveryToken: String)
    : BaseViewModel(application) {

    var code = MutableLiveData<String>()

    fun continueCommand() {
        if (!code.isCodeValid()) {
            errorDialog.value = R.string.error_code_empty_message
            return
        }

        val code = code.value ?: return
        isProgressDialog.value = true
        setNewPinUseCase.execute(SetNewPinParam(code = code, token = checkRecoveryToken)).subscribeAndDispose(
                onSuccess = {
                    isProgressDialog.value = false
                    startActivity.value = Intent(context(), ForgotPinSumUpActivity::class.java)
                },
                onError = {
                    isProgressDialog.value = false
                    showErrorMessage(it)
                }
        )
    }
}