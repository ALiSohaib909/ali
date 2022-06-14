package com.pins.infinity.viewModels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import android.content.Intent
import androidx.databinding.ObservableBoolean
import android.os.Parcelable
import com.pins.infinity.R
import com.pins.infinity.activity.payment.PaymentSummaryActivity
import com.pins.infinity.adapters.RecyclerAdapter
import com.pins.infinity.database.daos.BillingDao
import com.pins.infinity.database.daos.PaymentDao
import com.pins.infinity.database.models.RecoveryItem
import com.pins.infinity.externals.MutableLiveArrayList
import com.pins.infinity.repositories.PaymentRepository
import com.pins.infinity.viewModels.base.BaseRowViewModel
import com.pins.infinity.viewModels.rows.PlanRowViewModel
import kotlinx.android.parcel.Parcelize

/**
 * Created by Pavlo Melnyk on 29.11.2018.
 */
class PaymentPlanViewModel(
        application: Application,
        val paymentRepository: PaymentRepository,
        val paymentDao: PaymentDao,
        val billingDao: BillingDao,
        private val paymentOption: String)
    : BasePaymentViewModel(application), RecyclerAdapter.OnRowClickListener {

    private val planElements = MutableLiveArrayList<PlanRowViewModel>()
    var recoveryElement = MutableLiveData<RecoveryItem>()

    val isRecoveryVisible = ObservableBoolean()
    val isRecoveryChecked = ObservableBoolean()
    val isError = ObservableBoolean()

    val adapter = RecyclerAdapter(planElements) {
        mapOf(BaseRowViewModel.DEFAULT_ITEM to R.layout.row_payment_plan_choose)
    }

    init {
        isRecoveryChecked.set(false)
        isProgressDialog.value = true
        if (paymentOption != PaymentOptions.GOOGLE_PAY.payment) {
            getPlan()
        }

        adapter.onRowClickListener = this
    }

    private fun getPlan() {
        paymentDao.getPlanItems()
                .subscribeAndDispose({ plans ->
                    isProgressDialog.value = false
                    planElements.clear()

                    plans.forEach { it ->
                        val row = PlanRowViewModel(this, it.title, it.monthPrice, it.yearPrice, "", it.currency, paymentOption != PaymentOptions.GOOGLE_PAY.payment)
                        planElements.add(row)
                    }
                    planElements.value?.get(0)?.isChecked?.set(true)

                    paymentDao.getRecovery().subscribe({ r -> recoveryElement.value = r }, { isProgressDialog.value = false })

                    isError.set(false)
                }, { throwable ->
                    isProgressDialog.value = false
                    isError.set(true)
                    showError(throwable, R.string.error_title)
                })
    }

    override fun onRowClick(model: Any) {
        if (model !is PlanRowViewModel) {
            return
        }

        isRecoveryVisible.set(false)
        isRecoveryChecked.set(false)
        planElements.value!!.forEach {
            it.isChecked.set(false)
            if (planElements.getItem(1) == model) {
                isRecoveryVisible.set(true)
                isRecoveryChecked.set(true)
            }
        }
        model.isChecked.set(true)
    }

    fun continueCommand() {
        if (isError.get()) {
            showError(errorRes = R.string.error_title)
            return
        }
        val intent = Intent(context(), PaymentSummaryActivity::class.java)
        intent.putExtra(PaymentSummaryActivity.CHOSEN_OPTION, ChosenOption(isRecovery = isRecoveryChecked.get(), paymentOption = paymentOption, planOption = getPlanOption(), durationOption = DurationOption.MONTH.duration))
        startActivity.value = intent
    }

    enum class PaymentOptions(val payment: String) {
        GOOGLE_PAY("googlepay"), INSTORE("instore"), CARD("paystack"), USSD("ussd");
    }

    enum class PlanOption(val plan: String) {
        STANDARD_PLAN("standard"), PREMIUM_PLAN("premium")
    }

    enum class DurationOption(val duration: String) {
        YEAR("year"), MONTH("month")
    }

    @Parcelize
    data class ChosenOption(
            var isRecovery: Boolean,
            var paymentOption: String,
            var planOption: String,
            var durationOption: String
    ) : Parcelable

    private fun getPlanOption() = if (isRecoveryVisible.get()) PlanOption.STANDARD_PLAN.plan else PlanOption.PREMIUM_PLAN.plan

    fun getGooglePayPrices() {
        getPlan()
    }

    fun onErrorGettingGooglePayPrices() {
        planElements.clear()
        isError.set(true)
    }
}