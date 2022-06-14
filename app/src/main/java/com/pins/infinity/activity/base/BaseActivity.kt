package com.pins.infinity.activity.base

import androidx.lifecycle.Observer
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import com.pins.infinity.BR
import com.pins.infinity.dialogs.DialogService
import com.pins.infinity.viewModels.base.BaseViewModel
import org.koin.android.ext.android.inject

/**
 * Created by Pavlo Melnyk on 22.10.2018.
 */

abstract class BaseActivity<out T : BaseViewModel> : AppCompatActivity() {

    protected abstract val viewModel: T

    private val dialogService: DialogService by inject()

    @get:LayoutRes
    protected abstract val layout: Int

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        val dataBinding: ViewDataBinding = DataBindingUtil.setContentView(this, layout)

        dataBinding.setVariable(BR.viewModel, viewModel)
        dataBinding.setLifecycleOwner(this)
        bindObservables()
    }

    protected open fun bindObservables() {
        viewModel.startActivity.observe(this, Observer { startActivity(it) })
        viewModel.startService.observe(this, Observer { startService(it) })
        viewModel.closeActivity.observe(this, Observer { onBackPressed() })
        viewModel.isProgressDialog.observeForever { this.isProgressDialog(it!!) }
        viewModel.errorDialog.observeForever { resource ->
            dialogService.okDialog(this, null, resource!!)
                    .subscribe()
        }
        viewModel.showDialog.observe(this, Observer { openDialog(it!!) })
    }

    fun isProgressDialog(isVisible: Boolean) {
        if (isVisible) {
            dialogService.showProgressDialog(this)
        } else {
            dialogService.hideProgressDialog()
        }
    }

    private fun openDialog(resource: Int) {
        this.dialogService.okDialog(this, null, resource).subscribe()
    }
}
