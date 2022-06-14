package com.pins.infinity.activity.emailVerification

import com.pins.infinity.R
import com.pins.infinity.activity.base.BaseActivity
import com.pins.infinity.viewModels.emailVerification.EmailVerifyConfirmViewModel
import com.pins.infinity.viewModels.emailVerification.EmailVerifyViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * Created by Pavlo Melnyk on 15.01.2019.
 */

class EmailVerifyConfirmActivity: BaseActivity<EmailVerifyConfirmViewModel>() {

    override val layout: Int = R.layout.activity_email_verify_confirm

    override val viewModel: EmailVerifyConfirmViewModel  by viewModel {
        val extra = intent.getBooleanExtra(EmailVerifyViewModel.EMAIL_VERIFY_RESULT_KEY, false)
        parametersOf(extra)
    }

}