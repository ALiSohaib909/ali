@file:JvmName("Logger")
package com.pins.infinity.utility

import android.util.Log

/**
 * Created by Mateusz Sawa on 25/10/2018.
 */

fun Exception.log() = log(this.message ?: this.toString())

fun Throwable.log() = log(this.message ?: this.toString())

fun log(message: String) = Log.v(TAG, message)

private const val TAG = "PINSAPP"

