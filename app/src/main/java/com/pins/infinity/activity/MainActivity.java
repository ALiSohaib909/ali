package com.pins.infinity.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.Fragment;
import android.app.KeyguardManager;
import android.app.ProgressDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.navigation.NavigationView;
import com.microsoft.appcenter.AppCenter;
import com.pins.infinity.BuildConfig;
import com.pins.infinity.R;
import com.pins.infinity.activity.antitheft.AntiTheftActivity;
import com.pins.infinity.activity.antitheft.SetupPinActivity;
import com.pins.infinity.activity.applock.AppLockActivity;
import com.pins.infinity.activity.base.BaseMainActivity;
import com.pins.infinity.activity.emailVerification.EmailVerifyActivity;
import com.pins.infinity.activity.emailVerification.EmailVerifyConfirmActivity;
import com.pins.infinity.broadcast.ShutdownReceiver;
import com.pins.infinity.database.daos.DeviceDao;
import com.pins.infinity.fragments.FragmentDashBoard;
import com.pins.infinity.fragments.FragmentHelp;
import com.pins.infinity.fragments.FragmentHome;
import com.pins.infinity.fragments.FragmentProfile;
import com.pins.infinity.fragments.FragmentSetting;
import com.pins.infinity.interfaces.ApiResponseListener;
import com.pins.infinity.model.ProfileBaseModel;
import com.pins.infinity.services.IkarusManager;
import com.pins.infinity.services.LocationService;
import com.pins.infinity.services.applock.AppLaunchService;
import com.pins.infinity.services.simdetect.SimDetectAccessibilityService;
import com.pins.infinity.utility.ApiCall;
import com.pins.infinity.utility.ApiConstants;
import com.pins.infinity.utility.AppConstants;
import com.pins.infinity.utility.AppSharedPrefrence;
import com.pins.infinity.utility.DeviceUtils;
import com.pins.infinity.utility.Logger;
import com.pins.infinity.utility.MyAdmin;
import com.pins.infinity.utility.SharedPreferences.Const;
import com.pins.infinity.utility.SharedPreferences.Utils;
import com.pins.infinity.utility.Utility;
import com.pins.infinity.viewModels.antitheft.LockAction;
import com.pins.infinity.viewModels.antitheft.SetupPinViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import kotlin.Lazy;


import static com.pins.infinity.utility.Utility.showToast;
import static org.koin.java.KoinJavaComponent.inject;

public class MainActivity extends BaseMainActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener,
        ApiResponseListener {
    static final int RESULT_ENABLE = 1, LOCK_RESULT = 2, OVERLAY_PERMISSION_REQ_CODE = 1234;
    static final int LOCATION_UPDATE_FREQUENCY = 15000;
    static final int PIN_INTRUDER = 8;
    static final int PIN_INTRUDER1 = 81;

    public static Lazy<DeviceDao> deviceDao = inject(DeviceDao.class);

    private static int getIntruderButtonImage(boolean isActive) {
        return isActive ? R.drawable.ic_menu_intruder_on : R.drawable.ic_menu_intruder_off;
    }

    // permissions request code
    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
    private final static int REQUEST_CODE_ASK_PERMISSIONS3 = 13;
    // permissions that need to be explicitly requested from end user.
    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    public static NavigationView navigationView;
    static Toolbar toolbar;
    static ImageButton radioIntruderButton;
    private static boolean mIsIntruderActive;
    DevicePolicyManager deviceManger;
    ActivityManager activityManager;
    ComponentName compName;
    GoogleApiClient mGoogleApiClient;
    private IkarusManager mIkarusManager;
    private ShutdownReceiver shutdownReceiver;

    private ProgressDialog progressDialog;
    private String mToken = "";
    private String mUserId = "";

    public static void updateTitle(String title) {
        ImageView logo = (ImageView) toolbar.findViewById(R.id.logoImageView);
        TextView textView = (TextView) toolbar.findViewById(R.id.title);
        TextView lock = (TextView) toolbar.findViewById(R.id.lock);
        lock.setVisibility(View.GONE);
        logo.setVisibility(View.GONE);
//        edit = (TextView) toolbar.findViewById(R.id.edit);
        if (title.equalsIgnoreCase("Home")) {
//            edit.setVisibility(View.GONE);
            textView.setText("");
//            lock.setVisibility(View.VISIBLE);
            logo.setVisibility(View.VISIBLE);
        } else if (title.equalsIgnoreCase("Profile")) {
            toolbar.setLogo(R.color.transparent);
            textView.setText(title);
//            lock.setVisibility(View.GONE);
        } else {
//            edit.setVisibility(View.GONE);
            toolbar.setLogo(R.color.transparent);
            textView.setText(title);
//            lock.setVisibility(View.VISIBLE);
        }

        lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        shutdownReceiver = new ShutdownReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SHUTDOWN);
        filter.addDataScheme("package");

        this.registerReceiver(shutdownReceiver, filter);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        deviceManger = (DevicePolicyManager) getSystemService(
                Context.DEVICE_POLICY_SERVICE);
        activityManager = (ActivityManager) getSystemService(
                Context.ACTIVITY_SERVICE);
        compName = new ComponentName(this, MyAdmin.class);

        saveSimCardDetails();
        buildGoogleApiClient();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        checkPermissions();

        if (toolbar != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            }
            ImageView logo = (ImageView) toolbar.findViewById(R.id.logoImageView);
            toolbar.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            toolbar.setTitle("");
            logo.setVisibility(View.VISIBLE);
            toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimary));
        }

        loadHomeFragment();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getIfEmailVerifiedAndSetIntruderIsActive();

            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (getIntent() != null && getIntent().getBooleanExtra("restart", false)) {
            finish();
        }

        //TODO: enable when needed
