package com.pins.infinity.api.services

import com.pins.infinity.api.devicemodels.DeviceResponse
import com.pins.infinity.api.usermodels.UserResponse
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by Pavlo Melnyk on 22.02.2019.
 */
interface DeviceApiService {

    @PUT("/device/access/{serialNumber}/{imei}")
    fun remoteLogin(@Path("serialNumber") serialNumber: String,
                    @Path("imei") imei: String,
                    @Query("device") device: String,
                    @Query("device_name") deviceName: String,
                    @Query("device_model") deviceModel: String,
                    @Query("device_country") deviceCountry: String,
                    @Query("device_country_code") deviceCountryCode: String)
            : Single<UserResponse>

    @PUT("/device/token/{imei}")
    fun sendToken(@Path("imei") imei: String,
                  @Query("device_token") deviceToken: String)
            : Single<DeviceResponse>

    @PUT("/device/pin/{account_id}/{imei}")
    fun sendPin(@Path("account_id") accountId: String,
                @Path("imei") imei: String,
                @Query("new_pin") newPin: String)
            : Completable

    @POST("/device/unlock")
    fun unlock(@Query("password") password: String,
               @Query("imei") imei: String)
            : Completable

}