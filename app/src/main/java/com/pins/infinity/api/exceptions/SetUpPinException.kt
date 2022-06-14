package com.pins.infinity.api.exceptions

import androidx.annotation.StringRes
import com.pins.infinity.R

fun Throwable.toSetUpPinException(): Throwable {
    if (this !is ApiException) return this

    return when (this.message) {
        "Account Id and imei mis match" -> SetUpPinException.IdImeiMisMatchException
        "Invalid email or passowrd" -> SetUpPinException.InvalidCodeException
        "Error Message" -> SetUpPinException.UnknownException
        else -> this
    }
}

sealed class SetUpPinException(
        @StringRes override val errorRes: Int
) : BaseException(errorRes) {

    object IdImeiMisMatchException :
            SetUpPinException(R.string.appLock_error_id_imei_message)

    object InvalidCodeException :
            SetUpPinException(R.string.error_code_empty_message)

    object UnknownException :
            SetUpPinException(R.string.error_title)
}
