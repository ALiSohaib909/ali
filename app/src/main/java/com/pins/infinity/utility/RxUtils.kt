package com.pins.infinity.utility

import android.content.Context
import com.pins.infinity.api.exceptions.NoInternetConnectionException
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

/**
 * Created by Pavlo Melnyk on 29.11.2018.
 */
fun <T> Single<T>.applySchedulers(): Single<T>
        = observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread())

fun <T> Observable<T>.applySchedulers(): Observable<T>
        = observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread())

fun Completable.applySchedulers(): Completable
        = observeOn(AndroidSchedulers.mainThread()).subscribeOn(Schedulers.newThread())

fun <T> Single<T>.checkConnectivity(context: Context): Single<T>
        = if (context.isInternetConnection()) this.applySchedulers()
                else Single.error(NoInternetConnectionException)
