package com.pins.infinity.viewModels.rows

import androidx.databinding.ObservableBoolean
import android.graphics.drawable.Drawable
import com.pins.infinity.viewModels.applock.AppLockViewModel
import com.pins.infinity.viewModels.base.BaseRowViewModel

/**
 * Created by Pavlo Melnyk on 25.04.2019.
 */
class AppLockRowViewModel(
        parentViewModel: AppLockViewModel,
        var title: String,
        var packageName: String,
        var iconResource: Drawable
) : BaseRowViewModel(parentViewModel.context()){

    val isChecked = ObservableBoolean()
}