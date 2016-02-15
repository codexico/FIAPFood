package com.example.fk.fiapfood;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.example.fk.fiapfood.adapter.RestaurantAdapter;
import com.example.fk.fiapfood.model.Restaurant;

import io.realm.Realm;

public class MainListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Realm realm;

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

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_main_list);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        realm = Realm.getInstance(this);

        mAdapter = new RestaurantAdapter(realm.where(Restaurant.class).findAll());
        mRecyclerView.setAdapter(mAdapter);
    }

    private void goToAddStuffActivity() {
        Intent i = new Intent(MainListActivity.this, RestaurantAddActivity.class);
        startActivity(i);
    }

}
