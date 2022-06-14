package com.pins.infinity.repositories.models

import androidx.lifecycle.MutableLiveData
import com.pins.infinity.R
import com.pins.infinity.extensions.default

/**
 * Created by Pavlo Melnyk on 2018-12-06.
 */

class SummaryComputationModel(
        val planPrice: MutableLiveData<String> = MutableLiveData<String>().default(""))
