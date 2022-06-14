package com.pins.infinity.viewModels.base

import com.pins.infinity.utility.log
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

/**
 * Created by Pavlo Melnyk on 20.10.2020.
 */
interface DisposableComponent {
    fun disposeAll()

    fun <T> Single<T>.subscribeAndDispose(
            onSuccess: (T) -> Unit = {},
            onError: (Throwable) -> Unit = { it.log() }
    ): Disposable

    fun <T> Observable<T>.subscribeAndDispose(
            onSuccess: (T) -> Unit = {},
            onError: (Throwable) -> Unit = { it.log() }
    ): Disposable

    fun <T> Flowable<T>.subscribeAndDispose(
            onSuccess: (T) -> Unit = {},
            onError: (Throwable) -> Unit = { it.log() }
    ): Disposable

    fun Completable.subscribeAndDispose(
            onSuccess: () -> Unit = {},
            onError: (Throwable) -> Unit = { it.log() }
    ): Disposable
}

class DisposableComponentImpl : DisposableComponent {
    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun disposeAll() {
        if (!compositeDisposable.isDisposed) {
            compositeDisposable.dispose()
        }
    }

    override fun <T> Single<T>.subscribeAndDispose(
            onSuccess: (T) -> Unit,
            onError: (Throwable) -> Unit
    ): Disposable = subscribe(onSuccess, onError).also { compositeDisposable.add(it) }

    override fun <T> Observable<T>.subscribeAndDispose(
            onSuccess: (T) -> Unit,
            onError: (Throwable) -> Unit
    ): Disposable = subscribe(onSuccess, onError).also { compositeDisposable.add(it) }

    override fun <T> Flowable<T>.subscribeAndDispose(
            onSuccess: (T) -> Unit,
            onError: (Throwable) -> Unit
    ): Disposable = subscribe(onSuccess, onError).also { compositeDisposable.add(it) }

    override fun Completable.subscribeAndDispose(
            onSuccess: () -> Unit,
            onError: (Throwable) -> Unit
    ): Disposable = subscribe(onSuccess, onError).also { compositeDisposable.add(it) }
}