package com.pins.infinity.repositories.models

import android.os.Parcelable
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.pins.infinity.api.models.CompleteTransactionRequest
import com.pins.infinity.api.models.InitTransactionRequest
import com.pins.infinity.repositories.PaymentRepositoryImpl
import com.pins.infinity.viewModels.PaymentPlanViewModel
import kotlinx.android.parcel.Parcelize

/**
 * Created by Pavlo Melnyk on 2018-12-07.
 */
@Parcelize
data class StartTransactionModel(val chosenOption: PaymentPlanViewModel.ChosenOption,
                                 val code: String = "")
    : Parcelable {
    fun getInitTransactionRequestObject(): Map<String, String> {
        val request = InitTransactionRequest(
                plan = this.chosenOption.planOption,
                duration = this.chosenOption.durationOption,
                gateway = this.chosenOption.paymentOption,
                method = PaymentRepositoryImpl.BANK,
                app = PaymentRepositoryImpl.MOBILE,
                recovery = if (this.chosenOption.isRecovery) "1" else "0")

        val dataclassAsMap = ObjectMapper().convertValue<Map<String, String>>(request, object :
                TypeReference<Map<String, String>>() {})

        return dataclassAsMap
    }

    fun getCompleteTransactionRequest(): Map<String, String> {

        val request = CompleteTransactionRequest(
                status = PaymentRepositoryImpl.STATUS_SUCCESS,
                affiliateId = "",
                transactionReference = this.code)

        val dataclassAsMap = ObjectMapper().convertValue<Map<String, String>>(request, object :
                TypeReference<Map<String, String>>() {})

        return dataclassAsMap
    }
}