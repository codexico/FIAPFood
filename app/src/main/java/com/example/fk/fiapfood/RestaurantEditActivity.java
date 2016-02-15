package com.example.fk.fiapfood;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;

public class RestaurantEditActivity extends AppCompatActivity implements OnMapReadyCallback {

    protected static final String TAG = "FIAPFOOOOOOOOOOOOODEDIT";

    private Realm realm;
    private int oldRestaurantPosition;
    private Restaurant restaurant;


    @Bind(R.id.etName) EditText etName;
    @Bind(R.id.etPhone) EditText etPhone;
    @Bind(R.id.rgType) RadioGroup rgType;
    @Bind(R.id.etPrice) EditText etPrice;
    @Bind(R.id.etObservation) EditText etObservation;

    @Bind(R.id.radio_rodizio) RadioButton radio_rodizio;
    @Bind(R.id.radio_fast_food) RadioButton radio_fast_food;
    @Bind(R.id.radio_delivery) RadioButton radio_delivery;
    @Bind(R.id.radio_undefined) RadioButton radio_undefined;


    //////////////
    // photo vars
    //////////////
    private Uri fileUri;

    @Bind(R.id.ivPhoto) ImageView ivPhoto;

    static final int REQUEST_TAKE_PHOTO = 11;
    private static final String IMAGE_DIRECTORY_NAME = "FiapFood";

    //////////////
    // google map vars
    //////////////
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Helper.logMethodName(new Object() {
        });

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        Intent intent = getIntent();
        oldRestaurantPosition = intent.getIntExtra("restaurant", 0);

        realm = Realm.getInstance(this);

        restaurant = realm.where(Restaurant.class).findAll().get(oldRestaurantPosition);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapMini);
        mapFragment.getMapAsync(this);

        previewSavedImage();

        etName.setText(restaurant.getName());
        etPhone.setText(restaurant.getPhone());
        etPrice.setText(String.valueOf(restaurant.getPrice()));
        etObservation.setText(restaurant.getObservation());
        rgType.check(restaurant.getType());
    }



    //////////////
    // google map
    //////////////
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



    //////////////
    // photo
    //////////////
    @OnClick(R.id.btTakePhoto)
    public void onClickTakePhoto(View view) {
        Helper.logMethodName(new Object() {
        });

        dispatchTakePictureIntent();
    }

    private void dispatchTakePictureIntent() {
        Helper.logMethodName(new Object() {
        });

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));

                fileUri = Uri.fromFile(photoFile);

                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    private File createImageFile() throws IOException {
        Helper.logMethodName(new Object() {
        });

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

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
        Helper.logMethodName(new Object() {
        });

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            Log.w(TAG, "INTENT READY");
            previewCapturedImage();
        }
    }

    private void previewCapturedImage() {
        Helper.logMethodName(new Object() {
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

    private void previewSavedImage() {
        Helper.logMethodName(new Object() {
        });

        Log.w(TAG, restaurant.getImageUrl());

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();

            // prevent OutOfMemory for many large images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(restaurant.getImageUrl(), options);

            ivPhoto.setImageBitmap(bitmap);

        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

}

