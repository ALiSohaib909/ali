package com.pins.infinity.api.models

import com.google.gson.annotations.SerializedName
import androidx.room.PrimaryKey
import androidx.annotation.NonNull
import com.pins.infinity.database.models.PlanItem
import com.pins.infinity.database.models.RecoveryItem
import com.pins.infinity.viewModels.PaymentPlanViewModel

/**
 * Created by Pavlo Melnyk on 30.11.2018.
 */

data class PlanResponse (
        @PrimaryKey(autoGenerate = true) @NonNull val id: Int,

        @SerializedName("message") val message: String,
        @SerializedName("code") val code: Long,
        @SerializedName("response") val response: Response,
        @SerializedName("error") val error: Boolean
){

        fun castPlans(): List<PlanItem> {
                val plans = mutableListOf<PlanItem>()
                val premium = this.response.basic.premium
                val standard = this.response.basic.standard

                val titlePremium = premium.javaClass.simpleName.toUpperCase()
                val monthPricePremium = premium.month.amount.toString()
                val yearPricePremium = premium.year.amount.toString()

                plans.add(PlanItem(title = titlePremium, plan = PaymentPlanViewModel.PlanOption.PREMIUM_PLAN.plan, monthPrice = monthPricePremium, yearPrice = yearPricePremium))

                val titleStandard = standard.javaClass.simpleName.toUpperCase()
                val monthPriceStandard = standard.month.amount.toString()
                val yearPriceStandard = standard.year.amount.toString()

                plans.add(PlanItem(title = titleStandard, plan = PaymentPlanViewModel.PlanOption.STANDARD_PLAN.plan, monthPrice = monthPriceStandard, yearPrice = yearPriceStandard))

                return plans
        }
}

data class Response (
        @SerializedName("currency") val currency: String,
        @SerializedName("ussd_code") val ussdCode: String,
        @SerializedName("extra_plan") val extraPlan: ExtraPlan,
        @SerializedName("basic") val basic: Basic
)

data class Basic (
        @SerializedName("premium") val premium: Premium,
        @SerializedName("standard") val standard: Standard
)

data class Premium (
        @SerializedName("week") val week: Recovery,
        @SerializedName("year") val year: Recovery,
        @SerializedName("month") val month: Recovery
)

data class Standard (
        @SerializedName("week") val week: Recovery,
        @SerializedName("year") val year: Recovery,
        @SerializedName("month") val month: Recovery
)

data class Recovery (
        @SerializedName("amount") val amount: Long,
        @SerializedName("description") val description: String
){
        fun castRecovery(): RecoveryItem {
                val titleRecovery = this.javaClass.simpleName.toUpperCase()
                val priceRecovery = this.amount

                return RecoveryItem(title = titleRecovery, price = priceRecovery)
        }
}

data class ExtraPlan (
        @SerializedName("recovery") val recovery: Recovery
)