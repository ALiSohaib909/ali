package com.pins.infinity.viewModels

import android.app.Application
import androidx.lifecycle.MutableLiveData
import android.content.Intent
import androidx.databinding.ObservableBoolean
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.pins.infinity.R
import com.pins.infinity.activity.PaymentSelection
import com.pins.infinity.activity.PaymentSelection.*
import com.pins.infinity.activity.payment.PaymentCodeActivity
import com.pins.infinity.activity.payment.PaymentGoogleActivity
import com.pins.infinity.activity.payment.PaymentSummaryActivity
import com.pins.infinity.database.daos.PaymentDao
import com.pins.infinity.repositories.PaymentRepository
import com.pins.infinity.repositories.models.StartTransactionModel
import com.pins.infinity.repositories.models.SummaryComputationModel
import com.pins.infinity.services.billing.getSkuForChosenOption
import java.util.*

/**
 * Created by Pavlo Melnyk on 2018-12-03.
 */
class PaymentSummaryViewModel(
        application: Application,
        private val paymentRepository: PaymentRepository,
        private val paymentDao: PaymentDao,
        private val chosenOption: PaymentPlanViewModel.ChosenOption)
    : BasePaymentViewModel(application), AdapterView.OnItemSelectedListener {
    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        onRowClick(spinnerElements[position])
    }

    private val spinnerElements = ArrayList<String>()

    val isRecovery = ObservableBoolean()
    private val isError = ObservableBoolean()
    val summaryModel = MutableLiveData<SummaryComputationModel>()

    var dataAdapter: ArrayAdapter<String>

    init {

        isRecovery.set(chosenOption.isRecovery)

        spinnerElements.add(context().getString(R.string.paymentSummary_month))
        spinnerElements.add(context().getString(R.string.paymentSummary_year))

        dataAdapter = ArrayAdapter(context(), R.layout.payment_spinner_item, spinnerElements)
        dataAdapter.setDropDownViewResource(R.layout.payment_spinner_dropdown_item)

        onRowClick(spinnerElements[0])
    }

    fun onRowClick(model: Any) {
        if (model !is String || isProgressDialog.value == true) {
            return
        }

        val duration = getDuration(model)
        chosenOption.durationOption = duration

        paymentRepository.getSummaryComputationElements(
                StartTransactionModel(chosenOption)).subscribeAndDispose({ summary ->
            summaryModel.value = summary
        })
    }

    private fun getDuration(model: String) =
            if (spinnerElements.indexOf(model) == 0) PaymentPlanViewModel.DurationOption.MONTH.duration
            else PaymentPlanViewModel.DurationOption.YEAR.duration

    fun initPaymentAndContinue() {
        isProgressDialog.value = true
            paymentRepository.paymentInit(StartTransactionModel(chosenOption)).subscribe({ summary ->
                summaryModel.value = summary
                isProgressDialog.value = false
                continueCommand()
                isError.set(false)
            }, {
                isProgressDialog.value = false
                isError.set(true)
            })
    }

    fun continueCommand() {
        when {
            isError.get() -> {
                showError(errorRes = R.string.error_title)
            }
            chosenOption.paymentOption == PaymentPlanViewModel.PaymentOptions.GOOGLE_PAY.payment -> {
                val intent = Intent(context(), PaymentGoogleActivity::class.java)
                intent.putExtra(PaymentSummaryActivity.CHOSEN_OPTION, chosenOption)
                intent.putExtra(PaymentGoogleActivity.SKU, getSkuForChosenOption(chosenOption.planOption, chosenOption.durationOption))
                startActivity.value = intent
            }
            chosenOption.paymentOption == PaymentPlanViewModel.PaymentOptions.CARD.payment -> {
                startActivity.value = createPaymentSelectionIntent()
            }
            else -> {
                val intent = Intent(context(), PaymentCodeActivity::class.java)
                intent.putExtra(PaymentSummaryActivity.CHOSEN_OPTION, chosenOption)
                startActivity.value = intent
            }
        }
    }


    private fun createPaymentSelectionIntent() =
            Intent(context(), PaymentSelection::class.java).run {
                putExtra(TRANSACTION_ID, paymentDao.getInitItem().blockingGet().transId)
                putExtra(TOTAL_AMOUNT, summaryModel.value?.planPrice?.value?.toInt())
                putExtra(PLAN_NAME, chosenOption.planOption)
                putExtra(TOTAL_IN_UNIT, paymentDao.getInitItem().blockingGet().totalInUnit.toInt())
            }
}
