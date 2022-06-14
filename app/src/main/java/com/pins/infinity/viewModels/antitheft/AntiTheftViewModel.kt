package com.pins.infinity.viewModels.antitheft

import android.app.Application
import android.content.Intent
import com.pins.infinity.R
import com.pins.infinity.activity.antitheft.AntiTheftOptionActivity
import com.pins.infinity.adapters.RecyclerAdapter
import com.pins.infinity.database.SettingsManager
import com.pins.infinity.externals.MutableLiveArrayList
import com.pins.infinity.viewModels.base.BaseRowViewModel
import com.pins.infinity.viewModels.base.BaseViewModel
import com.pins.infinity.viewModels.rows.AntiTheftRowViewModel

/**
 * Created by Pavlo Melnyk on 2019-04-01.
 */
class AntiTheftViewModel(application: Application,
                         private val settingsManager: SettingsManager)
    : BaseViewModel(application), RecyclerAdapter.OnRowClickListener {

    private val paymentElements = MutableLiveArrayList<AntiTheftRowViewModel>()

    val adapter = RecyclerAdapter(paymentElements) {
        mapOf(BaseRowViewModel.DEFAULT_ITEM to R.layout.row_anti_theft)
    }

    init {
        val elements = listOf(
                AntiTheftRowViewModel(this, title = R.string.antiTheftView_locationTrackingTitle, subtitle = R.string.antiTheftView_locationTrackingDescription, image = R.drawable.ic_anti_theft_location, isGray = true, antiTheftOption = AntiTheftCommand.LOCATION_TRACKING),
                AntiTheftRowViewModel(this, title = R.string.antiTheftView_ringMyPhoneTitle, subtitle = R.string.antiTheftView_ringMyPhoneDescription, image = R.drawable.ic_anti_theft_ring, isGray = false, antiTheftOption = AntiTheftCommand.RING_PHONE),
                AntiTheftRowViewModel(this, title = R.string.antiTheftView_remoteWipeTitle, subtitle = R.string.antiTheftView_remoteWipeDescription, image = R.drawable.ic_anti_theft_wipe, isGray = true, antiTheftOption = AntiTheftCommand.REMOTE_WIPE))
        paymentElements.addAll(elements)

        adapter.onRowClickListener = this
    }

    override fun onRowClick(model: Any) {
        if (model !is AntiTheftRowViewModel) {
            return
        }
        continueCommand(model.antiTheftOption)
    }


    fun continueCommand(option: AntiTheftCommand) {
        val intent = Intent(context(), AntiTheftOptionActivity::class.java)
        intent.putExtra(ANTI_THEFT_OPTIONS, option)
        startActivity.value = intent
    }


    companion object {
        const val ANTI_THEFT_OPTIONS = "AntiTheftCommand"
    }
}

enum class AntiTheftCommand(val option: String) {
    LOCATION_TRACKING("locate"),
    RING_PHONE("ring"),
    REMOTE_WIPE("wipe"),
    WRONG_COMMAND("wrongCommand");

    companion object {
        fun fromValue(value: String): AntiTheftCommand = values().find { it.option == value }
                ?: AntiTheftCommand.WRONG_COMMAND
    }
}
