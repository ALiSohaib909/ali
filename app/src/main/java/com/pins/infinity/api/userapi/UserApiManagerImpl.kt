package com.pins.infinity.api.userapi

import android.content.Context
import com.pins.infinity.api.BaseApiManager
import com.pins.infinity.api.devicemodels.DeviceResponse
import com.pins.infinity.api.models.EmailVerifyResponse
import com.pins.infinity.api.services.UserApiService
import com.pins.infinity.api.usermodels.UserDangerRequest
import com.pins.infinity.api.usermodels.UserRequest
import com.pins.infinity.api.usermodels.UserResponse
import com.pins.infinity.api.utils.toApiStringBoolean
import com.pins.infinity.usecases.DangerTriggeredUseCase
import io.reactivex.Completable
import io.reactivex.Single

/**
 * Created by Pavlo Melnyk on 2018-12-17.
 */
class UserApiManagerImpl(context: Context, private val userApiService: UserApiService) :
    BaseApiManager(context), UserApiManager {

    override fun getUser(userId: String): Single<UserResponse> =
        userApiService.getUser(userId).makeRequest()

    override fun dangerTriggered(
        dangerType: DangerTriggeredUseCase.DangerType,
        request: UserDangerRequest
    ): Completable {

        return when (dangerType) {
            DangerTriggeredUseCase.DangerType.THEFT_DETECT -> userApiService.theftDetect(
                country = request.country,
                address = request.address,
                latitude = request.latitude,
                serial_number = request.serialNumber,
                phone_model = request.model,
                country_code = request.countryCode,
                carrier = request.carrier,
                account_id = request.accountId,
                sim_id = request.simId,
                imei = request.imei,
                state = request.state,
                msisdn = request.msisdn,
                longitude = request.longitude)
            DangerTriggeredUseCase.DangerType.FLIGHT_MODE_DETECT -> userApiService.flightModeDetect(
                country = request.country,
                address = request.address,
                latitude = request.latitude,
                serial_number = request.serialNumber,
                phone_model = request.model,
                country_code = request.countryCode,
                carrier = request.carrier,
                account_id = request.accountId,
                sim_id = request.simId,
                imei = request.imei,
                state = request.state,
                msisdn = request.msisdn,
                longitude = request.longitude)
            DangerTriggeredUseCase.DangerType.SIM_DETECT ->userApiService.simDetect(
                country = request.country,
                address = request.address,
                latitude = request.latitude,
                serial_number = request.serialNumber,
                phone_model = request.model,
                country_code = request.countryCode,
                carrier = request.carrier,
                account_id = request.accountId,
                sim_id = request.simId,
                imei = request.imei,
                state = request.state,
                msisdn = request.msisdn,
                longitude = request.longitude)
        }
    }

    override fun getUserDevice(imei: String): Single<DeviceResponse> =
        userApiService.getUserDevice(imei).makeRequest()

    override fun updateUser(user: UserRequest): Single<UserResponse> =
        userApiService.updateUser(
            user.userId,
            user.firstName,
            user.lastName,
            user.phone,
            user.email,
            user.activeIntruder?.toApiStringBoolean()
        ).makeRequest()

    override fun verifyUserEmail(userId: String): Single<EmailVerifyResponse> =
        userApiService.userEmailVerify(userId).makeRequest()
}