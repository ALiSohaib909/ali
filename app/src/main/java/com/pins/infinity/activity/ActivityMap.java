//package com.pins.infinity.activity;
//
//import android.Manifest;
//import android.content.pm.PackageManager;
//import android.graphics.PointF;
//import android.location.Address;
//import android.location.Geocoder;
//import android.os.Bundle;
//import android.support.annotation.NonNull;
//import android.support.v4.app.ActivityCompat;
//import android.support.v4.content.ContextCompat;
//import android.support.v7.app.AppCompatActivity;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.Toast;
//
//import com.crashlytics.android.Crashlytics;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.here.android.mpa.common.GeoPosition;
//import com.here.android.mpa.common.Image;
//import com.here.android.mpa.common.OnEngineInitListener;
//import com.here.android.mpa.common.PositioningManager;
//import com.here.android.mpa.common.ViewObject;
//import com.here.android.mpa.mapping.Map;
//import com.here.android.mpa.mapping.MapGesture;
//import com.here.android.mpa.mapping.MapMarker;
//import com.here.android.mpa.mapping.MapObject;
////import com.here.android.mpa.venues3d.VenueMapFragment;
//import com.pins.infinity.R;
//import com.pins.infinity.model.ProfileBaseModel;
//import com.pins.infinity.utility.AppSharedPrefrence;
//
//import java.io.IOException;
//import java.lang.ref.WeakReference;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Locale;
//
///**
// * Created by bimalchawla on 25/4/17.
// */
//
//public class ActivityMap extends AppCompatActivity {
//
//    // map embedded in the map fragment
//    private Map map = null;
//    // map fragment embedded in this activity
////    private VenueMapFragment mapView = null;
//    private MapMarker marker;
//    private PositioningManager posManager;
//    Image image;
//    private boolean pause;
//    private ImageView back;
//
//    // permissions request code
//    private final static int REQUEST_CODE_ASK_PERMISSIONS = 1;
//
//    /**
//     * Permissions that need to be explicitly requested from end user.
//     */
//    private static final String[] REQUIRED_SDK_PERMISSIONS = new String[]{
//            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.basic_demo);
//
//
//        checkPermissions();
//
//        back = (ImageView)findViewById(R.id.back);
//        back.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//                overridePendingTransition(0,0);
//            }
//        });
//
//        try {
//            String response = AppSharedPrefrence.getString(ActivityMap.this, AppSharedPrefrence.PREF_KEY.USER_PROFILE_INFO);
//            ProfileBaseModel profileBaseModel = new ObjectMapper().readValue(response, ProfileBaseModel.class);
//
//            if (profileBaseModel != null) {
//                // You can call any combination of these three methods
//                Crashlytics.setUserIdentifier(profileBaseModel.getResponse().getUser().getImei());
//                Crashlytics.setUserEmail(profileBaseModel.getResponse().getUser().getEmail());
//            }
//        } catch (IOException e) {
//            System.out.println("error " + Crashlytics.getInstance().getIdentifier());
//            e.printStackTrace();
//        }
////        initialize();
//    }
//
//
//    // Define positioning listener
//    private PositioningManager.OnPositionChangedListener positionListener = new
//            PositioningManager.OnPositionChangedListener() {
//
//                public void onPositionUpdated(PositioningManager.LocationMethod method,
//                                              GeoPosition position, boolean isMapMatched) {
//                    // set the center only when the app is in the foreground
//                    // to reduce CPU consumption
//
//                    try {
//                        if(!pause) {
//                            map.setCenter(position.getCoordinate(),
//                                    Map.Animation.LINEAR);
//
//
//                            if (marker != null) {
//                            map.removeMapObject(marker);
//                        }
//                        System.out.println("PositioningManager.getPosition()" + position.getCoordinate());
//                        marker = new MapMarker(position.getCoordinate(), image);
//
//                        Geocoder geocoder = new Geocoder(ActivityMap.this, Locale.getDefault());
//
//                        List<Address> addresses = geocoder.getFromLocation(position.getCoordinate().getLatitude(), position.getCoordinate().getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//
//                        marker.setTitle(addresses.get(0).getAddressLine(0));
//
//                        marker.setAnchorPoint(new PointF(image.getWidth() / 2, image.getHeight()));
//                        map.addMapObject(marker);
//                        marker.showInfoBubble();
//                        pause = true;
//                    }
//
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                public void onPositionFixChanged(PositioningManager.LocationMethod method,
//                                                 PositioningManager.LocationStatus status) {
//                }
//            };
//
//
//    private void initialize() {
//// Search for the map fragment to finish setup by calling init().
////        mapView = (VenueMapFragment
////                ) getFragmentManager().findFragmentById(
////                R.id.map);
////
////
////        mapView.init(new OnEngineInitListener() {
////            @Override
////            public void onEngineInitializationCompleted(
////                    OnEngineInitListener.Error error) {
////                if (error == OnEngineInitListener.Error.NONE) {
////// retrieve a reference of the map from the map fragment
////                    map = mapView.getMap();
////
////                    image = new Image();
////                    try {
////                        image.setImageResource(R.mipmap.ic_launcher);
////                    } catch (IOException e) {
////                        e.printStackTrace();
////                    }
////                    posManager = PositioningManager.getInstance();
////                    posManager.start(
////                            PositioningManager.LocationMethod.GPS_NETWORK);
////
////                    // Register positioning listener
////                    PositioningManager.getInstance().addListener(
////                            new WeakReference<PositioningManager.OnPositionChangedListener>(positionListener));
////
////
////// Set the zoom level to the average between min and max
////                    map.setZoomLevel(13);
////
////                    mapView.getMapGesture().addOnGestureListener(listener);
////                } else {
////                    System.out.println("ERROR: Cannot initialize Map Fragment");
////                }
////            }
////        });
//
//    }
//
//    // Create a gesture listener and add it to the MapFragment
//    MapGesture.OnGestureListener listener =
//            new MapGesture.OnGestureListener.OnGestureListenerAdapter() {
//                @Override
//                public boolean onMapObjectsSelected(List<ViewObject> objects) {
//                    for (ViewObject viewObj : objects) {
//                        if (viewObj.getBaseType() == ViewObject.Type.USER_OBJECT) {
//                            if (((MapObject) viewObj).getType() == MapObject.Type.MARKER) {
//// At this point we have the originally added
//// map marker, so we can do something with it
//// (like change the visibility, or more
//// marker-specific actions)
//                                if(null != marker) {
//                                    if (!marker.isInfoBubbleVisible()) {
//                                        marker.showInfoBubble();
//                                    } else {
//                                        marker.hideInfoBubble();
//                                    }
//                                }
//                            }
//                        }
//                    }
//// return false to allow the map to handle this callback also
//                    return false;
//                }
//            };
//
////        if(null != AppConstants.fullAddress) {
////           marker = map.addMarker(new MarkerOptions().position(latLng).title(AppConstants.fullAddress));
////        } else {
////           marker = map.addMarker(new MarkerOptions().position(latLng).title("PINS"));
////        }
////        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 13);
////        map.animateCamera(cameraUpdate);
////
////        marker.showInfoWindow();
////    }
//
//    /**
//     * Checks the dynamically controlled permissions and requests missing permissions from end user.
//     */
//    protected void checkPermissions() {
//        final List<String> missingPermissions = new ArrayList<String>();
//        // check all required dynamic permissions
//        for (final String permission : REQUIRED_SDK_PERMISSIONS) {
//            final int result = ContextCompat.checkSelfPermission(this, permission);
//            if (result != PackageManager.PERMISSION_GRANTED) {
//                missingPermissions.add(permission);
//            }
//        }
//        if (!missingPermissions.isEmpty()) {
//            // request all missing permissions
//            final String[] permissions = missingPermissions
//                    .toArray(new String[missingPermissions.size()]);
//            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_ASK_PERMISSIONS);
//        } else {
//            final int[] grantResults = new int[REQUIRED_SDK_PERMISSIONS.length];
//            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED);
//            onRequestPermissionsResult(REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
//                    grantResults);
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[],
//                                           @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case REQUEST_CODE_ASK_PERMISSIONS:
//                for (int index = permissions.length - 1; index >= 0; --index) {
//                    if (grantResults[index] != PackageManager.PERMISSION_GRANTED) {
//                        // exit the app if one permission is not granted
//                        Toast.makeText(this, "Required permission '" + permissions[index]
//                                + "' not granted, exiting", Toast.LENGTH_LONG).show();
//                        finish();
//                        return;
//                    }
//                }
//                // all permissions were granted
//                initialize();
//                break;
//        }
//    }
//    @Override
//    public void onBackPressed() {
//        finish();
//        overridePendingTransition(0,0);
//
//    }
//}