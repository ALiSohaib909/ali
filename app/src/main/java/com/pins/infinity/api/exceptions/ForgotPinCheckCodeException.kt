package com.pins.infinity.api.exceptions

import androidx.annotation.StringRes
import com.pins.infinity.R

fun Throwable.toForgotPinCheckCodeException(): Throwable {
    if (this !is ApiException) return this

    return when (this.message) {
        "Cannot identify recovery mode" -> ForgotPinCheckCodeException.InvalidCode
        "Resent code provided is not valid" -> ForgotPinCheckCodeException.InvalidCode
        else -> this
    }
}

sealed class ForgotPinCheckCodeException(
        @StringRes override val errorRes: Int
) : BaseException(errorRes) {

    object InvalidCode :
            ForgotPinCheckCodeException(R.string.appLock_wrongPin_title)
}
