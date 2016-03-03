package com.example.fk.fiapfood.dao;

import android.content.Context;
import android.util.Log;

import com.example.fk.fiapfood.helper.Helper;
import com.example.fk.fiapfood.model.Restaurant;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;

public class RestaurantDAO {

    private static final String TAG = "FIAPFOOOOOOOOOOOOOOODAO";

    private Realm realm;

    public RestaurantDAO(Context context) {
        // realm = Realm.getInstance(this);
        realm = getRealm(context);
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


















    //////////////
    // helpers
    //////////////


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

}
