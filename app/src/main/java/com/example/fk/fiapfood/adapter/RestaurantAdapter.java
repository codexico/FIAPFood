 package com.example.fk.fiapfood.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.fk.fiapfood.MainListActivity;
import com.example.fk.fiapfood.R;
import com.example.fk.fiapfood.RestaurantAddActivity;
import com.example.fk.fiapfood.RestaurantEditActivity;
import com.example.fk.fiapfood.helper.Helper;
import com.example.fk.fiapfood.model.Restaurant;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.ViewHolder> {

    protected static final String TAG = "FIAPFOOOOOOOOOODADAPTER";

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public IMyViewHolderClicks mListener;

        public ImageView iv_photo;
        public TextView tv_name;
        public TextView tv_price;
        public TextView tv_phone;

        public ViewHolder(View itemView, IMyViewHolderClicks listener) {
            super(itemView);

            mListener = listener;

            iv_photo = (ImageView) itemView.findViewById(R.id.iv_photo);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_price = (TextView) itemView.findViewById(R.id.tv_price);
            tv_phone = (TextView) itemView.findViewById(R.id.tv_phone);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Helper.logMethodName(TAG, new Object() {
            });

            mListener.onClickRestaurant(v, getAdapterPosition());
        }
        public static interface IMyViewHolderClicks {
            public void onClickRestaurant(View caller, int adapterPosition);
        }
    }

    private final List<Restaurant> restaurantList;

    public RestaurantAdapter(List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
    }


    private void goToRestaurantItemActivity(Context context, int position) {
        Intent i = new Intent(context, RestaurantEditActivity.class);
        i.putExtra("restaurant", position);
        context.startActivity(i);
    }


    @Override
    public RestaurantAdapter.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        Helper.logMethodName(TAG, new Object() {
        });

        final View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_restaurant, parent, false);

        RestaurantAdapter.ViewHolder vh = new ViewHolder(v,
                new RestaurantAdapter.ViewHolder.IMyViewHolderClicks() {
            public void onClickRestaurant(View caller, int position) {
                goToRestaurantItemActivity(parent.getContext(), position);
            };
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Helper.logMethodName(TAG, new Object() {
        });

        Restaurant restaurant = restaurantList.get(position);

        if (restaurant.getName() != null) {
            holder.tv_name.setText(restaurant.getName());
        }
        if (String.valueOf(restaurant.getPrice()) != null) {
            holder.tv_price.setText(String.valueOf(restaurant.getPrice()));
        }
        if (String.valueOf(restaurant.getPhone()) != null) {
            holder.tv_phone.setText(String.valueOf(restaurant.getPhone()));
        }

        if (restaurant.getImageUrl() != null) {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();

                // prevent OutOfMemory for many large images
                options.inSampleSize = 8;

                final Bitmap bitmap = BitmapFactory.decodeFile(restaurant.getImageUrl(), options);

                holder.iv_photo.setImageBitmap(bitmap);

            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getItemCount() {
        Helper.logMethodName(TAG, new Object() {
        });

        return restaurantList.size();
    }

}
