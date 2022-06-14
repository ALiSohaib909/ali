package com.pins.infinity.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;

import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.pins.infinity.BuildConfig;
import com.pins.infinity.R;
import com.pins.infinity.utility.AppConstants;
import com.pins.infinity.utility.AppSharedPrefrence;

/**
 * Created by shri.kant on 24-10-2016.
 */
public class ActivitySplash extends Activity {

    private static final int PERMISSION_CALLBACK_CONSTANT = 100;
    private static final int REQUEST_PERMISSION_SETTING = 101;
    private static final String PERMISSION_STATUS = "permissionStatus";
    private static final String BUILD_CONFIG = "build_config";

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    String[] permissionsRequired = new String[]{//Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
                  Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            //                                               Manifest.permission.READ_CONTACTS,
            //                                               Manifest.permission.READ_CALL_LOG,
            //                                               Manifest.permission.SEND_SMS,
            //                                               Manifest.permission.RECEIVE_SMS,
            //                                               Manifest.permission.CALL_PHONE,
            //                                               Manifest.permission.READ_SMS
    };

    ProgressDialog dialog;
    private String profileResp = "";
    private boolean firstTime, showHelpScreens;
    private boolean found;
    private SharedPreferences permissionStatus;
    private boolean sentToSettings = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(BUILD_CONFIG, BuildConfig.BUILD_TYPE);
        Log.e(BUILD_CONFIG, BuildConfig.SERVER_AUTHORITY);
        Log.e(BUILD_CONFIG, BuildConfig.SERVER_URL);
        Log.e(BUILD_CONFIG, BuildConfig.TOKEN);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);


        AppConstants.IS_SERVICE_RUNNING = false;
        permissionStatus = getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE);

        System.out.println("permission ....... " + permissionStatus);

        profileResp = AppSharedPrefrence.getString(this, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO);
        System.out.println("profile ....... " + profileResp);

        firstTime = AppSharedPrefrence.getBoolean(this, AppSharedPrefrence.PREF_KEY.IS_FIRST_TIME);
        showHelpScreens = AppSharedPrefrence.getBoolean(this, AppSharedPrefrence.PREF_KEY.SHOW_HELP_SCREENS);
        checkPerm();
    }

    void checkPerm() {

        if (!arePermissionGranted()) {
            if (shouldShowRequestPermission()) {

                //Show Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySplash.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Camera, Location, Phone, Storage, Contacts ans SMS permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(ActivitySplash.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton(this.getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                });
                builder.show();
            } else if (permissionStatus.getBoolean(permissionsRequired[0], false)) {
                //Previously Permission Request was cancelled with 'Dont Ask Again',
                // Redirect to Settings after showing Information about why you need the permission
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySplash.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Camera, Location, Phone, Storage and Contacts permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        sentToSettings = true;
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                        Toast.makeText(getBaseContext(), "Go to Permissions to Grant Camera, Location, Phone, Storage and Contacts", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton(this.getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                });
                builder.show();
            } else {
                //just request the permission
                ActivityCompat.requestPermissions(ActivitySplash.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
            }

            SharedPreferences.Editor editor = permissionStatus.edit();
            editor.putBoolean(permissionsRequired[0], true);
            editor.commit();
        } else {
            //You already have the permission, just go ahead.
            proceedAfterPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_CALLBACK_CONSTANT) {
            //check if all permissions are granted
            boolean allgranted = false;
            for (int i = 0; i < grantResults.length; i++) {

                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    allgranted = true;
                } else {
                    allgranted = false;
                    break;
                }
            }

            if (allgranted) {
                proceedAfterPermission();
            } else if (shouldShowRequestPermission()) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ActivitySplash.this);
                builder.setTitle("Need Multiple Permissions");
                builder.setMessage("This app needs Camera, Location, Phone, Storage, Contacts and SMS permissions.");
                builder.setPositiveButton("Grant", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        ActivityCompat.requestPermissions(ActivitySplash.this, permissionsRequired, PERMISSION_CALLBACK_CONSTANT);
                    }
                });
                builder.setNegativeButton(this.getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        finish();
                    }
                });
                builder.show();
            } else {
                Toast.makeText(getBaseContext(), R.string.error_permission, Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean arePermissionGranted() {
        for (String permission : permissionsRequired) {
            if (ActivityCompat.checkSelfPermission(ActivitySplash.this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private boolean shouldShowRequestPermission() {
        for (String permission : permissionsRequired) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ActivitySplash.this, permission)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            if (!arePermissionGranted()) {
                checkPerm();
            } else {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }

    private void proceedAfterPermission() {

        new Handler().postDelayed(new Runnable() {
            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                onPermissionCheckAndSetupResult();
            }
        }, SPLASH_TIME_OUT);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        System.out.println("post resume");
        if (sentToSettings) {
            if (!arePermissionGranted()) {
                checkPerm();
            } else {
                //Got Permission
                proceedAfterPermission();
            }
        }
    }

    void onPermissionCheckAndSetupResult() {
        AppSharedPrefrence.putBoolean(ActivitySplash.this, AppSharedPrefrence.PREF_KEY.IS_FIRST_TIME, true);

        if (profileResp != null && profileResp.length() > 0) {
            Intent intent = new Intent(ActivitySplash.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
            finish();
        } else {

            if (!showHelpScreens) {
                Intent intent = new Intent(ActivitySplash.this, HelpScreens.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
                AppSharedPrefrence.putBoolean(ActivitySplash.this, AppSharedPrefrence.PREF_KEY.SHOW_HELP_SCREENS, true);
            } else {
                Intent intent = new Intent(ActivitySplash.this, LoginRegisterActivity.class);
                startActivity(intent);
                overridePendingTransition(0, 0);
                finish();
            }
        }
    }
}
