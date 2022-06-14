package com.pins.infinity.repositories

import com.pins.infinity.database.models.CompleteItem
import com.pins.infinity.database.models.PlanItem
import com.pins.infinity.repositories.models.StartTransactionModel
import com.pins.infinity.repositories.models.SummaryComputationModel
import io.reactivex.Single

/**
 * Created by Pavlo Melnyk on 27.11.2018.
 */
interface PaymentRepository {

    fun getPaymentPlans(): Single<List<PlanItem>>
    fun getSummaryComputationElements(startTransactionModel: StartTransactionModel): Single<SummaryComputationModel>
    fun paymentInit(startTransactionModel: StartTransactionModel) : Single<SummaryComputationModel>
    fun getUssdCode() : Single<String>
    fun payByActivationCode(startTransactionModel: StartTransactionModel) : Single<CompleteItem>
    fun isSubscriptionValid() : Single<Boolean>
}