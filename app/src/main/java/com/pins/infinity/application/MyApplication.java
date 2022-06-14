package com.pins.infinity.application;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import androidx.multidex.MultiDex;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.androidhiddencamera.HiddenCameraUtils;
import com.androidhiddencamera.config.CameraImageFormat;
import com.avl.engine.AVLEngine;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ikarussecurity.android.theftprotection.IkarusSimChangeDetector;
import com.ikarussecurity.android.utils.IkarusLog;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import com.pins.infinity.BuildConfig;
import com.pins.infinity.activity.ActivityLogin;
import com.pins.infinity.activity.MainActivity;
import com.pins.infinity.api.usermodels.DeviceStatus;
import com.pins.infinity.application.ikarus.InfectionNotifier;
import com.pins.infinity.application.ikarus.MaliciousWebPageNotifier;
import com.pins.infinity.application.ikarus.SimChangeNotifier;
import com.pins.infinity.broadcast.NetworkStateReceiver;
import com.pins.infinity.database.daos.DeviceDao;
import com.pins.infinity.interfaces.ApiResponseListener;
import com.pins.infinity.model.ProfileBaseModel;
import com.pins.infinity.services.BackgroundService;
import com.pins.infinity.services.WhatsappCheckService;
import com.pins.infinity.utility.ApiCall;
import com.pins.infinity.utility.ApiConstants;
import com.pins.infinity.utility.AppConstants;
import com.pins.infinity.utility.AppSharedPrefrence;
import com.pins.infinity.utility.Logger;
import com.pins.infinity.utility.ServiceUtils;
import com.pins.infinity.utility.SharedPreferences.Const;
import com.pins.infinity.utility.Utility;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import co.paystack.android.PaystackSdk;
import io.realm.Realm;
import kotlin.Lazy;

import static org.koin.java.KoinJavaComponent.inject;


public class MyApplication extends BaseMyApplication {
    public static Realm realm;
    private static MyApplication mApplicationInsatnce;
    private RequestQueue mRequestQueue;
    private String TAG = "PIN";
    private static int countDown = 3;

    public static Lazy<DeviceDao> deviceDao = inject(DeviceDao.class);

    private InfectionNotifier mInfectionNotifier = null;
    private MaliciousWebPageNotifier mMaliciousWebPageNotifier = null;
    private SimChangeNotifier mSimChangeNotifier = null;

    private static final String TAG_ADDRESS = "address_to_shared";

    public static MyApplication getInstance() {
        return mApplicationInsatnce;
    }

    public static void onAddressUpdate(Location location) {
        new Retrievedata(location).execute();
    }

    public static void addressFetch(Location location) {
        boolean isStolen = false;
        try {
            isStolen = deviceDao.getValue().getDevice().blockingGet().getDeviceStatus().equals(DeviceStatus.Stolen.getValue());
        } catch (Exception e) {
            Log.e(DeviceStatus.class.getName(), e.toString());
            System.out.println(e.toString());
        }
        if (isStolen) {
            useGeocoding(location);
        } else {
            Address address = Utility.getCompleteAddressString(mApplicationInsatnce.getApplicationContext(), location.getLatitude(), location.getLongitude());
            if (address != null) {
                String city = address.getLocality();
                String code = address.getCountryCode();
                String country = address.getCountryName();
                saveAddressToSharedPreferences(address.getAddressLine(0), city, code, country);
            } else {
                saveAddressToSharedPreferences("-", "-", "-", "-");
            }
        }
    }

