package com.pins.infinity.viewModels.antitheft

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.MutableLiveData
import com.pins.infinity.R
import com.pins.infinity.activity.antitheft.AntiTheftActivity
import com.pins.infinity.activity.applock.AppLockActivity
import com.pins.infinity.database.SettingsManager
import com.pins.infinity.dialogs.DialogService
import com.pins.infinity.usecases.SaveAppLockPinUseCase
import com.pins.infinity.usecases.SetUpPinUseCase
import com.pins.infinity.viewModels.base.BaseViewModel

/**
 * Created by Pavlo Melnyk on 2018-12-04.
 */

class SetupPinViewModel(application: Application,
                        private val dialogService: DialogService,
                        private val setUpPinUseCase: SetUpPinUseCase,
                        private val setupPinAction: LockAction,
                        private val saveAppLockPinUseCase: SaveAppLockPinUseCase
) : BaseViewModel(application) {

    var pin = MutableLiveData<String>()
    var reenterPin = MutableLiveData<String>()

    lateinit var intent: Intent

    fun continueCommand() {
        val pin = pin.value ?: return
        if (pin != reenterPin.value) {
            showError(errorRes = R.string.error_code_empty_message)
            return
        }

        intent = if (setupPinAction == LockAction.ANTI_THEFT) {
            Intent(context(), AntiTheftActivity::class.java)
        } else {
            Intent(context(), AppLockActivity::class.java)
        }

        isProgressDialog.value = true

        setUpPinUseCase.execute(pin)
                .subscribeAndDispose(
                        onSuccess = {
                            saveAppLockPinUseCase.execute(pin)
                            startActivity.value = intent
                            closeActivity.call()
                        },
                        onError = ::showErrorMessage
                )
    }

    fun showInfoDialog(context: Context) =
            dialogService.informationDialog(context, R.layout.app_locker_prompt_popup)

    companion object {
        const val ACTION_KEY = "action_key"
    }
}

enum class LockAction(val option: String) {
    ANTI_THEFT("key_anti_theft"),
    APP_LOCK("key_app_lock"),
    WRONG_COMMAND("key_wrong_command");
}