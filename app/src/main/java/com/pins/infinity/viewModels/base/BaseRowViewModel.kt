package com.pins.infinity.viewModels.base

import android.content.Context

/**
 * Created by Pavlo Melnyk on 28.11.2018.
 */
abstract class BaseRowViewModel(context: Context) {
    open val viewType: Int = DEFAULT_ITEM

    companion object {
        const val DEFAULT_ITEM = 0
    }
}