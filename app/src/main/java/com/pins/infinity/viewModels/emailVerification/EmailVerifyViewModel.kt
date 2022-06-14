package com.pins.infinity.viewModels.emailVerification

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.MutableLiveData
import android.content.Intent
import com.pins.infinity.R
import com.pins.infinity.activity.emailVerification.EmailVerifyConfirmActivity
import com.pins.infinity.repositories.UserRepository
import com.pins.infinity.utility.isEmailValid
import com.pins.infinity.viewModels.base.BaseViewModel

/**
 * Created by Pavlo Melnyk on 15.01.2019.
 */

class EmailVerifyViewModel(application: Application,
                           private val userRepository: UserRepository)
    : BaseViewModel(application) {

    val userEmail: MutableLiveData<String> = MutableLiveData()

    @SuppressLint("CheckResult")
    fun continueCommand() {
        if (!userEmail.isEmailValid()) {
            errorDialog.value = R.string.error_email
            return
        }

        isProgressDialog.value = true
        userRepository.initVerifyUserEmail(userEmail.value!!).subscribe({ response ->
            isProgressDialog.value = false
            showResultView(response)

        }, { throwable ->
            isProgressDialog.value = false
            showError(throwable, R.string.error_title)
        })
    }

    private fun showResultView(isSuccess: Boolean) {
        val intent = Intent(context(), EmailVerifyConfirmActivity::class.java)
        intent.putExtra(EMAIL_VERIFY_RESULT_KEY, isSuccess)
        startActivity.value = intent
    }

    companion object {
        const val EMAIL_VERIFY_RESULT_KEY = "email_verify_result_key"
    }
}