    private static void useGeocoding(Location location) {
        try {
            String address = URLEncoder.encode(String.valueOf(location.getLatitude()) + "," + String.valueOf(location.getLongitude()), "UTF-8");

            HttpGet httpGet = new HttpGet(
                    "https://maps.google.com/maps/api/geocode/json?latlng="
                            + address + "&key=AIzaSyC8k6vyg2KWTs0ljseZrbEjyeiURdXics0&location_type=ROOFTOP");
            System.out.println("url to call   https://maps.google.com/maps/api/geocode/json?latlng="
                    + address + "&key=AIzaSyC8k6vyg2KWTs0ljseZrbEjyeiURdXics0&location_type=ROOFTOP");
            HttpClient client = new DefaultHttpClient();
            HttpResponse response;
            StringBuilder stringBuilder = new StringBuilder();

            try {
                response = client.execute(httpGet);
                HttpEntity entity = response.getEntity();
                InputStream stream = entity.getContent();
                int b;
                while ((b = stream.read()) != -1) {
                    stringBuilder.append((char) b);
                }
            } catch (ClientProtocolException e) {
            } catch (IOException e) {
            }

            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            JSONArray array = new JSONArray(jsonObject.getString("results"));

            JSONObject object = array.getJSONObject(0);
            System.out.println("second json response   " + object.toString());
            AppConstants.fullAddress = object.optString("formatted_address");

            JSONArray arrayList = object.optJSONArray("address_components");
            AppConstants.city = arrayList.getJSONObject(3).optString("short_name");
            AppConstants.countryCode = arrayList.getJSONObject(6).optString("short_name");
            AppConstants.countryName = arrayList.getJSONObject(6).optString("long_name");
            System.out.println("address" + AppConstants.fullAddress + "   " + AppConstants.countryCode + "   " + AppConstants.countryName);

            saveAddressToSharedPreferences(AppConstants.fullAddress, AppConstants.city, AppConstants.countryCode, AppConstants.countryName);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveAddressToSharedPreferences(String fullAddress, String city, String countryCode, String countryName) {
        SharedPreferences.Editor editor = MyApplication.getInstance().getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE).edit();
        editor.putString(Const.ADDRESS, fullAddress);
        editor.putString(Const.CITY, city);
        editor.putString(Const.COUNTRY_CODE, countryCode);
        editor.putString(Const.COUNTRY_NAME, countryName);

        Log.d(TAG_ADDRESS, city + countryCode + countryName);

        editor.apply();
        System.out.println("address to shared preferences");
    }

    private static File generateLocationFile() {
        String dirString = android.os.Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "LOCATIONS";
        File dir = new File(dirString);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = "locations.txt";
        return new File(dir, fileName);
    }

    private static void writeToFile(File file, String logs) {
        try {
            String format = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
            FileOutputStream stream = new FileOutputStream(file, true);
            PrintWriter writer = new PrintWriter(stream);
            writer.print(format + " ");
            writer.println(logs);
            writer.flush();
            writer.close();
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /* timer to fire pusher channel to location change intimation on server */
    private static void callAsynchronousTask() {
        try {
            if (null != AppConstants.newLocation && countDown < 1) {

                AppConstants.oldLocation = AppConstants.newLocation;

                String token = "";
                String imei = Utility.getImei(mApplicationInsatnce);
                String response = AppSharedPrefrence.getString(mApplicationInsatnce, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO);
                String fullAddress = AppSharedPrefrence.getString(mApplicationInsatnce, Const.ADDRESS);
                if (null != response) {
                    ProfileBaseModel profileBaseModel = new ObjectMapper().readValue(response, ProfileBaseModel.class);

                    if (profileBaseModel != null) {
                        token = profileBaseModel.getResponse().getToken();
                    }

                    HashMap<String, String> params = new HashMap<>();
                    params.put("device_latitude", String.valueOf(AppConstants.newLocation.getLatitude()));
                    params.put("device_longitude", String.valueOf(AppConstants.newLocation.getLongitude()));
                    params.put("device_country_code", AppConstants.countryCode);
                    params.put("device_country", AppConstants.countryName);
                    params.put("device_location", AppConstants.city);
                    params.put("device_address", fullAddress);

                    Map<String, String> header = new HashMap<>();
                    header.put(ApiConstants.AUTH_HEADER_KEY, ApiConstants.AUTH_HEADER_VALUE);
                    header.put(ApiConstants.AUTH_HEADER_KEY_SEC, ApiConstants.AUTH_HEADER_KEY_SEC_VALUE);
                    header.put(ApiConstants.AUTH_TOKEN_KEY, token);

                    File file = generateLocationFile();

                    ApiCall.getInstance().makePutRequest(ApiConstants.API_ADDRESS.LOCATION_UPDATE.path, params, header, new ApiResponseListener() {
                        @Override
                        public void onResponse(String response, int requestCode) throws IOException {
                            if (response != null) {
                                try {
                                    JSONObject jsonObject = new JSONObject(response);
                                    String address = jsonObject.optJSONObject("response").optJSONObject("device").optString("device_address");

                                    if (jsonObject != null) {
//                                        In case logging synced location to a file is needed
//                                        writeToFile(file, address);
//                                        Toast.makeText(getInstance(), address, Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        @Override
                        public void onError(String error, int errorCode, int requestCode) {
                            Logger.log("error location sending");
                            if (error != null) {
                                try {
                                    if (errorCode == 402) {
                                        AppSharedPrefrence.clearAllPrefs(mApplicationInsatnce);
                                        Intent login = new Intent(mApplicationInsatnce, ActivityLogin.class);
                                        login.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        mApplicationInsatnce.startActivity(login);
                                    } else {
                                        JSONObject jsonObject = new JSONObject(error);

                                        if (jsonObject != null) {
                                            String message = jsonObject.getString("message");
                                            Utility.showToast(mApplicationInsatnce, message);

                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }, 101, imei);
                }
            } else {
                countDown--;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        AppCenter.start(this, BuildConfig.APP_CENTER_SECRET, Analytics.class, Crashes.class);

        PaystackSdk.initialize(getApplicationContext());
        mApplicationInsatnce = this;

        initializeIkarus();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            registerReceiver(new NetworkStateReceiver(),
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }

        ServiceUtils.startForegroundService(new Intent(this, BackgroundService.class), this);

        // Obtain realm instance
        realm.init(this);
        realm = Realm.getDefaultInstance();
        System.out.println(" antivirus checking");
        int result = AVLEngine.init(this);

        if (result == 0) {
            System.out.println(" antivirus started");
            AVLEngine.setLanguage(this, AVLEngine.LANGUAGE_ENGLISH);
        } else {
            System.out.println(" antivirus failure");
        }
        System.out.println(" antivirus check finished");
    }

    private void initializeIkarus() {
        //TODO : Disable Ikarus because it slows down testing. Uncomment when release
        IkarusLog.setImplementation(IkarusLog.DEFAULT_IMPLEMENTATION);

//        IkarusMalwareDetection.initialize(this);
        IkarusSimChangeDetector.initialize(this);
//        IkarusAppLaunchDetector.initialize(this);
//        IkarusProcessRestarter.enable(this, true);

//        mInfectionNotifier = new InfectionNotifier(this);
//        mMaliciousWebPageNotifier = new MaliciousWebPageNotifier(this);
        mSimChangeNotifier = new SimChangeNotifier(this);

//        IkarusMalwareDetection.registerScanListener(mInfectionNotifier);
//        IkarusMalwareDetection.registerWebFilteringListener(mMaliciousWebPageNotifier);
        IkarusSimChangeDetector.registerListener(mSimChangeNotifier);
        IkarusSimChangeDetector.enable(true);

//        IkarusSmsBlacklist.initialize(this);
//        IkarusRemoteControl.initialize(this);

    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(this);
        }
        return mRequestQueue;
    }

    public void canclePendingRequest(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    public <T> void addToRequestQueue(Request<T> request) {
        request.setTag(TAG);
        getRequestQueue().add(request);
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        System.gc();
    }

    private static class Retrievedata extends AsyncTask<String, Void, String> {
        Location location;

        Retrievedata(Location location) {
            this.location = location;
        }

        @Override
        protected String doInBackground(String... params) {
            addressFetch(location);
            AppConstants.newLocation = location;
            callAsynchronousTask();
            return null;
        }
    }
}