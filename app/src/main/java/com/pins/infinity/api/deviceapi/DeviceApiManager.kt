package com.pins.infinity.api.deviceapi

import com.pins.infinity.api.devicemodels.DeviceResponse
import com.pins.infinity.api.devicemodels.RemoteLoginRequest
import com.pins.infinity.api.devicemodels.SendTokenRequest
import com.pins.infinity.api.usermodels.UserResponse
import io.reactivex.Single

/**
 * Created by Pavlo Melnyk on 22.02.2019.
 */
interface DeviceApiManager {
    fun remoteLogin(remoteLoginRequest: RemoteLoginRequest): Single<UserResponse>
    fun sendToken(sendTokenRequest: SendTokenRequest): Single<DeviceResponse>
}