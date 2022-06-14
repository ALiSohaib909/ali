package com.pins.infinity.viewModels.rows

import androidx.databinding.ObservableBoolean
import com.pins.infinity.R
import com.pins.infinity.viewModels.base.BaseRowViewModel
import com.pins.infinity.viewModels.PaymentPlanViewModel

/**
 * Created by Pavlo Melnyk on 30.11.2018.
 */
class PlanRowViewModel(
        var parentViewModel: PaymentPlanViewModel,
        var title: String,
        var monthPrice: String,
        var yearPrice: String,
        subtitle: String,
        var googlePayCurrency: String,
        var shouldIncludeCurrency: Boolean
): BaseRowViewModel(parentViewModel.context()){

    val isChecked = ObservableBoolean()
    var subtitle: String = subtitle
        get() {
            val perYear = parentViewModel.context().getString(R.string.paymentPlan_per_year)
            val perMonth = parentViewModel.context().getString(R.string.paymentPlan_per_month)
            val currency = if(shouldIncludeCurrency) parentViewModel.context().getString(R.string.nigerian_currency_short) else googlePayCurrency
            return "$currency$monthPrice$perMonth, $currency$yearPrice$perYear"
        }
}