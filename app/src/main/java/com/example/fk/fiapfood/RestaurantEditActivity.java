package com.example.fk.fiapfood;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;

import com.example.fk.fiapfood.helper.Helper;
import com.example.fk.fiapfood.model.Restaurant;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import butterknife.Bind;
import io.realm.Realm;

public class RestaurantEditActivity extends AppCompatActivity implements OnMapReadyCallback {

    private Realm realm;
    private int oldRestaurantPosition;
    private Restaurant restaurant;


    @Bind(R.id.etName) EditText etName;
    @Bind(R.id.etPhone) EditText etPhone;
    @Bind(R.id.rgType) RadioGroup rgType;
    @Bind(R.id.etPrice) EditText etPrice;
    @Bind(R.id.etObservation) EditText etObservation;


    private Uri fileUri;
    @Bind(R.id.ivPhoto) ImageView ivPhoto;

    //////////////
    // google map vars
    //////////////
    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;

    private GoogleMap mMap;
    protected Location restaurantLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent intent = getIntent();
        oldRestaurantPosition = intent.getIntExtra("restaurant", 0);

        realm = Realm.getInstance(this);

        restaurant = realm.where(Restaurant.class).findAll().get(oldRestaurantPosition);

        Log.w("asdfadsfadsf", restaurant.getName());


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapMini);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Helper.logMethodName(new Object() {
        });

        mMap = googleMap;

        double lat = restaurant.getLatitude();
        double lng = restaurant.getLongitude();
        LatLng here = new LatLng(lat, lng);

        mMap.addMarker(new MarkerOptions().position(here).title("I'm here!"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(here));
    }
}
