package com.pins.infinity.application.ikarus;

/**
 * Created by Pavlo Melnyk on 31.07.2018.
 */
import android.content.Context;
import android.widget.Toast;

import com.ikarussecurity.android.internal.utils.Log;
import com.ikarussecurity.android.theftprotection.IkarusDeviceLocker;
import com.ikarussecurity.android.theftprotection.IkarusSimChangeDetector;

// This class is a very simple IkarusSimChangeDetector.Listener implementation.
// It is instantiated in TestApplication's onCreate method and is never
// destroyed, so it will keep detecting SIM-chard changes for the entire
// duration of the app.
//
// When a SIM change is detected, it displays a simple notification and locks
// the device with IkarusLockScreen. Locking the screen is a very typical
// example of what might want to do when a SIM-card change is detected.

public class SimChangeNotifier implements IkarusSimChangeDetector.Listener {

    private final Context context;

    public SimChangeNotifier(Context context) {
        this.context = context;
    }

    @Override
    public void onSimChanged(String oldSimId, String newSimId) {

        // oldSimId and newSimId do not have to be used, but you could easily
        // use them for an "allowed SIM cards" feature.

        Toast.makeText(context, "SIM changed!", Toast.LENGTH_LONG).show();
        try {
            //TODO remove when ikarus will be activated
//            IkarusDeviceLocker.lock();
        } catch (Exception e) {
            Log.e("SIM Change", e);
        }
    }

    @Override
    public void onPermissionNotAvailable(String permission) {
        // TODO Auto-generated method stub

    }
}
