package com.pins.infinity.api

import android.content.Context
import com.pins.infinity.utility.applySchedulers
import com.pins.infinity.utility.checkConnectivity
import io.reactivex.Single

/**
 * Created by Pavlo Melnyk on 29.11.2018.
 */
open class BaseApiManager(val context: Context) {

    protected fun <T> Single<T>.makeRequest(): Single<T> {
        return this
                .applySchedulers()
                .checkConnectivity(context)
    }
}