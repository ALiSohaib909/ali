package com.pins.infinity.utility

import android.content.Context
import com.pins.infinity.R

/**
 * Created by Pavlo Melnyk on 29.11.2018.
 */

class Errors {
    class NoInternetConnection(context: Context) : Throwable(context.getString(R.string.error_no_internet_connection))
}