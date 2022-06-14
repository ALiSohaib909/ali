package com.pins.infinity.repositories

import com.pins.infinity.database.models.UserItem
import com.pins.infinity.usecases.DangerTriggeredUseCase
import io.reactivex.Completable
import io.reactivex.Single

/**
 * Created by Pavlo Melnyk on 2018-12-17.
 */

interface UserRepository {
    fun getUser(): Single<UserItem>
    fun initCheck(): Single<Boolean>
    fun dangerTriggered(param: DangerTriggeredUseCase.Param): Completable
    fun getUserDevice(): Single<Boolean>
    fun updateUserEmail(userEmail: String, userId: String): Single<Boolean>
    fun initVerifyUserEmail(userEmail: String): Single<Boolean>
    fun checkIfEmailVerified(): Single<Boolean>
    fun checkIfIntruderEnabled(): Single<Boolean>
    fun updateIntruderActive(isReceiving: Boolean): Single<Boolean>

}
