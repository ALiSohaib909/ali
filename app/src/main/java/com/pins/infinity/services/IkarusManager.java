package com.pins.infinity.services;

import android.app.Activity;
import android.util.Log;

import com.ikarussecurity.android.appblocking.IkarusAppLaunchDetector;
import com.ikarussecurity.android.databaseupdates.DatabaseAvailabilityCheckEvent;
import com.ikarussecurity.android.databaseupdates.DatabaseAvailabilityCheckResult;
import com.ikarussecurity.android.databaseupdates.DatabaseUpdateEvent;
import com.ikarussecurity.android.databaseupdates.DatabaseUpdateResult;
import com.ikarussecurity.android.databaseupdates.IkarusDatabaseAvailabilityCheckListener;
import com.ikarussecurity.android.databaseupdates.IkarusDatabaseUpdater;
import com.ikarussecurity.android.databaseupdates.IkarusDatabaseUpdaterListener;
import com.ikarussecurity.android.malwaredetection.IkarusMalwareDetection;
import com.ikarussecurity.android.malwaredetection.IkarusScanListener;
import com.ikarussecurity.android.malwaredetection.IkarusWebFilteringListener;
import com.ikarussecurity.android.malwaredetection.InfectedWebPageBlockedEvent;
import com.ikarussecurity.android.malwaredetection.InfectedWebPageFoundEvent;
import com.ikarussecurity.android.malwaredetection.ScanEvent;
import com.ikarussecurity.android.malwaredetection.ScanScope;
import com.ikarussecurity.android.smsblacklist.IkarusSmsBlacklistListener;
import com.ikarussecurity.android.theftprotection.IkarusPassword;
import com.ikarussecurity.android.theftprotection.remotecontrol.IkarusRemoteCommandLocate;
import com.ikarussecurity.android.theftprotection.remotecontrol.IkarusRemoteControl;
import com.ikarussecurity.android.theftprotection.remotecontrol.IkarusRemoteControlPassword;
import com.pins.infinity.BuildConfig;
import com.pins.infinity.R;
import com.pins.infinity.utility.DialogUtils;

import static com.pins.infinity.utility.Utility.showToast;

/**
 * Created by Pavlo Melnyk on 07.09.2018.
 */
