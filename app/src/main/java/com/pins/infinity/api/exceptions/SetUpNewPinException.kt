package com.pins.infinity.api.exceptions

import androidx.annotation.StringRes
import com.pins.infinity.R

fun Throwable.toSetUpNewPinException(): Throwable {
    if (this !is ApiException) return this

    return when (this.message) {
        "Error updating account" -> SetUpNewPinException.ErrorUpdatingAccount
        else -> this
    }
}

sealed class SetUpNewPinException(
        @StringRes override val errorRes: Int
) : BaseException(errorRes) {

    object ErrorUpdatingAccount :
            SetUpNewPinException(R.string.appLock_forgotPin_ErrorUpdatingAccount_message)
}
