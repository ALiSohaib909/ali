package com.pins.infinity.activity.emailVerification

import com.pins.infinity.R
import com.pins.infinity.activity.base.BaseActivity
import com.pins.infinity.viewModels.emailVerification.EmailVerifyViewModel
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Created by Pavlo Melnyk on 15.01.2019.
 */

class EmailVerifyActivity: BaseActivity<EmailVerifyViewModel>() {
    override val layout: Int = R.layout.activity_email_verify

    override val viewModel: EmailVerifyViewModel  by viewModel()
}