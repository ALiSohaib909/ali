package com.pins.infinity.activity.antitheft

import com.pins.infinity.R
import com.pins.infinity.activity.base.BaseActivity
import com.pins.infinity.viewModels.antitheft.AntiTheftViewModel
import org.koin.android.viewmodel.ext.android.viewModel

/**
 * Created by Pavlo Melnyk on 2019-03-25.
 */
class AntiTheftActivity : BaseActivity<AntiTheftViewModel>() {

    override val layout: Int = R.layout.activity_anti_theft

    override val viewModel: AntiTheftViewModel  by viewModel()
}