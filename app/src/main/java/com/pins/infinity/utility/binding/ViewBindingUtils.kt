package com.pins.infinity.utility.binding

import androidx.databinding.BindingAdapter
import android.view.View
/**
 * Created by Pavlo Melnyk on 08.11.2018.
 */

@BindingAdapter("app:isVisible")
fun View.isVisible(visible: Boolean) {
    this.visibility = if (visible) View.VISIBLE else View.GONE
}