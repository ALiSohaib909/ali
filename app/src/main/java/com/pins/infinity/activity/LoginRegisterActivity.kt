package com.pins.infinity.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.pins.infinity.R
import com.pins.infinity.activity.base.BaseActivity
import com.pins.infinity.viewModels.authorisation.LoginRegisterViewModel
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Created by Pavlo Melnyk on 24-10-2016.
 */
class LoginRegisterActivity : BaseActivity<LoginRegisterViewModel>() {

    override val viewModel: LoginRegisterViewModel by viewModel()

    override val layout = R.layout.activity_login_register

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)

        this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        with(viewModel) {
            dialogService.okDialog(this@LoginRegisterActivity, R.string.device_admin_disclosure_title, R.string.device_admin_disclosure_message)
                    .subscribe({

                        showProgress()
                        userAgreed = true
                        nextScreen()


                    }, {
                        showProgress()
                    })
        }
    }

    override fun onBackPressed() {
        askAppClose()
    }

    @SuppressLint("CheckResult")
    private fun askAppClose() {
        viewModel.dialogService.yesNoDialog(this, R.string.warning, R.string.warning_exiting_app)
                .filter { yes -> yes }
                .subscribe { finish() }
    }
}