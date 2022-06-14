package com.pins.infinity.api.userapi

import com.pins.infinity.api.devicemodels.DeviceResponse
import com.pins.infinity.api.models.EmailVerifyResponse
import com.pins.infinity.api.usermodels.UserDangerRequest
import com.pins.infinity.api.usermodels.UserRequest
import com.pins.infinity.api.usermodels.UserResponse
import com.pins.infinity.usecases.DangerTriggeredUseCase
import io.reactivex.Completable
import io.reactivex.Single

/**
 * Created by Pavlo Melnyk on 2018-12-17.
 */
interface UserApiManager{
    fun getUser(userId: String): Single<UserResponse>
    fun dangerTriggered(
        dangerType: DangerTriggeredUseCase.DangerType,
        userDangerRequest: UserDangerRequest
    ): Completable
    fun getUserDevice(imei: String): Single<DeviceResponse>
    fun updateUser(user: UserRequest): Single<UserResponse>
    fun verifyUserEmail(userId: String): Single<EmailVerifyResponse>
}
