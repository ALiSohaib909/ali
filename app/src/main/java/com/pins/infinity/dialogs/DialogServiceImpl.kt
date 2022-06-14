package com.pins.infinity.dialogs

import android.app.Dialog
import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.Window
import android.widget.Button
import com.afollestad.materialdialogs.MaterialDialog
import com.pins.infinity.R
import io.reactivex.Single
import io.reactivex.SingleEmitter

/**
 * Created by Pavlo Melnyk on 30.11.2018.
 */
class DialogServiceImpl : DialogService {
    override fun okDialog(activity: AppCompatActivity, title: Int?, message: Int): Single<Boolean> {
        return Single.create { e ->
            val dialog = MaterialDialog.Builder(activity)
            title?.let { dialog.title(it) }
            dialog.content(message)
                    .cancelable(false)
                    .positiveText(android.R.string.ok)
                    .onPositive { _, _ -> onResponse(e, true) }
                    .show()
        }
    }

    override fun yesNoDialog(activity: AppCompatActivity, title: Int, message: Int): Single<Boolean> {
        return Single.create { emitter ->
            MaterialDialog.Builder(activity)
                    .title(title)
                    .content(message)
                    .positiveText(R.string.yes)
                    .negativeText(android.R.string.no)
                    .onPositive { _, _ -> onResponse(emitter, true) }
                    .onNegative { dialog, action ->
                        dialog.dismiss()
                        onResponse(emitter, false)
                    }
                    .build()
                    .show()
        }
    }

    private var progressDialog: MaterialDialog? = null

    override fun showProgressDialog(activity: AppCompatActivity) {
        progressDialog = MaterialDialog.Builder(activity)
                .content(R.string.please_wait)
                .progress(true, 0)
                .cancelable(false)
                .show()
    }

    override fun hideProgressDialog() {
        progressDialog?.dismiss()
    }

    override fun showAlertOkDialog(context: Context, title: String?, message: String) {
        AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes) { dialog, whichButton -> }
                .show()
    }

    private fun <T> onResponse(source: SingleEmitter<T>, response: T) = source.onSuccess(response)

    override fun informationDialog(context: Context, contentView: Int) {
        Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(true)
            setContentView(contentView)
            (findViewById<View>(R.id.promptContinueButton) as Button).apply { setOnClickListener { dismiss() } }
            show()
        }
    }
}