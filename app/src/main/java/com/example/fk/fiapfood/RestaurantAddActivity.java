package com.example.fk.fiapfood;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.fk.fiapfood.dao.RestaurantDAO;
import com.example.fk.fiapfood.helper.Helper;
import com.example.fk.fiapfood.model.Restaurant;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class RestaurantAddActivity extends NavigationDrawerActivity implements
        OnMapReadyCallback,
        ConnectionCallbacks,
        OnConnectionFailedListener,
        LocationListener,
        GoogleMap.OnMarkerDragListener {

    private static final String TAG = "FIAPFOOOOOOOOOOOOOODADD";
    private static final int GET_CENTER_LOCATION = 21;

    @Bind(R.id.etName) EditText etName;
    @Bind(R.id.etPhone) EditText etPhone;
    @Bind(R.id.rgType) RadioGroup rgType;
    @Bind(R.id.etPrice) EditText etPrice;
    @Bind(R.id.etObservation) EditText etObservation;
    @Bind(R.id.nsvAdd) NestedScrollView nsvAdd;

    //////////////
    // google map vars
    //////////////
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private GoogleMap mMap;
    private Location currentLocation;
    private Marker marker;
    private Boolean isLocationSet = false;

    private static final int REQUEST_CHECK_SETTINGS = 43;

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;


    //////////////
    // photo vars
    //////////////
    private Uri fileUri;

    @Bind(R.id.ivPhoto) ImageView ivPhoto;

    private static final int REQUEST_TAKE_PHOTO = 11;
    private static final String IMAGE_DIRECTORY_NAME = "FiapFood";

    //////////////
    // init
    //////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Helper.logMethodName(TAG, new Object() {
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_add);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // connect NavigationDrawerActivity actions
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapMini);
        mapFragment.getMapAsync(this);

        buildGoogleApiClient();

        nsvAdd.setNestedScrollingEnabled(false);

        checkInternet();
    }

    private void checkInternet() {

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            Toast.makeText(RestaurantAddActivity.this,
                    "If you can't see the map please turn on the internet",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void goToMainListActivity() {
        Intent i = new Intent(RestaurantAddActivity.this, MainListActivity.class);
        startActivity(i);
        finish();
    }

    @OnClick(R.id.btSaveRestaurant)
    public void saveRestaurant() {
        Helper.logMethodName(TAG, new Object() {
        });

        Restaurant restaurant = new Restaurant();

        String name = etName.getText().toString();
        if (!name.isEmpty()) {
            restaurant.setName(name);
        } else {
            Toast.makeText(RestaurantAddActivity.this,
                    R.string.empty_name_validation, Toast.LENGTH_SHORT).show();
            return;
        }

        String phone = etPhone.getText().toString();
        if (!phone.isEmpty()) {
            restaurant.setPhone(phone);
        }

        String observation = etObservation.getText().toString();
        if (!observation.isEmpty()) {
            restaurant.setObservation(observation);
        }

        int checkedRadioButtonId = rgType.getCheckedRadioButtonId();
        RadioButton rbSelected = (RadioButton) rgType.findViewById(checkedRadioButtonId);

        // TODO: simplify to not repeat
        int type = 0; // R.id.radio_undefined
        switch (rbSelected.getId()) {
            case R.id.radio_rodizio:
                type = 1;
                break;
            case R.id.radio_fast_food:
                type = 2;
                break;
            case R.id.radio_delivery:
                type = 3;
                break;
        }

        restaurant.setType(type);

        String price = etPrice.getText().toString();
        if (!price.isEmpty()) {
            restaurant.setPrice(Integer.parseInt(etPrice.getText().toString()));
        }

        if (fileUri != null && !fileUri.getPath().isEmpty()) {
            restaurant.setImageUrl(fileUri.getPath());
        } else {
            restaurant.setImageUrl("");
        }

        restaurant.setLatitude(marker.getPosition().latitude);
        restaurant.setLongitude(marker.getPosition().longitude);

        RestaurantDAO dao = new RestaurantDAO(this);
        if (!dao.save(restaurant)) {
            Toast.makeText(RestaurantAddActivity.this, R.string.error_message,
                    Toast.LENGTH_SHORT).show();
        } else {
            goToMainListActivity();
        }
    }


    //////////////
    // google map
    //////////////
    private synchronized void buildGoogleApiClient() {
        Helper.logMethodName(TAG, new Object() {
        });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    private void createLocationRequest() {
        Helper.logMethodName(TAG, new Object() {
        });

        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
//                final LocationSettingsStates states = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.

                        if (ActivityCompat.checkSelfPermission(RestaurantAddActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(RestaurantAddActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }
                        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

                        startLocationUpdates();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    RestaurantAddActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
//                        ...
                        break;
                }
            }
        });
    }


    private void showLocation(Location location) {
        Helper.logMethodName(TAG, new Object() {
        });

        double lat = location.getLatitude();
        double lng = location.getLongitude();
        LatLng here = new LatLng(lat, lng);

        if (marker == null) {
            marker = mMap.addMarker(new MarkerOptions()
                    .position(here)
                    .title("Drag to adjust location")
                    .draggable(true));
        } else {
            marker.setPosition(here);
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLng(here));
        isLocationSet = true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Helper.logMethodName(TAG, new Object() {
        });

        mMap = googleMap;
        mMap.setOnMarkerDragListener(this);
        mMap.getUiSettings().setScrollGesturesEnabled(false);

        if (mGoogleApiClient.isConnected()) {
            showLocation(currentLocation);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Helper.logMethodName(TAG, new Object() {
        });

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        startLocationUpdates();
    }

    /**
     * Requests location updates from the FusedLocationApi.
     */
    private void startLocationUpdates() {
        Helper.logMethodName(TAG, new Object() {
        });

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }



    @Override
    public void onConnectionSuspended(int i) {
        Helper.logMethodName(TAG, new Object() {
        });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Helper.logMethodName(TAG, new Object() {
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        Helper.logMethodName(TAG, new Object() {
        });
        if (!isLocationSet) {
            showLocation(currentLocation);
        }
    }

    @Override
    protected void onStart() {
        Helper.logMethodName(TAG, new Object() {
        });

        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        Helper.logMethodName(TAG, new Object() {
        });

        super.onResume();
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.

//        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
//            startLocationUpdates();
//        }
    }

    @Override
    protected void onPause() {
        Helper.logMethodName(TAG, new Object() {
        });

        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
//        if (mGoogleApiClient.isConnected()) {
//            stopLocationUpdates();
//        }
    }

    @Override
    protected void onStop() {
        Helper.logMethodName(TAG, new Object() {
        });

        mGoogleApiClient.disconnect();

        super.onStop();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Helper.logMethodName(TAG, new Object() {
        });
//        Boolean isMarkerDraged = true;
        nsvAdd.setNestedScrollingEnabled(false);
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        Helper.logMethodName(TAG, new Object() {
        });
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Helper.logMethodName(TAG, new Object() {
        });
        nsvAdd.setNestedScrollingEnabled(true);
    }




    //////////////
    // photo
    //////////////
    @OnClick(R.id.btTakePhoto)
    public void onClickTakePhoto() {
        Helper.logMethodName(TAG, new Object() {
        });

        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Helper.logMethodName(TAG, new Object() {
        });

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile;
            photoFile = createImageFile();
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));

                fileUri = Uri.fromFile(photoFile);

                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    private File createImageFile() {
        Helper.logMethodName(TAG, new Object() {
        });

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK).format(new Date());

        File storageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);

        if (!storageDir.exists()) {
            if (!storageDir.mkdirs()) {
                Log.e(IMAGE_DIRECTORY_NAME, "Error creating dir" + IMAGE_DIRECTORY_NAME);
                return null;
            }
        }

        File imageFile = new File(storageDir.getPath() + File.separator
                + "img_" + timeStamp + ".jpg");

        Log.w(TAG, imageFile.getPath());
        return imageFile;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Helper.logMethodName(TAG, new Object() {
        });

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Log.w(TAG, "INTENT READY");
            previewCapturedImage();
        }

        if (requestCode == GET_CENTER_LOCATION) {
            if (resultCode == RESULT_OK) {

                double lat = data.getDoubleExtra("lat", currentLocation.getLatitude());
                double lon = data.getDoubleExtra("lon", currentLocation.getLongitude());

                LatLng here = new LatLng(lat, lon);

                marker.setPosition(here);
                mMap.moveCamera(CameraUpdateFactory.newLatLng(here));
            }
        }
    }

    private void previewCapturedImage() {
        Helper.logMethodName(TAG, new Object() {
        });

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();

            // prevent OutOfMemory for many large images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(fileUri.getPath(), options);

            ivPhoto.setImageBitmap(bitmap);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }


    @OnClick(R.id.btEditLocation)
    public void editLocation() {
        Intent i = new Intent(RestaurantAddActivity.this, EditLocationMapsActivity.class);

        Bundle b = new Bundle();
        b.putDouble("lat", currentLocation.getLatitude());
        b.putDouble("lon", currentLocation.getLongitude());
        i.putExtras(b);

        startActivityForResult(i, GET_CENTER_LOCATION);
    }
}