//        mIkarusManager = new IkarusManager(this);
        MenuItem pinIntruder = navigationView.getMenu().getItem(PIN_INTRUDER);
        pinIntruder.setActionView(R.layout.menu_image);
//        radioIntruderButton = (ImageButton) ((ConstraintLayout) pinIntruder.getActionView()).getChildAt(0);
//        radioIntruderButton.setOnClickListener(v -> showPinsIntruder());
    }

    @Override
    public void setIntruderResponse(boolean isActive) {
//        mIsIntruderActive = isActive;
//        radioIntruderButton.setImageResource(getIntruderButtonImage(mIsIntruderActive));
    }

    @Override
    public void setEmailVerifiedResponse(boolean isVerified) {
        //       radioIntruderButton.setImageResource(getIntruderButtonImage(isVerified && mIsIntruderActive));
    }

    private void getIfEmailVerifiedAndSetIntruderIsActive() {
        boolean isVerified = super.isEmailVerified();
        boolean isActive = super.isIntruderActive();
//        radioIntruderButton.setImageResource(getIntruderButtonImage(isVerified && isActive));
    }

    private void showPinsIntruder() {
        boolean isVerified = super.isEmailVerified();
        boolean isActive = super.isIntruderActive();
        if (!isVerified) {
            showPinsIntruder(false);
        } else {
            super.setIntruderActive(!isActive);
        }
    }

    private void saveSimCardDetails() {
        TelephonyManager telephoneManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (telephoneManager != null) {
            Utils.saveSimCardData(this, telephoneManager);
        }
    }

    public void startLocationTracker() {
        Log.d(LocationService.TAG, "MainActivity.onCreate startLocationTracker");
        Intent pushIntent = new Intent(this, LocationService.class);
        this.startService(pushIntent);
    }

    private void startAppLockService() {
        Log.d(LocationService.TAG, "MainActivity.onCreate startLocationTracker");
        Intent pushIntent = new Intent(this, AppLaunchService.class);
        this.startService(pushIntent);
    }

    /**
     * Checks the dynamically controlled permissions and requests missing
     * permissions from end user.
     */
    protected void checkPermissions() {
        final List<String> missingPermissions = new ArrayList<>();
        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
            final int result = ContextCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission);
            }
        }
        if (missingPermissions.isEmpty()) {
            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                    grantResults);
        } else {
            final String[] permissions = missingPermissions.toArray(new String[missingPermissions.size()]);
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    @Override
    protected void onDestroy() {
        //TODO : Disable Ikarus because it slows down testing. Uncomment when release
//        mIkarusManager.unregisterIkarusListeners();
        System.out.println("MAIN ACTIVITY: ON_DESTROY");
        try {
            this.unregisterReceiver(shutdownReceiver);
        } catch (final IllegalArgumentException exception) {
            // The receiver was not registered.
            // There is nothing to do in that case.
            // Everything is fine.
        }

        SharedPreferences.Editor editor = this.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE).edit();
        editor.putBoolean(Const.SHUTDOWN, true);
        editor.apply();

        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPlayServicesAvailable();
//        getSubscriptionData();
        try {
            String response = AppSharedPrefrence.getString(MainActivity.this, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO);
            ProfileBaseModel profileBaseModel = new ObjectMapper().readValue(response, ProfileBaseModel.class);

            if (profileBaseModel != null && null != profileBaseModel.getResponse() && null != profileBaseModel.getResponse().getUser()) {
                // You can call any combination of these three methods
                AppCenter.setUserId(profileBaseModel.getResponse().getUser().getEmail());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (null == mGoogleApiClient) {
            buildGoogleApiClient();
        }

        /*boolean active = deviceManger.isAdminActive(compName);
        if (!DeviceUtils.isAppDeviceAdministrator(this)) {
            Intent intent = new Intent(DevicePolicyManager
                    .ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                    compName);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                    "Additional text explaining why this needs to be added.");
            startActivityForResult(intent, RESULT_ENABLE);
        } else {
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            boolean lockStatus = false;
            if (Build.VERSION.SDK_INT >= 23) {
                lockStatus = keyguardManager.isDeviceSecure();
            } else {
                lockStatus = keyguardManager.isKeyguardSecure();
            }

            if (!lockStatus) {
                Intent intent = new Intent(DevicePolicyManager.ACTION_SET_NEW_PASSWORD);
                startActivity(intent);
            } else {
                AppSharedPrefrence.putBoolean(MainActivity.this, AppSharedPrefrence.PREF_KEY.IS_LOCK_ACTIVE, true);
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    if (!Settings.canDrawOverlays(this)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                    } else {
                        if (!isAccessGranted()) {
                            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                            startActivity(intent);
                        } else {
                            if (!SimDetectAccessibilityService.isAccessibilityEnabled(this)) {
                                Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
                                startActivityForResult(intent, 0);
                            } else {
                                userFinishInitialSetup();
                            }
                        }
                    }
                } else {
                    userFinishInitialSetup();
                }
            }
        }*/
    }

    private void userFinishInitialSetup() {
        //init payment
        super.initPaymentCheck();

        //check if location service is running and if not enable it
        if (AppConstants.isMyServiceRunning(this, LocationService.class)) {
            Logger.log("location service is running thus not starting");
            return;
        }
        Logger.log("Start locationTracker");
        startLocationTracker();
        startAppLockService();
    }

    /**
     * Workaround for Android O
     * https://stackoverflow.com/questions/46173460/why-in-android-o-method-settings-candrawoverlays-returns-false-when-user-has
     */
    private static boolean canDrawOverlays(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(context)) {
            return true;
        }
        return addViewForDrawOverlay(context);
    }

    private static boolean addViewForDrawOverlay(Context context) {
        final String EXCEPTION_TAG = "Exception DrawOverlay";
        try {
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (windowManager == null) {
                return false; //getSystemService might return null
            }
            View viewToAdd = new View(context);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    0,
                    0,
                    Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ?
                            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY : WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSPARENT);
            viewToAdd.setLayoutParams(params);
            windowManager.addView(viewToAdd, params);
            windowManager.removeView(viewToAdd);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.log("Exception DrawOverlay: " + e.toString());
            return false;
        }
    }

    //checking for whether app usage permission provided
    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = 0;
            if (android.os.Build.VERSION.SDK_INT > android.os.Build.VERSION_CODES.KITKAT) {
                mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                        applicationInfo.uid, applicationInfo.packageName);
            }
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("Location", "onConnectionFailed");
    }

    private void checkPlayServicesAvailable() {
        final GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int status = apiAvailability.isGooglePlayServicesAvailable(this);

        if (status != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(status)) {
                apiAvailability.getErrorDialog(this, status, 1).show();
            } else {
                Toast.makeText(MainActivity.this, "Google Play Services unavailable. This app will not work", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

            super.onBackPressed();
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.home) {
            loadHomeFragment();
            // Handle the camera action
        } else if (id == R.id.overview) {
            loadAboutFragment();
        } else if (id == R.id.profile) {
            loadProfileFragment();
        } else if (id == R.id.terms_conditions) {
            Utility.openBrowser(MainActivity.this, BuildConfig.TERMS_AND_CONDITIONS_URL);
        } else if (id == R.id.help) {
            Utility.openBrowser(MainActivity.this, BuildConfig.HELP_URL);
        }/*else if (id == R.id.contact) {
            startActivity(new Intent(MainActivity.this, ActivityContacts.class));

        }*/ else if (id == R.id.settings) {
            loadSettingFragment();
        } else if (id == R.id.mydashboard) {
            Utility.openBrowser(MainActivity.this, BuildConfig.DASHBOARD_URL);
//            loadDashBoardFragment();
        } else if (id == R.id.logout) {

            showLogOutDialog();
        }
      /*  else if (id == R.id.pinsIntruder) {
            showPinsIntruder(super.isEmailVerified());
        }*/
       /* else if (id == R.id.antiTheft) {
            showAntiTheft(LockAction.ANTI_THEFT);
        } */
        else if (id == R.id.appLock) {
            showAntiTheft(LockAction.APP_LOCK);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadAboutFragment() {
        updateTitle("About Pins");
        if (!(getFragmentManager().findFragmentById(R.id.content_frame) instanceof FragmentHelp)) {
            getFragmentManager().beginTransaction().replace(R.id.content_frame, new FragmentHelp()).commit();
        }
    }

    private void loadProfileFragment() {
        updateTitle("Profile");
        if (!(getFragmentManager().findFragmentById(R.id.content_frame) instanceof FragmentProfile)) {
            getFragmentManager().beginTransaction().replace(R.id.content_frame, new FragmentProfile()).commit();
        }
    }

    private void loadHomeFragment() {
        updateTitle("Home");
        if (!(getFragmentManager().findFragmentById(R.id.content_frame) instanceof FragmentHome)) {
            getFragmentManager().beginTransaction().replace(R.id.content_frame, new FragmentHome()).commit();
        }
    }

    private void loadSettingFragment() {

        updateTitle("Settings");
        if (!(getFragmentManager().findFragmentById(R.id.content_frame) instanceof FragmentSetting)) {
            getFragmentManager().beginTransaction().replace(R.id.content_frame, new FragmentSetting()).commit();
        }
    }

    private void showPinsIntruder(Boolean isVerified) {
        Intent intent = isVerified ? new Intent(this, EmailVerifyConfirmActivity.class) :
                new Intent(this, EmailVerifyActivity.class);
        startActivity(intent);
    }

    private void showAntiTheft(LockAction setupPinAction) {
        boolean isDeviceLockPasswordSaved = false;
        if (deviceDao.getValue().getThisDevice() != null) {
            isDeviceLockPasswordSaved = deviceDao.getValue().getThisDevice().isAppLockActive();
        }
        if (!isDeviceLockPasswordSaved) {
            Intent intent = new Intent(this, SetupPinActivity.class);
            intent.putExtra(SetupPinViewModel.ACTION_KEY, setupPinAction);
            startActivity(intent);
        } else if (setupPinAction == LockAction.ANTI_THEFT) {
            Intent intent = new Intent(this, AntiTheftActivity.class);
            intent.putExtra(SetupPinViewModel.ACTION_KEY, LockAction.ANTI_THEFT);
            startActivity(intent);
        } else if (setupPinAction == LockAction.APP_LOCK) {
            Intent intent = new Intent(this, AppLockActivity.class);
            intent.putExtra(SetupPinViewModel.ACTION_KEY, LockAction.APP_LOCK);
            startActivity(intent);
        }
    }

    private void loadDashBoardFragment() {

        updateTitle("My DashBoard");
        if (!(getFragmentManager().findFragmentById(R.id.content_frame) instanceof FragmentDashBoard)) {
            getFragmentManager().beginTransaction().replace(R.id.content_frame, new FragmentDashBoard()).commit();
        }
    }

    private void callLogout() {
        if (null != progressDialog && progressDialog.isShowing()) {

        } else {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }


        try {
            String response = AppSharedPrefrence.getString(this, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO);
            ProfileBaseModel profileBaseModel = new ObjectMapper().readValue(response, ProfileBaseModel.class);

            if (profileBaseModel != null) {
                mToken = profileBaseModel.getResponse().getToken();
                mUserId = profileBaseModel.getResponse().getUser().getAccountId();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("account_id", mUserId);

        java.util.Map<String, String> header = new HashMap<>();
        header.put(ApiConstants.AUTH_HEADER_KEY, ApiConstants.AUTH_HEADER_VALUE);
        header.put(ApiConstants.AUTH_HEADER_KEY_SEC, ApiConstants.AUTH_HEADER_KEY_SEC_VALUE);
        header.put(ApiConstants.AUTH_TOKEN_KEY, mToken);

        ApiCall.getInstance().makeDeleteRequest(this, ApiConstants.API_ADDRESS.LOG_OUT.path, params, header, this, 101, mUserId);
    }

    @Override
    public void onResponse(String response, int requestCode) throws IOException {

        JSONObject jsonObject = null;
        if (requestCode == 101) {
            try {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            } catch (IllegalArgumentException e) {
                progressDialog = null;
            } finally {
                progressDialog = null;
            }
            if (response != null) {
                try {
                    jsonObject = new JSONObject(response);

                    if (jsonObject != null) {
                        String message = jsonObject.getString("message");
                        if (!jsonObject.getString("error").equalsIgnoreCase("true")) {
                            AppSharedPrefrence.putString(this, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO, "");
                            showToast(this, message);
                            Intent intent = new Intent(this, LoginRegisterActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            AppSharedPrefrence.putString(this, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO, "");
                            showToast(this, message);
                            Intent intent = new Intent(this, LoginRegisterActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == 108) {
            try {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            } catch (IllegalArgumentException e) {
                progressDialog = null;
            } finally {
                progressDialog = null;
            }

            if (response != null) {
                try {
                    jsonObject = new JSONObject(response);

                    if (jsonObject != null) {


//
                        if (!jsonObject.getString("error").equalsIgnoreCase("true")) {
                            JSONObject inner = jsonObject.getJSONObject("response");
                            JSONObject userObject = inner.getJSONObject("user");
                            String plan = userObject.getString("duration");
                            long plan_exp_time = userObject.getLong("plan_exp_time");
                            String registered_date = userObject.getString("registered_date");
                            long remaining_days = 30;

                            try {
                                Date date = new Date(plan_exp_time * 1000L); // *1000 is to convert seconds to milliseconds
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy");// the format of your date
                                String expiryDate = sdf.format(date);
                                String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
                                SimpleDateFormat fromFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
                                Date _currentDate = fromFormat.parse(currentDateTimeString);
                                String currentDate = sdf.format(_currentDate);


                                Date e_d = sdf.parse(expiryDate);
                                Date c_d = sdf.parse(currentDate);

                                long different = e_d.getTime() - c_d.getTime();

                                long secondsInMilli = 1000;
                                long minutesInMilli = secondsInMilli * 60;
                                long hoursInMilli = minutesInMilli * 60;
                                long daysInMilli = hoursInMilli * 24;
                                remaining_days = 0;
                                remaining_days = different / daysInMilli;

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if (plan.equalsIgnoreCase("free")) {
                                //show subscription popup
                                showSubscriptionDialog(registered_date);
                            } else if (remaining_days < 3 && remaining_days > 0) {
                                // renewal dialog
                                showRenewalDialog();
                            } else if (remaining_days <= 0) {
                                // expiration dialog
                                showSubscriptionExpiresDialog();
                            }

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onError(String error, int errorCode, int requestCode) {
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        } catch (IllegalArgumentException e) {
            progressDialog = null;
        } finally {
            progressDialog = null;
        }
        if (error != null) {
            try {
                if (errorCode == 402) {
                    AppSharedPrefrence.clearAllPrefs(MainActivity.this);
                    Intent login = new Intent(MainActivity.this, ActivityLogin.class);
                    login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(login);
                    finish();
                } else {
                    JSONObject jsonObject = new JSONObject(error);

                    if (jsonObject != null) {
                        String message = jsonObject.getString("message");
                        showToast(this, message);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment uploadType = getFragmentManager().findFragmentById(R.id.content_frame);

        if (uploadType != null) {
            uploadType.onActivityResult(requestCode, resultCode, data);
        } else if ((requestCode == RESULT_ENABLE) && (resultCode == Activity.RESULT_OK)) {
            Log.i("DeviceAdminSample", "Admin enabled!");
            boolean active = deviceManger.isAdminActive(compName);
            if (!active) {
                Intent intent = new Intent(DevicePolicyManager
                        .ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                        compName);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                        "Additional text explaining why this needs to be added.");
                startActivityForResult(intent, RESULT_ENABLE);
            }
        } else if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                if (!Settings.canDrawOverlays(this)) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
                }
            }
        } else {
            Log.i("DeviceAdminSample", "Admin enable FAILED!");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public void showLogOutDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.logout);

        Button okay = (Button) dialog.findViewById(R.id.okay);
        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                MainActivity.navigationView.setCheckedItem(R.id.home);
                MainActivity.updateTitle("Home");

            }
        });


        dialog.show();

    }

    private void getSubscriptionData() {

//        getCountryInfoFromPhone();
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Please wait...");
        progressDialog.show();

        try {
            String response = AppSharedPrefrence.getString(MainActivity.this, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO);
            ProfileBaseModel profileBaseModel = new ObjectMapper().readValue(response, ProfileBaseModel.class);

            if (profileBaseModel != null) {
                mToken = profileBaseModel.getResponse().getToken();
                mUserId = profileBaseModel.getResponse().getUser().getAccountId();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("account_id", mUserId);

//        params.put("account_id", "719109238346");


        java.util.Map<String, String> header = new HashMap<>();
        header.put(ApiConstants.AUTH_HEADER_KEY, ApiConstants.AUTH_HEADER_VALUE);
        header.put(ApiConstants.AUTH_HEADER_KEY_SEC, ApiConstants.AUTH_HEADER_KEY_SEC_VALUE);
        header.put(ApiConstants.AUTH_TOKEN_KEY, mToken);
        // String url = ApiConstants.BASE_URL + ApiConstants.SUBSCRIPTION_INFO + mUserId;
        String url = ApiConstants.BASE_URL + ApiConstants.PROFILE_INFO + mUserId;

        ApiCall.getInstance().makeGetRequestWithUrl(MainActivity.this, url, params, header, this, 108);
    }


    public void showSubscriptionDialog(String registered_date) {


        String currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());

        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
        long elaspsed = 0;
        long remaining_days = 7;
        try {
            Date r_d = simpleDateFormat1.parse(registered_date);
            Date c_d = simpleDateFormat2.parse(currentDateTimeString);

            long different = c_d.getTime() - r_d.getTime();

            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            elaspsed = different / daysInMilli;
            remaining_days = remaining_days - elaspsed;
            if (remaining_days < 0) {
                remaining_days = 0;
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }

        final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.subscription_popup);

        TextView expiration_date = (TextView) dialog.findViewById(R.id.expiration_date);
        expiration_date.setText("0" + remaining_days + " DAYS");

        Button subscribe = (Button) dialog.findViewById(R.id.subscribe);

        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(MainActivity.this, SubscriptionPlanSelection.class));
            }
        });


        dialog.show();

    }


    public void showRenewalDialog() {

        final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.renew_subscription_popup);

        Button subscribe = (Button) dialog.findViewById(R.id.subscribe);

        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(MainActivity.this, SubscriptionPlanSelection.class));
                overridePendingTransition(0, 0);
            }
        });
        dialog.show();

    }

    public void showSubscriptionExpiresDialog() {


        final Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.expired_subscription_popup);


        Button subscribe = (Button) dialog.findViewById(R.id.subscribe);

        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                startActivity(new Intent(MainActivity.this, SubscriptionPlanSelection.class));
                overridePendingTransition(0, 0);
            }
        });

        dialog.show();
    }

}


