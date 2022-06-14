package com.pins.infinity.api.paymentapi

import android.content.Context
import com.pins.infinity.api.BaseApiManager
import com.pins.infinity.api.models.*
import com.pins.infinity.api.services.PaymentApiService
import io.reactivex.Single

/**
 * Created by Pavlo Melnyk on 29.11.2018.
 */
class PaymentApiManagerImpl(context: Context, private val paymentApiService: PaymentApiService)
    : BaseApiManager(context), PaymentApiManager {

    override fun getPaymentPlan(userId: String): Single<PlanResponse> =
        paymentApiService.getPaymentPlan(userId).makeRequest()

    override fun initTransaction(initTransactionRequest: Map<String, String>, userId: String)
            : Single<InitTransactionResponse> =
        paymentApiService.initTransaction(initTransactionRequest, userId)
                .makeRequest()

    override fun completeTransaction(userId: String,
                                     transactionId: String,
                                     request: Map<String, String>)
            : Single<CompleteTransactionResponse> =
        paymentApiService.completeTransaction(userId, transactionId, request["status"]!!,  request["affiliateId"]!!,  request["transactionReference"]!!)
                .makeRequest()



}
