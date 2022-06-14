package com.pins.infinity.viewModels.applock

import android.app.Application
import androidx.lifecycle.MutableLiveData
import android.content.Intent
import com.pins.infinity.R
import com.pins.infinity.activity.applock.ForgotPinCheckCodeActivity
import com.pins.infinity.usecases.ForgotPinInitUseCase
import com.pins.infinity.utility.isEmailValid
import com.pins.infinity.viewModels.base.BaseViewModel

/**
 * Created by Pavlo Melnyk on 02.05.2019.
 */

const val INIT_RECOVERY_TOKEN = "init_recovery_token"

class AppLockForgotPinViewModel(
        application: Application,
        private val forgotPinUseCase: ForgotPinInitUseCase)
    : BaseViewModel(application) {

    var email = MutableLiveData<String>()

    fun forgotPinCommand() {
        if (!email.isEmailValid()) {
            errorDialog.value = R.string.error_email
            return
        }

        val email = email.value ?: return
        isProgressDialog.value = true
        forgotPinUseCase.execute(email).subscribeAndDispose(
                onSuccess = {
                    isProgressDialog.value = false
                    startActivity.value = Intent(context(), ForgotPinCheckCodeActivity::class.java).apply {
                        putExtra(INIT_RECOVERY_TOKEN, it.token)
                    }
                },
                onError = {
                    isProgressDialog.value = false
                    showErrorMessage(it)
                }
        )
    }
}