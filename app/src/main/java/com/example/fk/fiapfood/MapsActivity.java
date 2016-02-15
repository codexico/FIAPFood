package com.example.fk.fiapfood;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.fk.fiapfood.helper.Helper;
import com.example.fk.fiapfood.model.Restaurant;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    protected static final String TAG = "FIAPFOOOOOOOOOOOOOODMAP";

    private Realm realm;

    RealmQuery<Restaurant> restaurantQuery;
    RealmResults<Restaurant> restaurantList;

    private GoogleMap mMap;

    protected void onCreate(Bundle savedInstanceState) {
        Helper.logMethodName(new Object() {
        });
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        realm = Realm.getInstance(this);

        restaurantList = realm.where(Restaurant.class).findAll();
        Log.w(TAG, Integer.toString(restaurantList.size()));
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Helper.logMethodName(new Object() {
        });

        mMap = googleMap;

        addRestaurantsToMap();
    }

    private void addRestaurantsToMap() {
        Helper.logMethodName(new Object() {
        });

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (Restaurant restaurant : restaurantList) {
            double lat = restaurant.getLatitude();
            double lng = restaurant.getLongitude();
            LatLng here = new LatLng(lat, lng);

            builder.include(mMap.addMarker(
                    new MarkerOptions().position(here).title(restaurant.getName()))
                    .getPosition());
        }

        LatLngBounds bounds = builder.build();

        int padding = 0; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        mMap.moveCamera(cu);
    }

}
