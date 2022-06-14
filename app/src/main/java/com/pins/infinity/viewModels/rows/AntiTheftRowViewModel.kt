package com.pins.infinity.viewModels.rows

import com.pins.infinity.viewModels.antitheft.AntiTheftCommand
import com.pins.infinity.viewModels.antitheft.AntiTheftViewModel
import com.pins.infinity.viewModels.base.BaseRowViewModel

/**
 * Created by Pavlo Melnyk on 2019-03-20.
 */
class AntiTheftRowViewModel(
        parentViewModel: AntiTheftViewModel,
        val title: Int,
        val subtitle: Int,
        val image: Int,
        val isGray: Boolean,
        val antiTheftOption: AntiTheftCommand
) : BaseRowViewModel(parentViewModel.context())
