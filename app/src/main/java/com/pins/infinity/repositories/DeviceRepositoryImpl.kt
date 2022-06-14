package com.pins.infinity.repositories

import com.pins.infinity.database.daos.DeviceDao
import com.pins.infinity.database.models.DeviceItem

/**
 * Created by Pavlo Melnyk on 19.10.2020.
 */
class DeviceRepositoryImpl(
        private val deviceDao: DeviceDao
) : DeviceRepository {

    override val device: DeviceItem?
        get() = deviceDao.getThisDevice()
}