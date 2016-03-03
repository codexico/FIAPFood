package com.example.fk.fiapfood.dao;

import android.content.Context;
import android.util.Log;

import com.example.fk.fiapfood.helper.Helper;
import com.example.fk.fiapfood.model.Restaurant;

import io.realm.Realm;

public class RestaurantDAO {

    private static final String TAG = "FIAPFOOOOOOOOOOOOOOODAO";

    private final Realm realm;

    public RestaurantDAO(Context context) {
        realm = Realm.getInstance(context);
    }


    public Boolean save(Restaurant newRestaurant) {
        Helper.logMethodName(TAG, new Object() {
        });

        Boolean result = false;
        try {
            realm.beginTransaction();
            realm.copyToRealm(newRestaurant);
            realm.commitTransaction();
            result = true;
        } catch (Exception e) {
            Log.w(TAG, "Realm Error");
            e.printStackTrace();
            realm.cancelTransaction();
        }
        return result;
    }

    public Boolean update(Restaurant restaurant) {
        Helper.logMethodName(TAG, new Object() {
        });

        Boolean result = false;
        try {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(restaurant);
            realm.commitTransaction();
            result = true;
        } catch (Exception e) {
            Log.w(TAG, "Realm Error");
            e.printStackTrace();
            realm.cancelTransaction();
        }
        return result;
    }

    public Boolean delete(Restaurant restaurant) {
        Boolean result = false;
        try {
            realm.beginTransaction();
            restaurant.removeFromRealm();
            realm.commitTransaction();
            result = true;
        } catch (Exception e) {
            Log.w(TAG, "Realm Error");
            e.printStackTrace();
            realm.cancelTransaction();
        }

        return result;
    }

}
