package com.pins.infinity.services.billing

import com.pins.infinity.viewModels.PaymentPlanViewModel
import java.util.*

/**
 * Created by Pavlo Melnyk on 03.09.2018.
 */
// Subscription's ids from:
// https://play.google.com/apps/publish/
const val SKU_MONTHLY = "pins_app_monthly_sub_03_09_2018"
const val SKU_YEARLY = "pins_app_year_sub_03_09_2018"
const val SKU_PREMIUM_MONTHLY = "pins_app_premium_month_subscription_19_12_2018"
const val SKU_PREMIUM_YEARLY = "pins_app_premium_year_subscription_19_12_2018"
const val SKU_STANDARD_MONTHLY = "pins_app_standard_month_subscription_19_12_2018"
const val SKU_STANDARD_YEARLY = "pins_app_standard_year_subscription_19_12_2018"

val skuSubscriptionList: List<String>
    get() = Arrays.asList(SKU_PREMIUM_MONTHLY, SKU_PREMIUM_YEARLY, SKU_STANDARD_MONTHLY, SKU_STANDARD_YEARLY)


fun getSkuForChosenOption(planOption: String, durationOption: String): String {
    return if (planOption == PaymentPlanViewModel.PlanOption.PREMIUM_PLAN.plan &&
            durationOption == PaymentPlanViewModel.DurationOption.MONTH.duration)
        SKU_PREMIUM_MONTHLY
    else if (planOption == PaymentPlanViewModel.PlanOption.PREMIUM_PLAN.plan &&
            durationOption == PaymentPlanViewModel.DurationOption.YEAR.duration)
        SKU_PREMIUM_YEARLY
    else if (planOption == PaymentPlanViewModel.PlanOption.STANDARD_PLAN.plan &&
            durationOption == PaymentPlanViewModel.DurationOption.MONTH.duration)
        SKU_STANDARD_MONTHLY
    else if (planOption == PaymentPlanViewModel.PlanOption.STANDARD_PLAN.plan &&
            durationOption == PaymentPlanViewModel.DurationOption.YEAR.duration)
        SKU_STANDARD_YEARLY
    else SKU_STANDARD_MONTHLY
}
