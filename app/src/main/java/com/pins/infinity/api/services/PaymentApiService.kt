package com.pins.infinity.api.services

import com.pins.infinity.api.models.CompleteTransactionResponse
import com.pins.infinity.api.models.InitTransactionResponse
import com.pins.infinity.api.models.PlanResponse
import io.reactivex.Single
import retrofit2.http.*

/**
 * Created by Pavlo Melnyk on 29.11.2018.
 */
interface PaymentApiService {

    @GET("/payment/plan/{userId}")
    fun getPaymentPlan(@Path("userId") userId: String): Single<PlanResponse>

    @FormUrlEncoded
    @POST("/payment/init/{userId}")
    fun initTransaction(@FieldMap request: Map<String, String>, @Path("userId") userId: String)
            : Single<InitTransactionResponse>

    @PUT("/payment/complete/{userId}/{transactionId}")
    fun completeTransaction(@Path("userId") userId: String,
                            @Path("transactionId") transactionId: String,
                            @Query("status") status: String,
                            @Query("affiliate_id") affiliateId: String,
                            @Query("trans_ref") transactionReference: String)
            : Single<CompleteTransactionResponse>
}