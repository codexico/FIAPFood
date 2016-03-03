package com.example.fk.fiapfood;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.fk.fiapfood.helper.Helper;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SearchActivity extends NavigationDrawerActivity {

    private static final String TAG = "FIAPFOOOOOOOOOOODSEARCH";

    @Bind(R.id.etSearchName) EditText etName;
    @Bind(R.id.rgSearchType) RadioGroup rgType;
    @Bind(R.id.etSearchMinPrice) EditText etMinPrice;
    @Bind(R.id.etSearchMaxPrice) EditText etMaxPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btSearch)
    public void searchRestaurants() {
        Helper.logMethodName(TAG, new Object() {
        });

        Intent i = new Intent(SearchActivity.this, ResultsActivity.class);

        int checkedRadioButtonId = rgType.getCheckedRadioButtonId();
        RadioButton rbSelected = (RadioButton) rgType.findViewById(checkedRadioButtonId);

        // TODO: DRY
        int type = -1; // any
        switch(rbSelected.getId()) {
            case R.id.radio_search_undefined:
                type = 0;
                break;
            case R.id.radio_search_rodizio:
                type = 1;
                break;
            case R.id.radio_search_fast_food:
                type = 2;
                break;
            case R.id.radio_search_delivery:
                type = 3;
                break;
        }

        i.putExtra("type", type);
        if (!etName.getText().toString().isEmpty()) {
            i.putExtra("name", etName.getText().toString());
        } else {
            i.putExtra("name", "");
        }
        if (!etMinPrice.getText().toString().isEmpty()) {
            i.putExtra("min", etMinPrice.getText().toString());
        } else {
            i.putExtra("min", "");
        }
        if (!etMaxPrice.getText().toString().isEmpty()) {
            i.putExtra("max", etMaxPrice.getText().toString());
        } else {
            i.putExtra("max", "");
        }

        startActivity(i);
    }

}
