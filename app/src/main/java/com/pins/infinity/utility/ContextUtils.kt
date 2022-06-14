package com.pins.infinity.utility

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import androidx.core.app.ActivityCompat
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.pins.infinity.viewModels.PaymentUssdViewModel


/**
 * Created by Pavlo Melnyk on 29.11.2018.
 */

fun Context.isInternetConnection(): Boolean {
    val connect = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (connect.activeNetworkInfo == null) return false
    return connect.activeNetworkInfo.isConnected
}

fun Context.callUssd(ussd: String): Boolean {
    return if (checkCallPermission(this)) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = ussdToCallableUri("${PaymentUssdViewModel.TEL_TAG}$ussd")
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        this.startActivity(intent)
        true
    } else {
        false
    }
}


private fun checkCallPermission(context: Context): Boolean {
    return ActivityCompat.checkSelfPermission(context,
            Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
}


fun Context.hideKeyboard(view: View) {
    val inputMethodManager: InputMethodManager = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}