package com.pins.infinity.viewModels.applock

import android.app.Application
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.pins.infinity.R
import com.pins.infinity.activity.applock.ForgotPinNewPinActivity
import com.pins.infinity.usecases.CheckSmsCodeUseCase
import com.pins.infinity.usecases.CheckSmsCodeUseCase.CheckCodeParam
import com.pins.infinity.utility.isCodeValid
import com.pins.infinity.viewModels.base.BaseViewModel

const val CHECK_RECOVERY_TOKEN = "check_recovery_token"

class ForgotPinCheckCodeViewModel(
        application: Application,
        private val checkSmsCodeUseCase: CheckSmsCodeUseCase,
        private val initRecoveryToken: String)
    : BaseViewModel(application) {

    var code = MutableLiveData<String>()

    private val _isWrongCode = MutableLiveData<Boolean>()
    val isWrongCode: LiveData<Boolean> = _isWrongCode

    fun continueCommand() {
        if (!code.isCodeValid()) {
            errorDialog.value = R.string.error_code_empty_message
            return
        }

        val code = code.value ?: return
        isProgressDialog.value = true
        checkSmsCodeUseCase.execute(CheckCodeParam(token = initRecoveryToken, code = code)).subscribeAndDispose(
                onSuccess = { recoveryCheck ->
                    isProgressDialog.value = false
                    startActivity.value = Intent(context(), ForgotPinNewPinActivity::class.java).apply {
                        putExtra(CHECK_RECOVERY_TOKEN, recoveryCheck.token)
                    }
                },
                onError = {
                    isProgressDialog.value = false
                    showErrorMessage(it)
                    _isWrongCode.value = false
                }
        )
    }
}