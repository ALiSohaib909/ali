package com.pins.infinity.repositories

import com.pins.infinity.api.NetworkManager
import com.pins.infinity.api.exceptions.toSetUpPinException
import com.pins.infinity.api.userapi.UserApiManager
import com.pins.infinity.api.usermodels.UserDangerRequest
import com.pins.infinity.api.usermodels.UserRequest
import com.pins.infinity.api.usermodels.UserResponse
import com.pins.infinity.api.utils.makeRequest
import com.pins.infinity.api.utils.onErrorThrow
import com.pins.infinity.database.SettingsManager
import com.pins.infinity.database.daos.DeviceDao
import com.pins.infinity.database.daos.UserDao
import com.pins.infinity.database.models.UserItem
import com.pins.infinity.usecases.DangerTriggeredUseCase
import io.reactivex.Completable
import io.reactivex.Single

/**
 * Created by Pavlo Melnyk on 2018-12-17.
 */
class UserRepositoryImpl(
    private val userApiManager: UserApiManager,
    private val userDao: UserDao,
    private val deviceDao: DeviceDao,
    private val networkManager: NetworkManager,
    private val settingsManager: SettingsManager
) : UserRepository {

    override fun getUser(): Single<UserItem> {
        return userDao.getUser().flatMap { item ->
            return@flatMap userApiManager.getUser(item.userId).map { userResponse ->
                val user = userResponse.castUser()
                val imei = settingsManager.imei

                insertUserWithDevice(userResponse, user, imei)

                return@map user
            }
        }
    }

    override fun initCheck(): Single<Boolean> {
        val userId = userDao.getUser().blockingGet().userId
        val imei = settingsManager.imei

        return userApiManager.getUser(userId).flatMap { userResponse ->
            val user = userResponse.castUser()

            insertUserWithDevice(userResponse, user, imei)

            return@flatMap userApiManager.getUserDevice(imei).map { deviceResponse ->
                val device = deviceResponse.castDevice()
                deviceDao.clearDevice()
                deviceDao.insertDevice(device)

                return@map user.plan != PaymentRepositoryImpl.EXPIRED
            }
        }
    }

    override fun dangerTriggered(param: DangerTriggeredUseCase.Param): Completable {


        return userApiManager.dangerTriggered(
            param.dangerType,
            UserDangerRequest(
                imei = param.imei,
                carrier = param.carrier,
                msisdn = param.msisdn,
                simId = param.simId,
                country = param.country,
                countryCode = param.countryCode,
                accountId = param.accountId,
                latitude = param.latitude,
                longitude = param.longitude,
                state = param.state,
                address = param.address,
                model = param.model,
                serialNumber = param.serialNumber,
                dangerType = param.dangerType,
            )
        )
            .makeRequest(networkManager)
            .onErrorThrow { it.toSetUpPinException() }
    }

    private fun insertUserWithDevice(userResponse: UserResponse, user: UserItem, imei: String) {

        userDao.clearUserItem()
        userDao.insertUser(user)

        settingsManager.imei.let {
            userResponse.response.user.devices.firstOrNull { device -> device.imei == it }
        }?.run {
            deviceDao.clearDevice()
            deviceDao.insertDevice(castDevice())
        }
    }

    override fun getUserDevice(): Single<Boolean> {
        val imei = settingsManager.imei

        return userApiManager.getUserDevice(imei).map { deviceResponse ->
            val device = deviceResponse.castDevice()
            deviceDao.clearDevice()
            deviceDao.insertDevice(device)
            return@map true
        }
    }

    override fun initVerifyUserEmail(userEmail: String): Single<Boolean> {
        return userDao.getUser().flatMap { it ->
            if (it.email == userEmail) {
                return@flatMap verifyUserEmailByApi(it.userId).map { response ->
                    return@map response
                }
            } else {
                return@flatMap updateUserEmail(userEmail, it.userId).map { response ->
                    return@map response
                }
            }
        }
    }

    private fun verifyUserEmailByApi(userId: String): Single<Boolean> {
        return userApiManager.verifyUserEmail(userId).map { response ->
            return@map !response.error
        }
    }

    override fun updateUserEmail(userEmail: String, userId: String): Single<Boolean> {
        val user = UserRequest(userId = userId, email = userEmail)
        return userApiManager.updateUser(user)
            .flatMap { userResponse ->
                val user = userResponse.castUser()
                if (user.email == userEmail) {
                    userDao.clearUserItem()
                    userDao.insertUser(user)
                    return@flatMap verifyUserEmailByApi(userId).map { response ->
                        return@map response
                    }
                } else {
                    return@flatMap Single.just(!userResponse.error)
                }
            }
    }

    override fun updateIntruderActive(isActive: Boolean): Single<Boolean> {
        return userDao.getUser().flatMap { it ->
            val user = UserRequest(userId = it.userId, activeIntruder = isActive)
            userApiManager.updateUser(user)
                .map { userResponse ->
                    if (userResponse.error) {
                        return@map false
                    }

                    val user = userResponse.castUser()
                    if (user.activeIntruder == isActive) {
                        userDao.clearUserItem()
                        userDao.insertUser(user)
                        return@map isActive
                    } else {
                        return@map false
                    }
                }
        }
    }

    override fun checkIfIntruderEnabled(): Single<Boolean> {
        return getUser().map { userItem ->
            return@map (userItem.activeIntruder)
        }
    }

    override fun checkIfEmailVerified(): Single<Boolean> {
        return getUser().map { userItem ->
            return@map (userItem.verified)
        }
    }
}