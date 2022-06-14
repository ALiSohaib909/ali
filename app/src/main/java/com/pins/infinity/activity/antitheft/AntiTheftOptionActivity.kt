package com.pins.infinity.activity.antitheft

import com.pins.infinity.R
import com.pins.infinity.activity.base.BaseActivity
import com.pins.infinity.viewModels.antitheft.AntiTheftOptionViewModel
import com.pins.infinity.viewModels.antitheft.AntiTheftViewModel
import org.koin.android.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

/**
 * Created by Pavlo Melnyk on 2019-04-04.
 */
class AntiTheftOptionActivity : BaseActivity<AntiTheftOptionViewModel>() {

    override val layout: Int = R.layout.activity_anti_theft_option

    override val viewModel: AntiTheftOptionViewModel  by viewModel {
        val option = intent.getSerializableExtra(AntiTheftViewModel.ANTI_THEFT_OPTIONS)
        parametersOf(option)
    }
}