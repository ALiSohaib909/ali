package com.pins.infinity.application

import android.app.Application
import android.location.Location
import com.ikarussecurity.android.theftprotection.IkarusSimChangeDetector
import com.ikarussecurity.android.utils.IkarusLog
import com.pins.infinity.application.ikarus.SimChangeNotifier
import com.pins.infinity.modules.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Created by Pavlo Melnyk on 27.11.2018.
 */

class PinsApplication : Application() {
    private var simChangeNotifier: SimChangeNotifier? = null

    private val TAG = "PIN"

    private val TAG_ADDRESS = "address_to_shared"


    override fun onCreate() {
        super.onCreate()
        // start Koin context
        startKoin {
            androidContext(this@PinsApplication)
            modules(listOf(viewModelModule))
        }
    }

    companion object {
        private var applicationInstance: PinsApplication? = null

        fun getInstance(): PinsApplication {
            return this.applicationInstance!!
        }

        fun onAddressUpdate(location: Location) {
        }
    }

    fun onAddressUpdate(location: Location) {
    }

    private fun initializeIkarus() {
        IkarusLog.setImplementation(IkarusLog.DEFAULT_IMPLEMENTATION)
        //TODO : Disable ikarus for testing. Unkoment on release
        //TODO : Disable ikarus for testing. Unkoment on release

        //        IkarusMalwareDetection.initialize(this);
        IkarusSimChangeDetector.initialize(this)
        //        IkarusAppLaunchDetector.initialize(this);
        //        IkarusProcessRestarter.enable(this, true);

        //        mInfectionNotifier = new InfectionNotifier(this);
        //        mMaliciousWebPageNotifier = new MaliciousWebPageNotifier(this);
        simChangeNotifier = SimChangeNotifier(this)

        //        IkarusMalwareDetection.registerScanListener(mInfectionNotifier);
        //        IkarusMalwareDetection.registerWebFilteringListener(mMaliciousWebPageNotifier);
        IkarusSimChangeDetector.registerListener(simChangeNotifier)
        IkarusSimChangeDetector.enable(true)

        //        IkarusSmsBlacklist.initialize(this);
        //        IkarusRemoteControl.initialize(this);

    }

    override fun onLowMemory() {
        super.onLowMemory()
        System.gc()
    }
}