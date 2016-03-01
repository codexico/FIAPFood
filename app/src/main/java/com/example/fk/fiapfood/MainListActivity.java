package com.example.fk.fiapfood;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.fk.fiapfood.adapter.RestaurantAdapter;
import com.example.fk.fiapfood.helper.Helper;
import com.example.fk.fiapfood.model.Restaurant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.exceptions.RealmMigrationNeededException;

public class MainListActivity extends NavigationDrawerActivity {

    private static final String TAG = "FIAPFOOOOOOOOOOOOODLIST";

    private final String INITIALDATA_PREFS = "INITIALDATA_PREFS";
    private final String ISLOADED = "ISLOADED";

    private Realm realm;

    // useful when developing
    // drop database if migration is needed
    private Realm getRealm(Context context){
        Helper.logMethodName(TAG, new Object() {
        });

        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(context).build();

        try {
            return Realm.getInstance(realmConfiguration);
        } catch (RealmMigrationNeededException e){
            try {
                Log.w(TAG, "drop database");
                realmConfiguration = new RealmConfiguration.Builder(context)
                        .deleteRealmIfMigrationNeeded()
                        .build();
                // Delete database and build a new one
                return Realm.getInstance(realmConfiguration);
            } catch (Exception ex) {
                ex.printStackTrace();
                throw ex;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Helper.logMethodName(TAG, new Object() {
        });

        setContentView(R.layout.activity_main_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToAddStuffActivity();
            }
        });

        // connect NavigationDrawerActivity actions
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rv_main_list);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

//        realm = Realm.getInstance(this);
        realm = getRealm(this);

        Log.w(TAG, "ISLOADED: " + isLoaded());
        checkInitialData();

        RecyclerView.Adapter mAdapter = new RestaurantAdapter(realm.where(Restaurant.class).findAll());
        mRecyclerView.setAdapter(mAdapter);
    }

    private void checkInitialData() {
        Helper.logMethodName(TAG, new Object() {
        });

        if (!isLoaded()) {
            loadInitialData();
        }
    }

    private void loadInitialData() {
        Helper.logMethodName(TAG, new Object() {
        });

        String stringUrl = "http://heiderlopes.com.br/restaurantes/restaurantes.json";
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            new DownloadWebpageTask().execute(stringUrl);
        } else {
//            textView.setText("No network connection available.");
        }
    }
    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadWebpageTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            Helper.logMethodName(TAG, new Object() {
            });

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Helper.logMethodName(TAG, new Object() {
            });

            Log.w(TAG, result);
            JSONArray a = null;
            try {
                a = new JSONArray(result);
                for(int i=0; i < a.length(); i++) {
                    JSONObject restaurantJson = a.getJSONObject(i);

                    realm.beginTransaction();

                    Restaurant restaurant = realm.createObject(Restaurant.class);

                    restaurant.setName(restaurantJson.optString("NOMERESTAURANTE").toString());
                    restaurant.setPhone(restaurantJson.optString("TELEFONE").toString());

                    // TODO: DRY
                    int type = 0; // não sei, indefinido
                    switch(restaurantJson.optString("TIPO").toString()) {
                        case "rodizio":
                            type = 1;
                            break;
                        case "fast food":
                            type = 2;
                            break;
                        case "domicilio":
                            type = 3;
                            break;
                    }

                    restaurant.setType(type);

                    restaurant.setPrice(restaurantJson.optInt("CustoMedio"));
                    restaurant.setObservation(
                            restaurantJson.optString("OBSERVACAO").toString());
                    String[] latLon = restaurantJson.optString("LOCALIZACAO").toString().split(",");
                    restaurant.setLatitude(Double.parseDouble(latLon[0]));
                    restaurant.setLongitude(Double.parseDouble(latLon[1]));

                    restaurant.setImageUrl("");

                    realm.commitTransaction();
                }


                RealmResults<Restaurant> restaurants = realm.where(Restaurant.class).findAll();

                if (restaurants.size() >= 3) {
                    SharedPreferences sp = getSharedPreferences(INITIALDATA_PREFS, MODE_PRIVATE);
                    Editor e = sp.edit();
                    e.putBoolean(ISLOADED, true);
                    // Consider using apply() instead; commit writes its data to persistent storage
                    // immediately, whereas apply will handle it in the background
                    // e.commit();
                    e.apply();
                    Log.w(TAG, "ISLOADED: " + isLoaded());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    // Given a URL, establishes an HttpUrlConnection and retrieves
// the web page content as a InputStream, which it returns as
// a string.
    private String downloadUrl(String myurl) throws IOException {
        Helper.logMethodName(TAG, new Object() {
        });

        InputStream is = null;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.w(TAG, "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            return readIt(is);

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }
    // Reads an InputStream and converts it to a String.
    private String readIt(InputStream stream) throws IOException {
        Helper.logMethodName(TAG, new Object() {
        });

        Reader reader = new InputStreamReader(stream, "UTF-8");

        String str = "";
        int v = reader.read(); // lê byte a byte
        while (v!= -1) {
            str += (char) v; // converte cada byte em char e concatena na String
            v = reader.read();
        }
        Log.w(TAG, str);
        return str;
    }

    private boolean isLoaded() {
        Helper.logMethodName(TAG, new Object() {
        });

        SharedPreferences initialData = getSharedPreferences(INITIALDATA_PREFS, MODE_PRIVATE);
        return initialData.getBoolean(ISLOADED, false);
    }

    private void goToAddStuffActivity() {
        Helper.logMethodName(TAG, new Object() {
        });

        Intent i = new Intent(MainListActivity.this, RestaurantAddActivity.class);
        startActivity(i);
    }

}
