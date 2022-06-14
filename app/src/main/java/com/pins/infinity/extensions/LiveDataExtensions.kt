package com.pins.infinity.extensions

import androidx.lifecycle.MutableLiveData

/**
 * Created by Pavlo Melnyk on 2018-12-06.
 */
fun <T : Any?> MutableLiveData<T>.default(initialValue: T) = apply { setValue(initialValue) }

fun  <T: Any>T.toLiveData(): MutableLiveData<T>{
    val live = MutableLiveData<T>()
    live.value = this
    return live
}

fun  String.toLiveData(): MutableLiveData<String>{
    val live = MutableLiveData<String>()
    live.value = this
    return live
}