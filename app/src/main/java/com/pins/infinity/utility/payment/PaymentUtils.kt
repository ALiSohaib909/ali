package com.pins.infinity.utility.payment

/**
 * Created by Pavlo Melnyk on 2018-12-06.
 */
class PaymentUtils {
    companion object {
        fun getPercentage(percent: Long, planPrice: Long, isRecovery: Boolean, recoveryPrice: Long = 0)
                : Float {
            var total = planPrice

            if (isRecovery) {
                total += recoveryPrice
            }
            val proportion = percent.toFloat() * total.toFloat()
            return proportion / 100
        }
    }
}