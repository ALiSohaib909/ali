package com.pins.infinity.api.paymentapi

import com.pins.infinity.api.models.*
import io.reactivex.Single

/**
 * Created by Pavlo Melnyk on 29.11.2018.
 */
interface PaymentApiManager{
    fun getPaymentPlan(userId: String): Single<PlanResponse>
    fun initTransaction(initTransactionRequest: Map<String, String>, userId: String)
            : Single<InitTransactionResponse>
    fun completeTransaction(userId: String,
                            transactionId: String,
                            completeTransactionRequest: Map<String, String>)
            : Single<CompleteTransactionResponse>}