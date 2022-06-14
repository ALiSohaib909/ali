package com.pins.infinity.repositories

import com.pins.infinity.utility.log
import com.pins.infinity.api.paymentapi.PaymentApiManager
import com.pins.infinity.database.daos.PaymentDao
import com.pins.infinity.database.daos.UserDao
import com.pins.infinity.database.models.CompleteItem
import com.pins.infinity.database.models.InitItem
import com.pins.infinity.database.models.PlanItem
import com.pins.infinity.database.models.UssdItem
import com.pins.infinity.extensions.toLiveData
import com.pins.infinity.repositories.models.StartTransactionModel
import com.pins.infinity.repositories.models.SummaryComputationModel
import com.pins.infinity.viewModels.PaymentPlanViewModel
import io.reactivex.Single

/**
 * Created by Pavlo Melnyk on 27.11.2018.
 */
class PaymentRepositoryImpl(private val paymentApiManager: PaymentApiManager,
                            private val userRepository: UserRepository,
                            private val paymentDao: PaymentDao,
                            private val userDao: UserDao)
    : PaymentRepository {

    override fun isSubscriptionValid(): Single<Boolean> = userRepository.getUser().map {
        log("plan is valid: ${it.plan != EXPIRED}")
        it.plan != EXPIRED
    }

    override fun getPaymentPlans(): Single<List<PlanItem>> {
        return userDao.getUser().flatMap {
            return@flatMap paymentApiManager.getPaymentPlan(it.userId).map { planResponse ->
                val plans = planResponse.castPlans()
                paymentDao.clearPlanItem()
                paymentDao.clearRecoveryItem()
                paymentDao.clearUssdItem()

                paymentDao.insertPlans(plans)
                paymentDao.insertRecovery(planResponse.response.extraPlan.recovery.castRecovery())
                paymentDao.insertUssd(UssdItem(code = planResponse.response.ussdCode))

                return@map plans
            }
        }
    }

    override fun paymentInit(startTransactionModel: StartTransactionModel)
            : Single<SummaryComputationModel> {
        return userDao.getUser().flatMap { user ->
            return@flatMap paymentApiManager.initTransaction(
                    startTransactionModel.getInitTransactionRequestObject(),
                    user.userId).flatMap { initResponse ->

                val initItem = InitItem(
                        transId = initResponse.response.transactionId,
                        vat = initResponse.response.vat,
                        error = initResponse.error,
                        total = initResponse.response.amount,
                        totalInUnit = initResponse.response.totalInUnit)
                paymentDao.clearInitItem()
                paymentDao.insertInit(initItem)

                return@flatMap getSummaryComputationElements(startTransactionModel)
            }
        }
    }

    override fun getSummaryComputationElements(startTransactionModel: StartTransactionModel)
            : Single<SummaryComputationModel> {
        return paymentDao.getFilteredPlans(startTransactionModel.chosenOption.planOption).map { plans ->

            val planPrice = getPlanPrice(plans.firstOrNull()!!, startTransactionModel.chosenOption.durationOption)

            return@map SummaryComputationModel(
                    planPrice = planPrice.toLiveData())
        }
    }

    override fun getUssdCode(): Single<String> = paymentDao.getUssd().map { it.code }

    override fun payByActivationCode(startTransactionModel: StartTransactionModel)
            : Single<CompleteItem> {
        return userDao.getUser().flatMap { user ->
            return@flatMap paymentDao.getInitItem().flatMap { init ->
                return@flatMap paymentApiManager.completeTransaction(
                        user.userId,
                        init.transId,
                        startTransactionModel.getCompleteTransactionRequest()
                ).map { completeResponse ->

                    val completeItem = CompleteItem(
                            error = completeResponse.error,
                            transactionId = init.transId
                    )
                    paymentDao.clearCompleteItem()
                    paymentDao.insertComplete(completeItem)

                    return@map completeItem
                }
            }
        }
    }

    private fun getPlanPrice(plan: PlanItem, duration: String): String =
            if (PaymentPlanViewModel.DurationOption.YEAR.duration == duration) plan.yearPrice
            else plan.monthPrice

    companion object {
        const val BANK = "bank"
        const val INSTORE = "instore"
        const val MOBILE = "mobile"
        const val STATUS_SUCCESS = "00"
        const val STATUS_CANCELED = "33"
        const val EXPIRED = "expired"
        const val FREE = "free"
    }
}

