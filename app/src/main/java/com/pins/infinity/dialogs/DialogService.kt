package com.pins.infinity.dialogs

import android.app.Activity
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Single

/**
 * Created by Pavlo Melnyk on 30.11.2018.
 */
interface DialogService {
    fun showProgressDialog(activity: AppCompatActivity)
    fun okDialog(activity: AppCompatActivity, title: Int?, message: Int): Single<Boolean>
    fun yesNoDialog(activity: AppCompatActivity, title: Int, message: Int): Single<Boolean>
    fun showAlertOkDialog(context: Context, title: String?, message: String)
    fun informationDialog(context: Context, contentView: Int)
    fun hideProgressDialog()
}