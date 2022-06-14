package com.pins.infinity.usecases

import com.pins.infinity.database.SettingsManager
import com.pins.infinity.repositories.AppLockRepository
import com.pins.infinity.repositories.DeviceRepository
import com.pins.infinity.repositories.UserRepository
import io.reactivex.Completable

/**
 * Created by Pavlo Melnyk on 12.10.2020.
 */
class DangerTriggeredUseCase(
        private val userRepository: UserRepository
) : CompletableParamUseCase<DangerTriggeredUseCase.Param>() {

    override fun buildUseCase(param: Param): Completable {
        return userRepository.dangerTriggered(param = param)
    }

    data class Param(
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
        val dangerType: DangerType
    )

    data class PresenterParam(
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
        val serialNumber: String
    )

    enum class DangerType {
        SIM_DETECT,
        THEFT_DETECT,
        FLIGHT_MODE_DETECT
    }
}