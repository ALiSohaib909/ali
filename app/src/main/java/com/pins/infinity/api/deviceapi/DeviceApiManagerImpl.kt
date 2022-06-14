package com.pins.infinity.api.deviceapi

import android.content.Context
import com.pins.infinity.api.BaseApiManager
import com.pins.infinity.api.devicemodels.DeviceResponse
import com.pins.infinity.api.devicemodels.RemoteLoginRequest
import com.pins.infinity.api.devicemodels.SendTokenRequest
import com.pins.infinity.api.services.DeviceApiService
import com.pins.infinity.api.usermodels.UserResponse
import io.reactivex.Single

/**
 * Created by Pavlo Melnyk on 22.02.2019.
 */
class DeviceApiManagerImpl(
        context: Context,
        private val deviceApiService: DeviceApiService
) : BaseApiManager(context),
        DeviceApiManager {

    override fun remoteLogin(request: RemoteLoginRequest): Single<UserResponse> =
            deviceApiService.remoteLogin(
                    serialNumber = request.serialNumber,
                    imei = request.imei,
                    device = request.device,
                    deviceName = request.deviceName,
                    deviceModel = request.deviceModel,
                    deviceCountry = request.deviceCountry,
                    deviceCountryCode = request.deviceCountryCode)
                    .makeRequest()

    override fun sendToken(request: SendTokenRequest): Single<DeviceResponse> =
            deviceApiService.sendToken(
                    imei = request.imei,
                    deviceToken = request.deviceToken)
                    .makeRequest()
}