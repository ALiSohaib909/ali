package com.pins.infinity.viewModels.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import android.content.Context
import android.content.Intent
import androidx.annotation.StringRes
import android.view.View
import com.pins.infinity.R
import com.pins.infinity.activity.MainActivity
import com.pins.infinity.api.utils.getError
import com.pins.infinity.externals.SingleLiveEvent
import com.pins.infinity.utility.Errors
import com.pins.infinity.utility.log

/**
 * Created by Pavlo Melnyk on 27.11.2018.
 */
abstract class BaseViewModel(application: Application) :
        AndroidViewModel(application),
        DisposableComponent by DisposableComponentImpl() {

    val startActivity: SingleLiveEvent<Intent> = SingleLiveEvent()
    val startService: SingleLiveEvent<Intent> = SingleLiveEvent()
    val closeActivity: SingleLiveEvent<Boolean> = SingleLiveEvent()
    var isProgressDialog: MutableLiveData<Boolean> = SingleLiveEvent()
    var errorDialog: MutableLiveData<Int> = SingleLiveEvent()
    var showDialog: MutableLiveData<Int> = SingleLiveEvent()

    open val onBackPressed: View.OnClickListener = View.OnClickListener {
        closeActivity.call()
    }

    override fun onCleared() {
        super.onCleared()
        disposeAll()
    }

    fun showError(error: Throwable = Throwable(), @StringRes errorRes: Int) {
        isProgressDialog.value = false

        if (error is Errors.NoInternetConnection) {
            errorDialog.value = R.string.error_no_internet_connection
        } else {
            errorDialog.value = errorRes
        }
    }

    fun showErrorMessage(error: Throwable) {
        isProgressDialog.value = false
        error.log()
        errorDialog.value = error.getError(defaultError = R.string.error_title)
    }

    fun context(): Context = getApplication<Application>().applicationContext

    open fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    }

    open fun homeCommand() {
        val intent = Intent(context(), MainActivity::class.java)
        startActivity.value = intent
    }
}
