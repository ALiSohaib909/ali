package com.pins.infinity.activity.payment

import android.content.Intent
import com.pins.infinity.R
import com.pins.infinity.activity.base.BaseActivity
import com.pins.infinity.viewModels.PaymentUssdViewModel
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Created by Pavlo Melnyk on 2018-12-07.
 */
class PaymentUssdActivity : BaseActivity<PaymentUssdViewModel>() {

    override val layout: Int = R.layout.activity_payment_ussd

    override val viewModel: PaymentUssdViewModel  by viewModel()

    override fun onResume() {
        super.onResume()
        if(viewModel.isDialing.value == true){
            val intent = Intent(this, PaymentUssdProgressActivity::class.java)
            intent.putExtra(PaymentUssdProgressActivity.IS_DIAL, viewModel.isDialing.value!!)
            viewModel.startActivity.value = intent
        }
    }
}