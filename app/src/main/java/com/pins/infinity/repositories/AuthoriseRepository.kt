package com.pins.infinity.repositories

import io.reactivex.Single

/**
 * Created by Pavlo Melnyk on 21.02.2019.
 */
interface AuthoriseRepository {
    fun remoteLogin(imei: String): Single<Boolean>

}