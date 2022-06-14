package com.pins.infinity.activity;

import android.location.Address;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.pins.infinity.R;
import com.pins.infinity.database.SettingsManager;
import com.pins.infinity.utility.Utility;

import kotlin.Lazy;

import static org.koin.java.KoinJavaComponent.inject;

/**
 * Created by bimalchawla on 26/7/17.
 */

public class ActivityGoogleMap extends AppCompatActivity implements OnMapReadyCallback {

    public Lazy<SettingsManager> settings = inject(SettingsManager.class);

    Marker marker;
    MapView mapView;
    GoogleMap map;
    PlaceAutocompleteFragment placeAutoComplete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_google_demo);

        placeAutoComplete = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete);
        placeAutoComplete.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                Log.d("Maps", "Place selected: " + place.getName());

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 17);
                map.animateCamera(cameraUpdate);
            }

            @Override
            public void onError(Status status) {
                Log.d("Maps", "An error occurred: " + status);
            }
        });

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(this);
    }


    // Include the OnCreate() method here too, as described above.
    @Override
    public void onMapReady(GoogleMap googleMap) {

        map = googleMap;
        map.getUiSettings().setMapToolbarEnabled(true);
        map.getUiSettings().setAllGesturesEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.getUiSettings().setZoomControlsEnabled(false);
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng location = null;

        String savedLatitude = settings.getValue().getLatitude();
        String savedLongitude = settings.getValue().getLongitude();

        if (Utility.checkString(savedLatitude) && Utility.checkString(savedLongitude)) {
            Double latitude = Double.parseDouble(savedLatitude);
            Double longitude = Double.parseDouble(savedLongitude);
            location = new LatLng(latitude, longitude);
        } else {
            location = new LatLng(0.0, 0.0);
        }

        Address address = Utility.getCompleteAddressString(this, location.latitude, location.longitude);
        String addressName = null;

        if (address != null) {
            addressName = address.getAddressLine(0);
        }

        placeAutoComplete.setText(addressName == null ? "" : addressName);

        MarkerOptions markerOptions = new MarkerOptions().position(location);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.pin_map));
        marker = map.addMarker(markerOptions);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(location, 17);
        map.moveCamera(cameraUpdate);
        marker.showInfoWindow();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

}