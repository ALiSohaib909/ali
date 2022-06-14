package com.pins.infinity.viewModels.emailVerification

import android.app.Application
import android.content.Intent
import com.pins.infinity.activity.MainActivity
import com.pins.infinity.viewModels.base.BaseViewModel

/**
 * Created by Pavlo Melnyk on 15.01.2019.
 */

class EmailVerifyConfirmViewModel(application: Application,
                                  isSuccess: Boolean) : BaseViewModel(application) {

    fun continueCommand() {
        val intent = Intent(context(), MainActivity::class.java)
        startActivity.value = intent
    }
}