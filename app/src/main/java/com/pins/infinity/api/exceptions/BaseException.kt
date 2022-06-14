package com.pins.infinity.api.exceptions

import androidx.annotation.StringRes

abstract class BaseException(@StringRes open val errorRes: Int) : Exception()
