package com.pins.infinity.usecases

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

abstract class CompletableUseCase : BaseUseCase<Completable>()

abstract class CompletableParamUseCase<T> : BaseParamUseCase<Completable, T>()

abstract class SingleUseCase<T> : BaseUseCase<Single<T>>()

abstract class SingleParamUseCase<T, E> : BaseParamUseCase<Single<T>, E>()

abstract class FlowableUseCase<T> : BaseUseCase<Flowable<T>>()

abstract class FlowableParamUseCase<T, E> : BaseParamUseCase<Flowable<T>, E>()

abstract class BaseUseCase<T> {

    abstract fun buildUseCase(): T

    /**
     * Executes the current use case.
     */
    open fun execute(): T = this.buildUseCase()
}

abstract class BaseParamUseCase<T, E> {

    abstract fun buildUseCase(param: E): T

    /**
     * Executes the current use case.
     */
    open fun execute(param: E): T = this.buildUseCase(param)
}
