package com.example.fk.fiapfood;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.fk.fiapfood.adapter.RestaurantAdapter;
import com.example.fk.fiapfood.helper.Helper;
import com.example.fk.fiapfood.model.Restaurant;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;

public class MainListActivity extends NavigationDrawerActivity {

    protected static final String TAG = "FIAPFOOOOOOOOOOOOODLIST";

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Realm realm;

    // useful when developing
    // drop database if migration is needed
    private Realm getRealm(Context context){
        Helper.logMethodName(new Object() {
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

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_main_list);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

//        realm = Realm.getInstance(this);
        realm = getRealm(this);

        mAdapter = new RestaurantAdapter(realm.where(Restaurant.class).findAll());
        mRecyclerView.setAdapter(mAdapter);
    }

    private void goToAddStuffActivity() {
        Intent i = new Intent(MainListActivity.this, RestaurantAddActivity.class);
        startActivity(i);
    }

}
