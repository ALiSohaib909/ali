package com.pins.infinity.api.exceptions

import androidx.annotation.StringRes
import com.pins.infinity.R

fun Throwable.toForgotPinInitException(): Throwable {
    if (this !is ApiException) return this

    return when (this.message) {
        "Invalid Identity" -> ForgotPinInitException.InvalidIdentity
        else -> this
    }
}

sealed class ForgotPinInitException(
    @StringRes override val errorRes: Int
) : BaseException(errorRes) {

    object InvalidIdentity :
            ForgotPinInitException(R.string.error_email)
}
