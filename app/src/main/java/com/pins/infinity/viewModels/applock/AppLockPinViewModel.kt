package com.pins.infinity.viewModels.applock

import android.app.Application
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.pins.infinity.activity.applock.AppLockForgotPinActivity
import com.pins.infinity.api.exceptions.NoInternetConnectionException
import com.pins.infinity.api.exceptions.UnlockAppLockedException
import com.pins.infinity.extensions.default
import com.pins.infinity.usecases.GetAppLockPinUseCase
import com.pins.infinity.usecases.SaveAppLockPinUseCase
import com.pins.infinity.usecases.TemporaryUnlockAppUseCase
import com.pins.infinity.usecases.UnlockUseCase
import com.pins.infinity.viewModels.base.BaseViewModel

/**
 * Created by Pavlo Melnyk on 24.04.2019.
 */
class AppLockPinViewModel(
        application: Application,
        private val unlockUseCase: UnlockUseCase,
        private val lockPackage: String,
        private val unlockAppUseCase: TemporaryUnlockAppUseCase,
        private val saveAppLockPinUseCase: SaveAppLockPinUseCase,
        private val getAppLockPinUseCase: GetAppLockPinUseCase
) : BaseViewModel(application) {

    var pin = MutableLiveData<String>()
    var isWrongPinHintVisible = MutableLiveData<Boolean>().default(false)

    fun unlockCommand() {
        val pin = pin.value ?: return
        isProgressDialog.value = true

        unlockUseCase.execute(UnlockUseCase.Param(pin = pin, packageName = lockPackage))
                .subscribeAndDispose(
                        onSuccess = {
                            isProgressDialog.value = false
                            saveAppLockPinUseCase.execute(pin)
                            closeActivity.call()
                        },
                        onError = { error ->
                            isProgressDialog.value = false
                            handleErrors(error, pin)
                        })
    }

    private fun handleErrors(error: Throwable, pin: String) {
        when (error) {
            is UnlockAppLockedException.InvalidPin -> {
                isWrongPinHintVisible.value = true
            }
            is NoInternetConnectionException -> handleNoInternetConnection(pin)
        }
    }

    private fun handleNoInternetConnection(pin: String) {
        if (pin == getAppLockPinUseCase.execute()) {
            unlockAppUseCase.execute(lockPackage)
            closeActivity.call()
        } else {
            isWrongPinHintVisible.value = true
        }
    }

    fun forgotPinCommand() {
        startActivity.value = Intent(context(), AppLockForgotPinActivity::class.java)
        if (pin.value == getAppLockPinUseCase.execute()) {
            closeActivity.call()
        } else {
            isWrongPinHintVisible.value = false
        }
    }
}