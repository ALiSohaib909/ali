package com.pins.infinity.viewModels

import android.app.Application
import android.content.Intent
import com.pins.infinity.R
import com.pins.infinity.activity.payment.PaymentPlanActivity
import com.pins.infinity.activity.payment.PaymentUssdActivity
import com.pins.infinity.adapters.RecyclerAdapter
import com.pins.infinity.database.SettingsManager
import com.pins.infinity.database.daos.UserDao
import com.pins.infinity.externals.MutableLiveArrayList
import com.pins.infinity.repositories.PaymentRepository
import com.pins.infinity.viewModels.base.BaseRowViewModel
import com.pins.infinity.viewModels.rows.PaymentRowViewModel

/**
 * Created by Pavlo Melnyk on 27.11.2018.
 */
class PaymentViewModel(application: Application,
                       private val userDao: UserDao,
                       private val paymentRepository: PaymentRepository,
                       private val settingsManager: SettingsManager)
    : BasePaymentViewModel(application), RecyclerAdapter.OnRowClickListener {

    private val paymentElements = MutableLiveArrayList<PaymentRowViewModel>()

    val adapter = RecyclerAdapter(paymentElements) {
        mapOf(BaseRowViewModel.DEFAULT_ITEM to R.layout.row_payment_choose)
    }

    init {
        val elements = listOf(
                PaymentRowViewModel(this, title = R.string.paymentChoose_google_pay_title, subtitle = R.string.paymentChoose_google_pay_subtitle, image = R.drawable.ic_google_play, isImageVisible = true, paymentOption = PaymentPlanViewModel.PaymentOptions.GOOGLE_PAY),
                PaymentRowViewModel(this, title = R.string.paymentChoose_instore_title, subtitle = R.string.paymentChoose_instore_subtitle, image = R.drawable.ic_instore, isImageVisible = true, paymentOption = PaymentPlanViewModel.PaymentOptions.INSTORE),
                PaymentRowViewModel(this, title = R.string.paymentChoose_card_title, subtitle = R.string.paymentChoose_card_subtitle, image = R.drawable.ic_paystack, isImageVisible = true, paymentOption = PaymentPlanViewModel.PaymentOptions.CARD),
                PaymentRowViewModel(this, title = R.string.paymentChoose_ussd_title, subtitle = R.string.paymentChoose_ussd_subtitle, image = R.drawable.ic_paystack, isImageVisible = false, paymentOption = PaymentPlanViewModel.PaymentOptions.USSD))
        paymentElements.addAll(elements)

        paymentElements.getItem(0).isChecked.set(true)

        adapter.onRowClickListener = this
    }

    override fun onRowClick(model: Any) {
        if (model !is PaymentRowViewModel) {
            return
        }

        paymentElements.value!!.forEach { it.isChecked.set(false) }
        model.isChecked.set(true)
    }


    fun continueCommand() {
        getPlansAndContinue()
    }

    private fun getPaymentOption(): String {
        return paymentElements.value?.firstOrNull { it -> it.isChecked.get() }?.paymentOption?.payment!!
    }

    private fun getPlansAndContinue() {
        isProgressDialog.value = true
        paymentRepository.getPaymentPlans()
                .subscribeAndDispose({
                    isProgressDialog.value = false
                    continuePayment()
                }, { throwable ->
                    isProgressDialog.value = false
                    showError(throwable, R.string.error_title)
                })
    }

    private fun continuePayment() {
        when (getPaymentOption()) {
            PaymentPlanViewModel.PaymentOptions.GOOGLE_PAY.payment,
            PaymentPlanViewModel.PaymentOptions.CARD.payment,
            PaymentPlanViewModel.PaymentOptions.INSTORE.payment -> {
                val intent = Intent(context(), PaymentPlanActivity::class.java)
                intent.putExtra(PaymentPlanActivity.PAYMENT_OPTION, getPaymentOption())
                startActivity.value = intent
            }
            PaymentPlanViewModel.PaymentOptions.USSD.payment -> {
                val intent = Intent(context(), PaymentUssdActivity::class.java)
                startActivity.value = intent
            }
        }
    }
}
