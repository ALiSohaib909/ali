package com.pins.infinity.viewModels.base

import android.annotation.SuppressLint
import android.app.Application
import android.content.Intent
import android.util.Log
import com.fasterxml.jackson.databind.ObjectMapper
import com.pins.infinity.R
import com.pins.infinity.activity.payment.PaymentActivity
import com.pins.infinity.database.SettingsManager
import com.pins.infinity.database.daos.UserDao
import com.pins.infinity.database.models.UserItem
import com.pins.infinity.model.ProfileBaseModel
import com.pins.infinity.repositories.PaymentRepository
import com.pins.infinity.repositories.UserRepository
import com.pins.infinity.utility.AppSharedPrefrence
import com.pins.infinity.utility.log
import io.reactivex.Single
import java.io.IOException

/**
 * Created by Pavlo Melnyk on 27.11.2018.
 */
class BaseMainViewModel(application: Application,
                        private val userDao: UserDao,
                        private val userRepository: UserRepository,
                        private val paymentRepository: PaymentRepository,
                        private val settingsManager: SettingsManager)
    : BaseViewModel(application) {

    // TODO: This class links
    // old java MVC implementation in ManiActivity with new repository.
    // It will be removed when MainActivity will be formated to kotlin and MVVM

    var emailIsVerified: Boolean = false
    var intruderActive: Boolean = false

    init {
        val userId = settingsManager.userId
        userDao.clearUserItem()
        log("insert user to room with userId: $userId")
        userDao.insertUser(UserItem(userId = userId))

    }

    @SuppressLint("CheckResult")
    fun initPaymentCheck(): Single<Boolean> {
        if (settingsManager.shouldCheckSubscriptionStatus) {
            settingsManager.lastSubscriptionCheck = System.currentTimeMillis()

            return userRepository.initCheck().map { isValid ->
                settingsManager.isSubscriptionValid = isValid
                if (!isValid) {
        //           showPaymentActivity()
                }
                return@map isValid
            }
        } else {
            showPaymentActivity()
            return Single.just(false)
        }
    }

    @SuppressLint("CheckResult")
    fun getIntruderActive(): Single<Boolean> {
        return userDao.getUser().map { response ->
            intruderActive = response.activeIntruder
            Log.d("IntruderActive", intruderActive.toString())
            return@map intruderActive
        }
    }

    @SuppressLint("CheckResult")
    fun getEmailVerified(): Single<Boolean> {
        return userDao.getUser().map { response ->
            emailIsVerified = response.verified
            Log.d("EmailVerified", emailIsVerified.toString())
            return@map emailIsVerified
        }
    }

    @SuppressLint("CheckResult")
    fun checkIfEmailVerified(): Single<Boolean> {
        return userRepository.checkIfEmailVerified().map { response ->
            emailIsVerified = response
            return@map response
        }
    }

    @SuppressLint("CheckResult")
    fun checkIfIntruderEnabled(): Single<Boolean> {
        return userRepository.checkIfIntruderEnabled().map { response ->
            isProgressDialog.value = false
            intruderActive = response
            return@map response
        }
    }

    @SuppressLint("CheckResult")
    fun setIntruderActive(isActive: Boolean): Single<Boolean> {
        isProgressDialog.value = true
        return userRepository.updateIntruderActive(isActive).map { response ->
            isProgressDialog.value = false
            intruderActive = response
            return@map response
        }
    }

    private fun showPaymentActivity() {
        val intent = Intent(context(), PaymentActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)

        startActivity.value = intent
    }
}
