package com.pins.infinity.api.usermodels

import android.os.Build
import com.pins.infinity.usecases.DangerTriggeredUseCase

data class UserDangerRequest(
    val imei: String,
    val carrier: String,
    val msisdn: String,
    val simId: String,
    val country: String,
    val countryCode: String,
    val accountId: String,
    val latitude: String,
    val longitude: String,
    val state: String,
    val address: String,
    val model: String,
    val serialNumber: String,
    val dangerType: DangerTriggeredUseCase.DangerType
)