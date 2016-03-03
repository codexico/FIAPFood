package com.example.fk.fiapfood;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.fk.fiapfood.adapter.RestaurantAdapter;
import com.example.fk.fiapfood.model.Restaurant;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;

public class ResultsActivity extends AppCompatActivity {

    private Realm realm;

    private int type;
    private int min = -1;
    private int max = -1;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.rv_main_list);
        mRecyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        realm = Realm.getInstance(this);

        List<Restaurant> restaurants = searchRestaurants();

        RecyclerView.Adapter mAdapter = new RestaurantAdapter(restaurants);
        mRecyclerView.setAdapter(mAdapter);

        if (restaurants.size() == 0) {
            TextView no_results = (TextView) findViewById(R.id.no_results);
            no_results.setVisibility(TextView.VISIBLE);
        }
    }

    private void buildSearchData() {
        Intent intent = getIntent();

        name = intent.getStringExtra("name");

        String i_min = intent.getStringExtra("min");
        if (!i_min.isEmpty()) {
            min = Integer.parseInt(i_min);
        }

        String i_max = intent.getStringExtra("max");
        if (!i_max.isEmpty()) {
            max = Integer.parseInt(i_max);
        }

        type = intent.getIntExtra("type", -1);
    }

    private List<Restaurant> searchRestaurants() {
        buildSearchData();

        RealmQuery<Restaurant> query = realm.where(Restaurant.class);

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
