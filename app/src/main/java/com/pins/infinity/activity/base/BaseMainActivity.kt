package com.pins.infinity.activity.base

import android.annotation.SuppressLint
import android.util.Log
import com.pins.infinity.R
import com.pins.infinity.viewModels.base.BaseMainViewModel
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Created by Pavlo Melnyk on 22.10.2018.
 */

open class BaseMainActivity : BaseActivity<BaseMainViewModel>() {

    // TODO: This class links
    // old java MVC implementation in ManiActivity with new repository.
    // It will be removed when MainActivity will be formated to kotlin and MVVM

    override val layout: Int = R.layout.activity_main_app
    override val viewModel: BaseMainViewModel  by viewModel()

    @SuppressLint("CheckResult")
    fun initPaymentCheck() {
        viewModel.initPaymentCheck().subscribe({
            setEmailVerifiedResponse(isEmailVerified())
            setIntruderResponse(isIntruderActive() && isEmailVerified())
        }, { throwable ->
            viewModel.showError(throwable, R.string.error_title)
        })
    }

    fun isEmailVerified() = viewModel.getEmailVerified().blockingGet()

    fun isIntruderActive() = viewModel.getIntruderActive().blockingGet()

    @SuppressLint("CheckResult")
    fun setIntruderActive(isIntruderActive: Boolean) {
        viewModel.setIntruderActive(isIntruderActive).subscribe({ response ->
            setIntruderResponse(response && isEmailVerified())

        }, { throwable ->
            viewModel.showError(throwable, R.string.error_title)
        })
    }

    open fun setIntruderResponse(isActive: Boolean) {
        Log.d("setIntruderResponse", "super")
    }

    open fun setEmailVerifiedResponse(isActive: Boolean) {
        Log.d("setIntruderResponse", "super")
    }
}


