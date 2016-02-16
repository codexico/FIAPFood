package com.example.fk.fiapfood;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.fk.fiapfood.adapter.RestaurantAdapter;
import com.example.fk.fiapfood.model.Restaurant;

import java.util.List;

import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmQuery;

public class ResultsActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Realm realm;

    private int type;
    private int min;
    private int max;
    private String name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        ButterKnife.bind(this);

        Intent intent = getIntent();

        name = intent.getStringExtra("name");

        String i_min = intent.getStringExtra("min");
        if (i_min != null && !i_min.isEmpty()) {
            min = Integer.parseInt(i_min);
        } else {
            min = -1;
        }

        String i_max = intent.getStringExtra("max");
        if (i_max != null && !i_max.isEmpty()) {
            max = Integer.parseInt(i_max);
        } else {
            max = -1;
        }

        type = intent.getIntExtra("type", -1);


        mRecyclerView = (RecyclerView) findViewById(R.id.rv_main_list);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        realm = Realm.getInstance(this);

//        mAdapter = new RestaurantAdapter(realm.where(Restaurant.class).findAll());
        mAdapter = new RestaurantAdapter(searchRestaurants());
        mRecyclerView.setAdapter(mAdapter);
    }

    private List<Restaurant> searchRestaurants() {
        RealmQuery<Restaurant> query = realm.where(Restaurant.class);

        Log.w("name", name);
        Log.w("min", String.valueOf(min));
        Log.w("max", String.valueOf(max));
        Log.w("type", String.valueOf(type));

        if (name != null) {
            query.contains("name", name);
        }

        if (min > 0) {
            query.greaterThan("price", min);
        }

        if (max > 0) {
            query.lessThan("price", max);
        }

        if (type > 0) {
            query.equalTo("type", type);
        }


        return query.findAll();
    }

}
