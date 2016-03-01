package com.example.fk.fiapfood;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.fk.fiapfood.helper.Helper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class EditLocationMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "FIAPFOOOOOOOOODLOCATION";

    private GoogleMap mMap;

    private double lat;
    private double lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Helper.logMethodName(TAG, new Object() {
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_location_maps);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Bundle b = getIntent().getExtras();
        lat = b.getDouble("lat");
        lon = b.getDouble("lon");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)  {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.cancel_edit_location) {
            finish();
        }

        if (id == R.id.save_edit_location) {
            LatLng center = mMap.getCameraPosition().target;

            Intent i = new Intent(EditLocationMapsActivity.this, RestaurantEditActivity.class);

            Bundle b = new Bundle();
            b.putDouble("lat", center.latitude);
            b.putDouble("lon", center.longitude);
            i.putExtras(b);

            setResult(RESULT_OK, i);
            finish();
        }

        return super.onOptionsItemSelected(item);
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
        mMap = googleMap;
        LatLng here = new LatLng(lat, lon);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(here));
    }
}
