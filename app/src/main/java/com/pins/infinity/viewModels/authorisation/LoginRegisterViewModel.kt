package com.pins.infinity.viewModels.authorisation

import android.app.Application
import android.content.Intent
import com.pins.infinity.activity.ActivityLogin
import com.pins.infinity.activity.ActivitySignup
import com.pins.infinity.activity.MainActivity
import com.pins.infinity.activity.payment.PaymentActivity
import com.pins.infinity.database.SettingsManager
import com.pins.infinity.dialogs.DialogService
import com.pins.infinity.repositories.AuthoriseRepository
import com.pins.infinity.utility.Utility
import com.pins.infinity.viewModels.base.BaseViewModel

/**
 * Created by Pavlo Melnyk on 21.02.2019.
 */

class LoginRegisterViewModel(application: Application,
                             private val settingsManager: SettingsManager,
                             val dialogService: DialogService,
                             private val authoriseRepository: AuthoriseRepository)
    : BaseViewModel(application) {

    private var shouldShowProgress: Boolean = true
    var userAgreed: Boolean = false
    var remoteLoginSuccess: Boolean = false

    init {
        shouldShowProgress = false
        remoteLogin()
    }

    private fun remoteLogin() {
        val imei = Utility.getImei(context()) ?: return
        isProgressDialog.value = true
        authoriseRepository.remoteLogin(imei).subscribeAndDispose({ isSuccess ->
            isProgressDialog.value = false
            shouldShowProgress = false
            remoteLoginSuccess = isSuccess
            if (isSuccess) {
                nextScreen()
            }
        }, {
            isProgressDialog.value = false
            shouldShowProgress = false
        })
    }

    fun nextScreen() {
        if (userAgreed && remoteLoginSuccess) {
            val subscriptionId = settingsManager.userSubscriptionId

            if (subscriptionId.isEmpty()) {
                val intent = Intent(context(), PaymentActivity::class.java)
                startActivity.value = intent
            }

            val intent = Intent(context(), MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity.value = intent
        }
    }

    fun loginCommand() {
        startActivity.value = Intent(context(), ActivityLogin::class.java)
    }

    fun registerCommand() {
        startActivity.value = Intent(context(), ActivitySignup::class.java)
    }

    fun showProgress() {
        isProgressDialog.value = shouldShowProgress
    }
}