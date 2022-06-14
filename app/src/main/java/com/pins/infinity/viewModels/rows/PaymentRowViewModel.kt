package com.pins.infinity.viewModels.rows

import androidx.databinding.ObservableBoolean
import com.pins.infinity.viewModels.base.BaseRowViewModel
import com.pins.infinity.viewModels.PaymentPlanViewModel
import com.pins.infinity.viewModels.PaymentViewModel

/**
 * Created by Pavlo Melnyk on 28.11.2018.
 */
class PaymentRowViewModel(
        parentViewModel: PaymentViewModel,
        var title: Int,
        var subtitle: Int,
        var image: Int,
        var isImageVisible: Boolean,
        var paymentOption: PaymentPlanViewModel.PaymentOptions
): BaseRowViewModel(parentViewModel.context()){

    val isChecked = ObservableBoolean()
}