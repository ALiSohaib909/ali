package com.pins.infinity.api.exceptions

import androidx.annotation.StringRes
import com.pins.infinity.R

fun Throwable.toUnlockAppLockedException(): Throwable {
    if (this !is ApiException) return this

    return when (this.message) {
        "Invalid Pin" -> UnlockAppLockedException.InvalidPin
        else -> this
    }
}

sealed class UnlockAppLockedException(
        @StringRes override val errorRes: Int
) : BaseException(errorRes) {

    object InvalidPin :
            UnlockAppLockedException(R.string.appLock_wrongPin_title)
}
