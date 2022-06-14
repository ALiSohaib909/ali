package com.pins.infinity.utility.binding

import androidx.databinding.BindingAdapter
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
/**
 * Created by Pavlo Melnyk on 2018-12-10.
 */

@BindingAdapter("android:srcDrawable")
fun ImageView.src(drawable: Drawable) {
    this.setImageDrawable(drawable)
}