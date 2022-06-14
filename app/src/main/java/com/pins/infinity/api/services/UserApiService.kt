package com.pins.infinity.api.services

import com.pins.infinity.api.devicemodels.DeviceResponse
import com.pins.infinity.api.models.EmailVerifyResponse
import com.pins.infinity.api.models.recovery.RecoveryInitResponse
import com.pins.infinity.api.usermodels.UserResponse
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.*

/**
 * Created by Pavlo Melnyk on 2018-12-17.
 */
interface UserApiService {

    @GET("/users/id/{userId}")
    fun getUser(@Path("userId") userId: String): Single<UserResponse>

    @POST("theft/detect")
    fun theftDetect(
             @Query("country") country: String,
             @Query("address") address: String,
             @Query("latitude") latitude: String,
             @Query("serial_number") serial_number: String,
             @Query("phone_model") phone_model: String,
             @Query("country_code") country_code: String,
             @Query("carrier") carrier: String,
             @Query("account_id") account_id: String,
             @Query("sim_id") sim_id: String,
             @Query("imei") imei: String,
             @Query("state") state: String,
             @Query("msisdn") msisdn: String,
             @Query("longitude") longitude: String)
            : Completable


    @POST("/flightmode/detect")
    fun flightModeDetect(
             @Query("country") country: String,
             @Query("address") address: String,
             @Query("latitude") latitude: String,
             @Query("serial_number") serial_number: String,
             @Query("phone_model") phone_model: String,
             @Query("country_code") country_code: String,
             @Query("carrier") carrier: String,
             @Query("account_id") account_id: String,
             @Query("sim_id") sim_id: String,
             @Query("imei") imei: String,
             @Query("state") state: String,
             @Query("msisdn") msisdn: String,
             @Query("longitude") longitude: String)
            : Completable


    @POST("/sim/detect")
    fun simDetect(
             @Query("country") country: String,
             @Query("address") address: String,
             @Query("latitude") latitude: String,
             @Query("serial_number") serial_number: String,
             @Query("phone_model") phone_model: String,
             @Query("country_code") country_code: String,
             @Query("carrier") carrier: String,
             @Query("account_id") account_id: String,
             @Query("sim_id") sim_id: String,
             @Query("imei") imei: String,
             @Query("state") state: String,
             @Query("msisdn") msisdn: String,
             @Query("longitude") longitude: String)
            : Completable


    @GET("/users/device/{imei}")
    fun getUserDevice(@Path("imei") imei: String): Single<DeviceResponse>

    @PUT("/users/id/{userId}")
    fun updateUser(@Path("userId") userId: String,
                   @Query("first_name") first_name: String?,
                   @Query("last_name") lastName: String?,
                   @Query("phone") phone: String?,
                   @Query("email") email: String?,
                   @Query("activity_email") activeIntruder: String?)
            : Single<UserResponse>

    @GET("/users/verify/trigger/{userId}")
    fun userEmailVerify(@Path("userId") userId: String): Single<EmailVerifyResponse>
}