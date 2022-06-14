package com.pins.infinity.repositories

import android.annotation.SuppressLint
import android.app.Application
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.microsoft.appcenter.AppCenter
import com.pins.infinity.activity.ActivityLogin
import com.pins.infinity.api.deviceapi.DeviceApiManager
import com.pins.infinity.api.devicemodels.RemoteLoginRequest
import com.pins.infinity.api.devicemodels.SendTokenRequest
import com.pins.infinity.utility.Utility
import io.reactivex.Single

/**
 * Created by Pavlo Melnyk on 21.02.2019.
 */
class AuthoriseRepositoryImpl(private val application: Application, private val deviceApiManager: DeviceApiManager)
    : AuthoriseRepository {

    @SuppressLint("CheckResult")
    override fun remoteLogin(imei: String): Single<Boolean> {
        return deviceApiManager.remoteLogin(RemoteLoginRequest(imei)).flatMap { user ->
            System.out.print(user.code)

            return@flatMap deviceApiManager.sendToken(SendTokenRequest()).map { device ->
                val userEmail = user.response.user.email
                AppCenter.setUserId(userEmail)

                val jsonStr = jacksonObjectMapper().writeValueAsString(user)

                ActivityLogin.saveUserData(application, jsonStr, Utility.getImei(application), userEmail)
                ActivityLogin.sendSms(application)

                return@map true
            }
        }
    }

}