public class IkarusManager implements IkarusDatabaseUpdaterListener, IkarusScanListener,
        IkarusSmsBlacklistListener, IkarusAppLaunchDetector.Listener,
        IkarusDatabaseAvailabilityCheckListener, IkarusWebFilteringListener {

    private final Activity mActivity;

    private static String mYourUpdateProductIdentifier = "dllupdatepinssolutions";
    private static final String IKARUS_IMP = "IKARUS_IMP";

    private boolean mIsDatabaseUpdated;

    public interface OnResponse {
        void call(String status);
    }


    public IkarusManager(Activity activity) {
        mActivity = activity;

        IkarusPassword.savePassword(mActivity, BuildConfig.IKARUS_SERVICE_PASSWORD);
        IkarusRemoteControlPassword.savePassword(mActivity, BuildConfig.IKARUS_SERVICE_PASSWORD);

        registerIkarusListeners();

        //TODO : uncomment when release
//        IkarusDatabaseUpdater.start(mActivity, mYourUpdateProductIdentifier, null);

        IkarusRemoteControl.addCommand("Locate", new IkarusRemoteCommandLocate(BuildConfig.IKARUS_SERVICE_PASSWORD + "%s",
                "location tracking did not work"));

    }


    private void registerIkarusListeners(){
        //TODO : Disable Ikarus because it slows down testing. Uncomment when release

//        IkarusDatabaseUpdater.registerListener(this);
//         IkarusMalwareDetection.registerScanListener(this);
//        IkarusDatabaseUpdater.registerAvailabilityCheckListener(this);
//        IkarusMalwareDetection.registerWebFilteringListener(this);
//        IkarusSmsBlacklist.registerListener(this);
//        IkarusAppLaunchDetector.registerListener(this);

//        if (IkarusMalwareDetection.isReadyToScan()) {
//            IkarusMalwareDetection.setWebFilteringMode(WebFilteringMode.MANUAL_BLOCKING);
//        }
    }

    public void unregisterIkarusListeners(){

        IkarusDatabaseUpdater.unregisterListener(this);
        IkarusMalwareDetection.unregisterScanListener(this);
        IkarusDatabaseUpdater.unregisterAvailabilityCheckListener(this);
        IkarusMalwareDetection.unregisterWebFilteringListener(this);
    }

    // ========================= Database ================================
    @Override
    public void onDatabaseAvailabilityCheckStarted(DatabaseAvailabilityCheckEvent databaseAvailabilityCheckEvent) {
    }

    @Override
    public void onDatabaseAvailabilityCheckProgress(DatabaseAvailabilityCheckEvent databaseAvailabilityCheckEvent) {

    }

    @Override
    public void onDatabaseAvailabilityCheckCompleted(DatabaseAvailabilityCheckResult databaseAvailabilityCheckResult, DatabaseAvailabilityCheckEvent databaseAvailabilityCheckEvent) {
    }

    @Override
    public void onDatabaseUpdateStarted(DatabaseUpdateEvent databaseUpdateEvent) {
        showToast(mActivity, mActivity.getString(R.string.ikarus_database_updated_started));

    }

    @Override
    public void onDatabaseUpdateProgress(DatabaseUpdateEvent databaseUpdateEvent) {

    }

    @Override
    public void onDatabaseUpdateCompleted(DatabaseUpdateResult databaseUpdateResult, DatabaseUpdateEvent databaseUpdateEvent) {
        Log.i(IKARUS_IMP, "Database update completed.");
        showToast(mActivity, mActivity.getString(R.string.ikarus_database_updated));
        DialogUtils.showAlertOkDialog(mActivity, null, mActivity.getString(R.string.ikarus_database_updated));

        IkarusMalwareDetection.startScan(ScanScope.APP_ONLY);
    }

    @Override
    public void onPermissionNotAvailable(String s) {

    }

    // ========================= SCAN ================================
    @Override
    public void onScanStarted(ScanEvent scanEvent) {
        showToast(mActivity, mActivity.getString(R.string.ikarus_start_scan));
        DialogUtils.showAlertOkDialog(mActivity, null, mActivity.getString(R.string.ikarus_start_scan));

        Log.i(IKARUS_IMP, "Start scan");
    }

    @Override
    public void onScanCompleted(ScanEvent scanEvent) {
        showToast(mActivity, mActivity.getString(R.string.ikarus_scan_completed));
        DialogUtils.showAlertOkDialog(mActivity, null, mActivity.getString(R.string.ikarus_scan_completed));

        Log.i(IKARUS_IMP, "Scan completed");
    }

    @Override
    public void onScanProgress(ScanEvent scanEvent) {

    }

    @Override
    public void onInfectionFound(ScanEvent scanEvent) {
        mActivity.runOnUiThread(() -> {
            DialogUtils.showAlertOkDialog(mActivity, null, mActivity.getString(R.string.ikarus_infection_found));
            showToast(mActivity, mActivity.getString(R.string.ikarus_infection_found));
            Log.i(IKARUS_IMP, "Infection found");
        });
    }

    @Override
    public void onInfectionRemoved(ScanEvent scanEvent) {

    }

    @Override
    public void onIgnoreListModified(ScanEvent scanEvent) {

    }

    @Override
    public void onInfectedPageBlocked(InfectedWebPageBlockedEvent infectedWebPageBlockedEvent) {

    }

    @Override
    public void onInfectedPageFound(InfectedWebPageFoundEvent infectedWebPageFoundEvent) {

    }

    @Override
    public void onInfectedPageBlockedByUser(InfectedWebPageFoundEvent infectedWebPageFoundEvent) {

    }

    @Override
    public void onInfectedPageIgnoredByUser(InfectedWebPageFoundEvent infectedWebPageFoundEvent) {

    }

    @Override
    public void onSmsBlocked(String s) {

    }

    @Override
    public void onSmsBlacklistChanged() {

    }

    @Override
    public void onAppLaunched(String s) {

    }

    @Override
    public void onUsageStatsPermissionNotGranted() {

    }
}
