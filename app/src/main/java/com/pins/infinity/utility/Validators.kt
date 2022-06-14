package com.pins.infinity.utility

import androidx.lifecycle.LiveData
import com.wajahatkarim3.easyvalidation.core.view_ktx.validator

/**
 * Created by Pavlo Melnyk on 16.01.2019.
 */

fun LiveData<String>.isEmailValid() = this.value?.validator()?.nonEmpty()?.validEmail()?.check()
        ?: false

fun LiveData<String>.isCodeValid() = this.value?.validator()?.nonEmpty()?.onlyNumbers()?.check()
        ?: false
