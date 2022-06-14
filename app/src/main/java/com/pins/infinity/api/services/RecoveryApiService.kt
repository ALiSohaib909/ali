package com.pins.infinity.api.services

import com.pins.infinity.api.models.recovery.RecoveryCheckResponse
import com.pins.infinity.api.models.recovery.RecoveryInitResponse
import io.reactivex.Completable
import io.reactivex.Single
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * Created by Pavlo Melnyk on 29.10.2020.
 */

interface RecoveryApiService {

    @POST("/recovery/init")
    fun init(@Query("recovery_mode") mode: String,
             @Query("identifier") identifier: String,
             @Query("imei") imei: String,
             @Query("recovery_type") type: String)
            : Single<RecoveryInitResponse>

    @POST("/recovery/check")
    fun check(@Query("response") response: String,
              @Query("imei") imei: String)
            : Single<RecoveryCheckResponse>

    @POST("/recovery/password")
    fun password(@Query("password") password: String,
                 @Query("imei") imei: String)
            : Completable
}