package com.pins.infinity.repositories

import android.bluetooth.BluetoothClass
import com.pins.infinity.database.models.DeviceItem

/**
 * Created by Pavlo Melnyk on 19.10.2020.
 */
interface DeviceRepository {
    val device: DeviceItem?
}
