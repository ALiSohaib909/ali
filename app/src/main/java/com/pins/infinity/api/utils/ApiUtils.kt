package com.pins.infinity.api.utils

import com.pins.infinity.api.NetworkManager
import com.pins.infinity.api.exceptions.BaseException
import com.pins.infinity.api.exceptions.NoInternetConnectionException
import com.pins.infinity.api.exceptions.NotRespondingException
import com.pins.infinity.api.utils.ApiUtils.Companion.API_FALSE
import com.pins.infinity.api.utils.ApiUtils.Companion.API_TRUE
import com.pins.infinity.modules.ModuleConstants.TIMEOUT
import com.pins.infinity.utility.applySchedulers
import com.pins.infinity.utility.checkConnectivity
import io.reactivex.Completable
import io.reactivex.Single
import java.util.concurrent.TimeUnit

/**
 * Created by Pavlo Melnyk on 28.01.2019.
 */

class ApiUtils {
    companion object {
        const val API_TRUE = "1"
        const val API_FALSE = "0"
    }
}

fun Boolean.toApiStringBoolean() = if (this) API_TRUE else API_FALSE

fun String.fromApiStringBoolean() = this == API_TRUE

internal fun <T> Single<T>.makeRequest(networkManager: NetworkManager): Single<T> =
        this.applySchedulers()
                .timeout(TIMEOUT, TimeUnit.MILLISECONDS, Single.error(NotRespondingException))
                .checkConnectivity(networkManager)

internal fun Completable.makeRequest(networkManager: NetworkManager): Completable =
        this.applySchedulers()
                .timeout(TIMEOUT, TimeUnit.MILLISECONDS, Completable.error(NotRespondingException))
                .checkConnectivity(networkManager)

internal fun <T> Single<T>.checkConnectivity(networkManager: NetworkManager): Single<T> {
    return if (networkManager.isInternetAvailable()) {
        this.applySchedulers()
    } else {
        Single.error(NoInternetConnectionException)
    }
}

internal fun Completable.checkConnectivity(networkManager: NetworkManager): Completable {
    return if (networkManager.isInternetAvailable()) {
        this.applySchedulers()
    } else {
        Completable.error(NoInternetConnectionException)
    }
}

internal fun Completable.onErrorThrow(toThrow: (Throwable) -> Throwable): Completable =
        this.onErrorResumeNext { Completable.error(toThrow(it)) }

internal fun <T> Single<T>.onErrorThrow(toThrow: (Throwable) -> Throwable): Single<T> =
        this.onErrorResumeNext { Single.error(toThrow(it)) }

internal fun Throwable.getError(defaultError: Int): Int =
        (this as? BaseException)?.errorRes ?: defaultError
