package com.example.fk.fiapfood;

import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MapDialog extends DialogFragment {

    static MapDialog newInstance() {
        return new MapDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_dialog, container, false);

        getDialog().setTitle("Edit Location");

        return v;
    }
}
